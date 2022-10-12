import numpy as np
import optimizations

import logging

logging.basicConfig(level=logging.DEBUG)

def base_function1(x):
    return 100 * (x[1] - x[0]**2)**2 + (1 - x[0])**2

x0 = [14, 4.5]
optimizer = optimizations.ExactLineSearchNewtonOptimization(base_function1, x0)
x_star = optimizer.minimize()
assert np.allclose(x_star, 1)
