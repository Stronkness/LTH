import numpy as np
import matplotlib.pyplot as plt
import scipy.sparse.linalg

import create_problem


class DomainSolver:
    def __init__(self, domain):
        self.domain = domain

        A, b, indicator, shape = create_problem.create_domain(domain)
        self.A = A
        self.b = b
        # indicator > 0 => dirichlet
        # indicator < 0 => neumann
        self.indicator = indicator
        self.abs_indicator = np.abs(indicator)
        # for debugging
        self.shape = shape

        self.inv_h2 = create_problem.tile_size ** 2

        self.omega = 0.8

        # create initial guess
        self.solution = 15 * np.ones(len(b))

    def get_boundary_domains(self):
        return self.boundary_domains

    def get_boundaries(self):
        boundary_values = [
            self.solution[self.abs_indicator == d]
            for d in range(1, create_problem.n_domains + 1)
        ]
        return boundary_values

    def solve(self, boundary_values):
        b = self.b.copy()
        for i in range(create_problem.n_domains):
            if len(boundary_values[i]) == 0:
                continue
            current_indicator = self.abs_indicator == i + 1
            dirichlet_indicator = current_indicator & (self.indicator > 0)
            neumann_indicator = current_indicator & (self.indicator < 0)
            # print(i, np.flatnonzero(dirichlet_indicator), np.flatnonzero(neumann_indicator), boundary_values[i])

            b[dirichlet_indicator] += (
                -boundary_values[i][dirichlet_indicator[current_indicator]]
                * self.inv_h2
            )
            b[neumann_indicator] += (
                self.solution[neumann_indicator]
                - boundary_values[i][neumann_indicator[current_indicator]]
            ) * self.inv_h2
        # print(b.reshape(self.shape))

        # previous_solution is kept for relaxation
        self.previous_solution = self.solution
        self.solution = scipy.sparse.linalg.spsolve(self.A, b)
        return boundary_values

    def relax(self):
        self.solution = (
            self.omega * self.solution + (1 - self.omega) * self.previous_solution
        )


def single_core_solver():
    n_iterations = 10
    world, results = create_problem.get_world()
    domains = [DomainSolver(d) for d in range(1, create_problem.n_domains + 1)]
    for k in range(n_iterations):
        # solve for 1
        _, boundary_01, _ = domains[0].get_boundaries()
        _, boundary_21, _ = domains[2].get_boundaries()
        domains[1].solve([boundary_01, [], boundary_21])
        boundary_10, _, boundary_12 = domains[1].get_boundaries()
        # solve for 0
        domains[0].solve([[], boundary_10, []])
        # solve for 2
        domains[2].solve([[], boundary_12, []])
        for i in range(create_problem.n_domains):
            domains[i].relax()

    for d in range(1, create_problem.n_domains + 1):
        results[world == d] = domains[d - 1].solution
    return results

def multiple_core_solver():
    from mpi4py import MPI
    comm = MPI.Comm.Clone(MPI.COMM_WORLD)
    rank = comm.Get_rank()
    
    n_iterations = 10
    world, results = create_problem.get_world()

    if rank == 0:
        domain = DomainSolver(rank+1)
        
        for i in range(n_iterations):
            _, boundary_01, _ = domain.get_boundaries()
            comm.send(boundary_01, dest=1)
            boundary_10 = comm.recv(source=1)

            domain.solve([[], boundary_10, []])
            domain.relax()

        comm.send(domain.solution, dest=1)
        
        
    elif rank == 1:
        domain = DomainSolver(rank+1)

        for i in range(n_iterations):
            boundary_01 = comm.recv(source=0)
            boundary_21 = comm.recv(source=2)
            
            domain.solve([boundary_01, [], boundary_21])
            boundary_10, _, boundary_12 = domain.get_boundaries()
            
            comm.send(boundary_10, dest=0)
            comm.send(boundary_12, dest=2)

            domain.relax()
        results[world == 2] = domain.solution
        results[world == 1] = comm.recv(source=0)
        results[world == 3] = comm.recv(source=2)
        return results
    elif rank == 2:
        domain = DomainSolver(rank+1)
        
        for i in range(n_iterations):
            _, boundary_21, _ = domain.get_boundaries()
            comm.send(boundary_21, dest=1)
            boundary_12 = comm.recv(source=1)

            domain.solve([[], boundary_12, []])
            domain.relax()
        comm.send(domain.solution, dest=1)



if __name__ == "__main__":
    # results = single_core_solver()
    results = multiple_core_solver()
    if results is not None:
        plt.imshow(results, cmap="inferno")
        plt.title("Results")
        plt.colorbar()
        plt.show()
