import abc
import numpy as np
import logging

# import numdifftools

logger = logging.getLogger(__name__)


def Gradient(fun, eps=1e-10):
    """
    Numerical evaluation of the gradient of the object function
    at  x.
    """

    def grad_inner(x):
        idx = np.eye(len(x))
        gradf = np.array(
            [
                (fun(x + eps * idx[i]) - fun(x - eps * idx[i])) / 2 / eps
                for i in range(len(x))
            ]
        )
        return gradf

    return grad_inner


def Hessian(fun, eps=1e-5):
    def hess_inner(x):
        idx = np.eye(len(x))
        hess = np.zeros((len(x), len(x)))
        for i in range(len(x)):
            for j in range(len(x)):
                hess[i][j] = (
                    fun(x + eps * (idx[i] + idx[j]))
                    - fun(x + eps * idx[i])
                    - fun(x + eps * idx[j])
                    + fun(x)
                ) / eps ** 2
        hess = (hess + hess.T) / 2
        return hess

    return hess_inner


class Optimization(abc.ABC):
    def __init__(
        self,
        fun,
        x0,
        gradient=None,
        step_tolerance=1e-4,
        gradient_tolerance=1e-4,
        max_iterations=1000,
        sigma=1e-2,
        rho=0.99,
    ):
        self._fun = fun
        if gradient is None:
            gradient = Gradient(fun)
            # gradient = numdifftools.Gradient(fun)
        self.gradient = gradient
        self.hessian = Hessian(fun)
        # self.hessian = numdifftools.Hessian(fun)

        if isinstance(x0, int):
            # x0 is the number of dimensions for function
            x0 = np.zeros(x0)
        # must be self.x so that the setter is executed
        self.x = x0
        self._step_tolerance = step_tolerance
        self._gradient_tolerance = gradient_tolerance
        self._max_iterations = max_iterations
        if sigma <= 0 or sigma >= 0.5:
            raise ValueError("Sigma must be in the range (0, 0.5)")
        self._sigma = sigma
        if rho <= sigma or rho >= 1:
            raise ValueError("Rho must be in the range (sigma, 1)")
        self._rho = rho
        self.n_steps = 0

    @property
    def x(self):
        return self._x

    @x.setter
    def x(self, x):
        self._x = np.array(x)

    @property
    def n_steps(self):
        return self._n_steps

    @n_steps.setter
    def n_steps(self, n_steps):
        self._n_steps = n_steps

    def classical_newton_step(self, hess=None, hess_inv=None):
        current_gradient = self.gradient(self._x).reshape(-1)
        if hess_inv is not None:
            current_step = -hess_inv.dot(current_gradient)
        else:
            if hess is None:
                hess = self.hessian(self._x)
            current_step = -np.linalg.solve(hess, current_gradient)
        return current_step, current_gradient

    def amijo_step_size(self, current_step, base_fi, base_gradient):
        """
        sigma must be in the range (0, 1)
        """
        alpha = 2  # from example
        while (
            self._fun(self._x + alpha * current_step)
            > base_fi + self._sigma * alpha * base_gradient
        ):
            alpha /= 2
        return alpha

    def powell_wolfe_step_size(self, current_step):
        """
        sigma must be in the range [0, 0.5]
        rho must be in the range [sigma, 1]
        """

        def phi(alpha):
            return self._fun(self._x + alpha * current_step)

        phi_gradient_base = Gradient(phi)

        def phi_gradient(alpha):
            return phi_gradient_base(np.array([alpha]))[0]

        ### Calculate fi(0) and fi'(0)
        base_fi = phi(0)
        base_gradient = phi_gradient(0)

        def amijo_rule(alpha):
            return phi(alpha) <= base_fi + self._sigma * alpha * base_gradient

        def wolfe_rule(alpha):
            return phi_gradient(alpha) >= self._rho * base_gradient

        alpha_minus = 1
        while not amijo_rule(alpha_minus):
            alpha_minus /= 2

        alpha_plus = alpha_minus
        while amijo_rule(alpha_plus):
            alpha_plus *= 2

        # While alpha_minus does not fulfill (ii)
        while not wolfe_rule(alpha_minus):
            alpha_o = (alpha_minus + alpha_plus) / 2
            if amijo_rule(alpha_o):
                alpha_minus = alpha_o
            else:
                alpha_plus = alpha_o
        return alpha_minus

    # def fletcher_step_size(self, current_step, current_gradient, alpha_start):
    #     alpha0 = 0
    #     alpha1 = alpha_start # 0 < alpha1 <= mu
    #     f_lim = 0
    #     tau1 = 9 # >1
    #     tau2 = 0.1
    #     tau3 = 0.5

    #     base_fi = self._fun(self._x)
    #     base_gradient = np.sum(current_gradient * current_step) # lutningen i decent direction s (current step)
    #     mu = (f_lim - base_fi)/(self._rho*base_gradient)
    #
    #     ## Bracketing phase ##
    #     while True:
    #         funval = self._fun(self._x + alpha1 * current_step)
    #
    #         if ( funval <= f_lim ) :
    #             return alpha1
    #         if ( (funval > base_fi + alpha1 * base_gradient) or
    #                 (funval >= self._fun(self._x + alpha0 * current_step)) ) :
    #             a = alpha0
    #             b = alpha1
    #             break
    #         grad_at_a1 = np.sum( self.gradient(self._x + alpha1 * current_step) * current_step ) # gradient f in alpha1
    #         if np.abs(grad_at_a1) <= -self._sigma * base_gradient :
    #             return alpha1
    #         if grad_at_a1 >= 0 :
    #             a = alpha1
    #             b = alpha0
    #             break
    #         if mu <= 2*alpha1 - alpha0 :
    #             alpha0 = alpha1
    #             alpha1 = mu
    #         else:
    #             alpha_temp = alpha1
    #             alpha1 = (2*alpha1 - alpha0  +  min(mu, alpha1 + tau1 * (alpha1 - alpha0))) / 2 # This was bit of a cheap solution...
    #             alpha0 = alpha_temp

    #     ## Sectioning ##
    #     while True:
    #         alpha1 = (a + tau2*(b - a) + b - tau3*(b - a)) / 2 # Another cheap one
    #         funval = self._fun(self._x + alpha1*current_step)
    #         if funval > base_fi + self._rho*alpha1*base_gradient :
    #             b = alpha1
    #         elif funval >= self._fun(self._x + a*current_step) :
    #             b = alpha1
    #         else :
    #             grad_at_a1 = np.sum( self.gradient(self._x + alpha1 * current_step) * current_step ) # gradient f in alpha1
    #             if np.abs(grad_at_a1) <= -self._sigma*base_gradient :
    #                 return alpha1
    #             a = alpha1
    #             if (b - a)*grad_at_a1 >= 0 :
    #                 b = a
    #     return alpha1

    @abc.abstractmethod
    def step(self):
        ...

    def stopping_criterion(self, step_size, grad_size):
        step_condition = step_size < self._step_tolerance
        grad_condition = grad_size < self._gradient_tolerance
        singular_condition = False
        if not hasattr(self, "_hessian_or_inv"):
            hessian = self.hessian(self._x)
            if not (
                np.linalg.matrix_rank(hessian) == hessian.shape[0] == hessian.shape[1]
            ):
                singular_condition = True
        return (step_condition and grad_condition) or singular_condition

    def minimize(self, return_steps=False):
        if return_steps:
            steps = [self._x]
        fun_val = self._fun(self._x)
        logger.debug(f"Start fun_val={fun_val:.4f}, x={self._x}")
        for i in range(self._max_iterations):
            current_step = self.step()
            self._x = self._x + current_step
            self._n_steps += 1
            step_size = np.linalg.norm(current_step)
            grad_size = np.linalg.norm(self.gradient(self._x))

            fun_val = self._fun(self._x)
            logger.debug(
                f"fun_val={fun_val:.4f}, x={self._x}, step_size={step_size:.4f}, grad_size={grad_size:.4f}"
            )
            if return_steps:
                steps.append(self._x)
            if self.stopping_criterion(step_size, grad_size):
                logger.debug(f"Minimum found for x={self._x}")
                found_minimizer = self._x
                break
        else:
            # Is run if the for loops finish without break, in other words no minimizer found
            logger.debug(
                f"Did not find minimun after {self._max_iterations} iterations"
            )
            found_minimizer = None
        if return_steps:
            return found_minimizer, steps
        else:
            return found_minimizer


