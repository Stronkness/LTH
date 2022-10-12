import optimizations
import numpy as np
import matplotlib.pyplot as plt

def rosenbrock2(x):
    return 100 * (x[1] - x[0]**2)**2 + (1 - x[0])**2


x0 = [0.5, 0.5]
optimizer = optimizations.BFGSQuasiNewtonOptimization(rosenbrock2, x0)
x_star, x_steps = optimizer.minimize(return_steps=True)
# x_steps = x_steps[-8:]
x_steps = np.array(x_steps)

# print(x_star)
# print(x_steps)

buff = 1
xlist = np.linspace(np.min(x_steps[:, 0]) - buff, np.max(x_steps[:, 0]) + buff, 100)
ylist = np.linspace(np.min(x_steps[:, 1]) - buff, np.max(x_steps[:, 1]) + buff, 100)
X, Y = np.meshgrid(xlist, ylist)
Z = 100*(Y - X**2)**2 + (1-X)**2
fig,ax=plt.subplots(1,1)
cp = ax.contour(X, Y, Z, 100)
fig.colorbar(cp) # Add a colorbar to a plot
ax.set_title('Rosenbrock')
plt.plot(x_steps[:,0],x_steps[:,1], '--o')
plt.show()
