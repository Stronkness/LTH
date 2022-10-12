import numpy as np
from scipy import sparse
import matplotlib.pyplot as plt
import matplotlib.ticker as mticker

normal_temp = 15
heater_temp = 40
window_temp = 5

tile_size = 20


def get_A(shape):
    """
    Shape of g must be odd in both dimensions
    to be able to use the cache g should be a tuple
    """
    g = np.array(((0, 1, 0), (1, -4, 1), (0, 1, 0)))

    mid_r, mid_c = (np.array(g.shape) - 1) // 2
    n_diags = np.count_nonzero(g)
    r, c = np.nonzero(g)

    size = np.prod(shape)
    data = np.ones((n_diags, size)) * np.expand_dims(g[g != 0], 1)
    diags = (r - mid_r) * shape[1] + (c - mid_c)

    A = sparse.spdiags(data, diags, size, size).tocsr()

    indexs_to_remove = np.arange(shape[1], np.prod(shape), shape[1])
    A[indexs_to_remove - 1, indexs_to_remove] = 0
    A[indexs_to_remove, indexs_to_remove - 1] = 0
    return A


def get_world(tile_size=tile_size):
    shape = (2 * tile_size - 1, 3 * tile_size - 1)

    world = np.zeros(shape, dtype=int)
    # setting domains
    world[tile_size:, :tile_size] = 1
    world[:, tile_size : 2 * tile_size - 1] = 2
    world[: tile_size - 1, 2 * tile_size - 1 :] = 3

    # adding boundary values
    large_shape = (shape[0] + 2, shape[1] + 2)
    world_large = np.zeros(large_shape, dtype=int)
    world_large[1:-1, 1:-1] = world

    boundary_temperatures = np.zeros(large_shape) * np.nan

    boundary_temperatures[tile_size, 1 : tile_size + 1] = normal_temp
    boundary_temperatures[1 : tile_size + 1, tile_size] = normal_temp
    boundary_temperatures[-1, 1 : tile_size + 1] = normal_temp
    boundary_temperatures[-tile_size - 1, -tile_size - 1 : -1] = normal_temp
    boundary_temperatures[-tile_size - 1 : -1, -tile_size - 1] = normal_temp
    boundary_temperatures[0, -tile_size - 1 : -1] = normal_temp

    boundary_temperatures[tile_size + 1 : -1, 0] = heater_temp
    boundary_temperatures[0, tile_size + 1 : 2 * tile_size] = heater_temp
    boundary_temperatures[1:tile_size, -1] = heater_temp

    boundary_temperatures[-1, tile_size + 1 : 2 * tile_size] = window_temp

    return world_large, boundary_temperatures


def create_domain1(tile_size=tile_size, flipped=False):
    h2 = (1 / tile_size) ** 2
    shape = (tile_size - 1, tile_size)
    A = get_A(shape)
    b = np.zeros(np.prod(shape))

    if flipped:
        left_ind = -1
        right_ind = 0
    else:
        left_ind = 0
        right_ind = -1

    neumann_indicator = np.zeros(shape, dtype=bool)
    neumann_indicator[:, right_ind] = True
    neumann_indicator = neumann_indicator.ravel()
    A[neumann_indicator, neumann_indicator] = -3

    dir_ind_normal = np.zeros(shape, dtype=bool)
    dir_ind_normal[0, :] = True
    dir_ind_normal[-1, :] = True
    dir_ind_normal = dir_ind_normal.ravel()
    b[dir_ind_normal] += -normal_temp / h2

    dir_ind_heater = np.zeros(shape, dtype=bool)
    dir_ind_heater[:, left_ind] = True
    dir_ind_heater = dir_ind_heater.ravel()
    b[dir_ind_heater] += -heater_temp / h2

    indicator = -neumann_indicator.astype(int) * 2
    return A / h2, b, indicator, shape


def create_domain2(tile_size=tile_size, flipped=False):
    inv_h2 = tile_size ** 2
    shape = (2 * tile_size - 1, tile_size - 1)
    A = get_A(shape)
    b = np.zeros(np.prod(shape))

    dir_ind_normal = np.zeros(shape, dtype=bool)
    dir_ind_normal[:tile_size, 0] = True
    dir_ind_normal[tile_size - 1 :, -1] = True
    dir_ind_normal = dir_ind_normal.ravel()
    b[dir_ind_normal] += -normal_temp * inv_h2

    dir_ind_heater = np.zeros(shape, dtype=bool)
    dir_ind_heater[0, :] = True
    dir_ind_heater = dir_ind_heater.ravel()
    b[dir_ind_heater] += -heater_temp * inv_h2

    dir_ind_window = np.zeros(shape, dtype=bool)
    dir_ind_window[-1, :] = True
    dir_ind_window = dir_ind_window.ravel()
    b[dir_ind_window] += -window_temp * inv_h2

    neighbor_indicator = np.zeros(shape, dtype=int)
    neighbor_indicator[tile_size:, 0] = 1
    neighbor_indicator[: tile_size - 1, -1] = 3
    neighbor_indicator = neighbor_indicator.ravel()

    return A * inv_h2, b, neighbor_indicator, shape


def create_domain3(tile_size=tile_size):
    return create_domain1(tile_size, flipped=True)


create_domain_functions = [create_domain1, create_domain2, create_domain3]
n_domains = len(create_domain_functions)


def create_domain(domain, tile_size=tile_size):
    return create_domain_functions[domain - 1](tile_size)


if __name__ == "__main__":
    #create_problem1()

    #comm = MPI.COMM_WORLD

    #rank = comm.Get_rank()

    #if rank == 0 : # Domain 1

    #elif rank == 1 : # Domain 2
    #    A, b, I = create_problem2(size) 
    #    init_temp1 = np.ones(size-1)*21 # Starting temperature 21 in room
    #    init_temp2 = np.ones(size-1)*21

    #    np.zeros(size*(size-1))[]
    #    np.putmask(b, I, b + np.zeros(size*(size-1))[I])


    #elif rank == 2 : # Domain 3

    world, boundary_temperatures = get_world()

    for d in range(1, n_domains + 1):
        # for d in [1]:
        print("Domain", d)
        A, b, indicator, shape = create_domain(d)
        print("shape", shape)
        plt.figure()
        plt.imshow(A.todense())
        print(A.todense())
        print(b.reshape(shape))
        print(indicator.reshape(shape))
        print()

    fig, ax = plt.subplots()
    ax.imshow(world)
    ax.imshow(boundary_temperatures, vmin=-20, cmap="inferno")
    ax.xaxis.set_major_locator(mticker.MaxNLocator(integer=True))
    ax.yaxis.set_major_locator(mticker.MaxNLocator(integer=True))
    ax.set_xticks(np.arange(world.shape[1]), minor=True)
    ax.set_yticks(np.arange(world.shape[0]), minor=True)
    ax.grid("on", which="both")
    plt.show()