class ClassicalNewtonOptimization(Optimization):
    def step(self):
        current_step, _ = self.classical_newton_step()
        return current_step


class ExactLineSearchNewtonOptimization(Optimization):
    def step(self):
        current_step, _ = self.classical_newton_step()
        # find the optimal step size
        def new_fun(alpha):
            new_x = self._x + current_step * alpha
            return self._fun(new_x)

        # optimizing for alpha
        new_optimizer = ClassicalNewtonOptimization(
            new_fun,
            [1],
            step_tolerance=1e-3,
            gradient_tolerance=1e-3,
        )
        alpha = new_optimizer.minimize()
        if alpha is None:
            raise ValueError(
                f"Exact line search did not converge, alpha={new_optimizer.x}"
            )
        return alpha * current_step


class PowellWolfeLineSearchNewtonOptimization(Optimization):
    def step(self):
        current_step, current_gradient = self.classical_newton_step()
        alpha = self.powell_wolfe_step_size(current_step)
        return alpha * current_step


#### Quasi below ####


class QuasiNewtonOptimization(Optimization):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self._hessian_or_inv = np.eye(len(self._x))  # self.hessian(self._x)
        self._newton_step_key = "hess"

    @abc.abstractmethod
    def update_hessian(self, delta, gamma):
        """update self.hessian_or_inv attribute"""
        ...

    def step(self):
        current_step, current_gradient = self.classical_newton_step(
            **{self._newton_step_key: self._hessian_or_inv}
        )
        alpha = self.powell_wolfe_step_size(current_step)
        delta = current_step * alpha
        new_gradient = self.gradient(self._x + delta)
        gamma = new_gradient - current_gradient
        self.update_hessian(delta, gamma)
        return alpha * current_step


