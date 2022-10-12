import numpy as np
import matplotlib.pyplot as plt
import timeit

import splines


test_control_points = np.random.randn(9, 2)
test_node_points = np.random.randn(9)

spline = splines.Cubic_spline(test_control_points, test_node_points)
u_range = spline.get_definition_range()


def call_loop(us):
    for u in us:
        spline(u)

n_runs_each = 100
n_numbers = np.arange(1, 1000, 100)
vector_times = np.zeros(len(n_numbers))
loop_times = np.zeros(len(n_numbers))
# run vectorised
for i, n in enumerate(n_numbers):
    us = np.linspace(*u_range, n)
    time1 = timeit.timeit("spline(us)", globals=globals(), number=n_runs_each) / n_runs_each
    time2 = timeit.timeit("call_loop(us)", globals=globals(), number=n_runs_each) / n_runs_each
    print(f"n={n}, vector time: {time1}, loop time: {time2}")
    vector_times[i] = time1
    loop_times[i] = time2

def line_coefficient(n, t):
    return np.sum(n * t) / np.sum(t**2)

vector_coefficient = line_coefficient(n_numbers, vector_times)
loop_coefficient = line_coefficient(n_numbers, loop_times)

plt.figure()
plt.plot(n_numbers, vector_times, label=f"vector, coeff={vector_coefficient:.4f}")
plt.plot(n_numbers, loop_times, label=f"loop, coeff={loop_coefficient:.4f}")
plt.xlabel("number of us")
plt.ylabel("excecution time")
plt.legend()
plt.savefig("excecution_time_comparison")
plt.show()
