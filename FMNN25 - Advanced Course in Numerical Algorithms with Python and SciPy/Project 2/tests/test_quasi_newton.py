import numpy as np
import pytest

import optimizations


def base_function0(x):
    return np.sum(((x - 1) ** 2))


def base_function1(x): # rosenbrock
    return 100 * (x[1] - x[0] ** 2) ** 2 + (1 - x[0]) ** 2


def test_abstract_classes():
    x0 = [3, 4, 6]
    with pytest.raises(TypeError):
        # should throw an error since it is an abstract class
        optimizations.NewtonOptimization(base_function0, x0)
    with pytest.raises(TypeError):
        optimizations.QuasiNewtonOptimization(base_function0, x0)
    with pytest.raises(TypeError):
        optimizations.QuasiNewtonInvOptimization(base_function0, x0)


def test_gradient_function():
    def func(x):
        return 2 * x[0] ** 2 - 5 * x[1] + np.cos(x[2])

    def gradfunc(x):
        return np.array([6 * x[0] ** 2, -5, -np.sin(x[2])])

    x0 = np.array([0, 0, 0])
    gradgrad = optimizations.Gradient(func)(x0)
    assert np.allclose(gradgrad, gradfunc(x0))


def test_hessian_function():
    def func(x):
        return 2 * x[0] ** 2 - 5 * x[1] + np.cos(x[2])

    real_hess = np.array([[4, 0, 0], [0, 0, 0], [0, 0, -1]])
    x0 = np.array([0, 0, 0])
    est_hess = optimizations.Hessian(func)(x0)
    assert np.allclose(real_hess, est_hess)


@pytest.mark.parametrize(
    "optimization_class",
    [
        optimizations.ClassicalNewtonOptimization,
        optimizations.ExactLineSearchNewtonOptimization,
        optimizations.PowellWolfeLineSearchNewtonOptimization,
        # optimizations.GoodBroydenQuasiNewtonOptimization,
        # optimizations.BadBroydenQuasiNewtonOptimization,
        # optimizations.SymmetricBroydenQuasiNewtonOptimization,
        # optimizations.DFPQuasiNewtonOptimization,
        optimizations.BFGSQuasiNewtonOptimization,
    ],
)
def test_optimization_with_rosenbrock(optimization_class):
    np.random.seed(0)
    x0 = np.random.randn(2) * 5  # [14, 4.5]
    print(x0)
    optimizer = optimization_class(base_function1, x0)
    x_star = optimizer.minimize()
    if x_star is None:
        raise ValueError(f"Did not converge at {optimizer.x}")
    # assert np.allclose(x_star, 1)
    assert np.all(np.abs(x_star - 1) < 1e-4)


# @pytest.mark.parametrize(
#     "optimization_class",
#     [
#         optimizations.BFGSQuasiNewtonOptimization,
#     ],
# )
# def test_update_hessian(optimization_class):
#     x0 = np.array([3, 5])
#     optimizer = optimization_class(base_function0, x0)
#     current_step = optimizer.step()
#     print(current_step)
#     new_x = x0 + current_step
# 
#     estimated_hessian = optimizer._hessian_or_inv
#     real_hessian = optimizer.hessian(new_x)
#     if "inv" in optimizer._newton_step_key:
#         real_hessian = np.linalg.inv(real_hessian)
#     # print("estimated")
#     # print(estimated_hessian)
#     # print("real")
#     # print(real_hessian)
# 
#     assert np.all(np.abs(estimated_hessian - real_hessian) < 1e-5)