class QuasiNewtonInvOptimization(QuasiNewtonOptimization):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        # self._hessian_or_inv = np.linalg.inv(self._hessian_or_inv)
        self._newton_step_key = "hess_inv"


class GoodBroydenQuasiNewtonOptimization(QuasiNewtonOptimization):
    def update_hessian(self, delta, gamma):
        gamma.shape = (gamma.size, 1)
        delta.shape = (delta.size, 1)
        u = delta - self._hessian_or_inv @ gamma
        a = delta.transpose() @ self._hessian_or_inv @ delta
        self._hessian_or_inv = self._hessian_or_inv = (
            (1 / a) * u @ delta.transpose() @ self._hessian_or_inv
        )
        return self._hessian_or_inv


class BadBroydenQuasiNewtonOptimization(QuasiNewtonInvOptimization):
    def update_hessian(self, delta, gamma):
        gamma.shape = (gamma.size, 1)
        delta.shape = (delta.size, 1)
        u = delta - self._hessian_or_inv @ gamma
        a = 1 / gamma.transpose() @ gamma
        self._hessian_or_inv = self._hessian_or_inv + a * u @ gamma.transpose()
        return self._hessian_or_inv


class SymmetricBroydenQuasiNewtonOptimization(QuasiNewtonInvOptimization):
    def update_hessian(self, delta, gamma):
        gamma.shape = (gamma.size, 1)
        delta.shape = (delta.size, 1)
        u = delta - self._hessian_or_inv @ gamma
        uT_gamma = u.transpose() @ gamma
        if uT_gamma == 0:
            a = 0
        else:
            a = 1 / uT_gamma
        self._hessian_or_inv = self._hessian_or_inv + a * u @ u.transpose()
        return self._hessian_or_inv


class DFPQuasiNewtonOptimization(QuasiNewtonInvOptimization):
    def update_hessian(self, delta, gamma):
        gamma.shape = (gamma.size, 1)
        delta.shape = (delta.size, 1)
        delta_delta_T = delta @ delta.transpose()
        deltaT_gamma = delta.transpose() @ gamma
        H_gamma = self._hessian_or_inv @ gamma
        H_gamma_gammaT_H = H_gamma @ H_gamma.transpose()
        gammaT_H_gamma = gamma.transpose() @ self._hessian_or_inv @ gamma

        term1 = self._hessian_or_inv
        term2 = delta_delta_T / deltaT_gamma
        term3 = H_gamma_gammaT_H / gammaT_H_gamma
        new_H = term1 + term2 - term3
        self._hessian_or_inv = new_H

        return self._hessian_or_inv


class BFGSQuasiNewtonOptimization(QuasiNewtonInvOptimization):
    def update_hessian(self, delta, gamma):
        gamma.shape = (gamma.size, 1)
        delta.shape = (delta.size, 1)
        deltaT_gamma = delta.transpose() @ gamma
        if deltaT_gamma[0, 0] < 1e-16:
            return self._hessian_or_inv
        H_gamma_deltaT = (self._hessian_or_inv @ gamma) @ delta.transpose()
        delta_deltaT = delta @ delta.transpose()
        gammaT_H_gamma = gamma.transpose() @ (self._hessian_or_inv @ gamma)

        term1 = self._hessian_or_inv
        term2 = (1 + gammaT_H_gamma / deltaT_gamma) * delta_deltaT / deltaT_gamma
        term3 = (H_gamma_deltaT.transpose() + H_gamma_deltaT) / deltaT_gamma
        new_H = term1 + term2 - term3

        self._hessian_or_inv = new_H
        return self._hessian_or_inv
