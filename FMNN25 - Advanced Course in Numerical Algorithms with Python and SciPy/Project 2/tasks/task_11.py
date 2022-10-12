import numpy as np
from scipy import optimize

import optimizations

from chebyquad_problem import chebyquad, gradchebyquad


for n in [4, 8, 11]:
    # Only works for N = 4
    print(f"N={n}")

    x = np.linspace(0, 1, n)

    xmin = optimize.fmin_bfgs(
        chebyquad, x, gradchebyquad
    )  # should converge after 18 iterations
    print(f"scipy: xmin={xmin}")

    optimizer = optimizations.BFGSQuasiNewtonOptimization(chebyquad, x, gradchebyquad,max_iterations=10)
    xmin = optimizer.minimize()  # should converge after 18 iterations
    n_steps = optimizer.n_steps
    print(f"Ours: xmin={xmin}, n_steps={n_steps}")
    print()
