import numpy as np
import matplotlib.pyplot as plt

import optimizations

from chebyquad_problem import chebyquad, gradchebyquad


n_steps = 9
x = np.linspace(0, 1, 4)
optimizer = optimizations.BFGSQuasiNewtonOptimization(chebyquad, x, gradchebyquad)

hess_norm_diff = np.zeros(n_steps)
for k in range(n_steps):
    print(k)
    real_inv_hessian = np.linalg.inv(optimizer.hessian(optimizer._x))
    hess_norm_diff[k] = np.linalg.norm(real_inv_hessian - optimizer._hessian_or_inv)
    # hess_norm_diff[k] = np.max(np.abs(real_inv_hessian - optimizer._hessian_or_inv))
    current_step = optimizer.step()
    optimizer._x += current_step

plt.figure()
plt.plot(hess_norm_diff)
# the difference is exploding?
plt.show()
