import numpy as np
import matplotlib.pyplot as plt


def _get_spline_basis(node_points, j, k=3):
    """
    replaces template place holder with values

    @param  node_points    1d-array that is also called Knot sequence by lecture notes, is assumed to be sorted
    @param int  j      index of basis function
    @param int  k           degree of splines

    @return
    """

    def spline_basis(u):
        """
        Evaluates basis function at the point(s) u

        @param int or float or array u     point(s) at which the spline is to be evaluated


        @return numpy.ndarray       Basis functions N(u)
        """
        # initialisation of recursion
        nis = np.zeros((k + 1, len(u)))
        for l, current_j in enumerate(range(j, j + k + 1)):
            index = np.searchsorted(
                node_points[current_j - 1 : current_j + 1], u, side="right"
            )
            nis[l] = index == 1

        # loop recursion
        for current_k in range(1, k + 1):
            for current_j in range(j, j + k - current_k + 1):
                # coeff0X is for the quota in front of current_j
                coeff00 = u - node_points[current_j - 1]
                coeff01 = (
                    node_points[current_j + current_k - 1] - node_points[current_j - 1]
                )
                # coeff1X is for the quota in front of current_j + 1
                coeff10 = node_points[current_j + current_k] - u
                coeff11 = node_points[current_j + current_k] - node_points[current_j]
                if coeff01 != 0:
                    quota1 = coeff00 / coeff01
                else:
                    # divide by zero so set to zero
                    quota1 = 0
                if coeff11 != 0:
                    quota2 = coeff10 / coeff11
                else:
                    # divide by zero so set to zero
                    quota2 = 0

                # index variable
                l = current_j - j
                nis[l] = quota1 * nis[l] + quota2 * nis[l + 1]

        values = nis[0]
        return values

    return spline_basis


def _de_boor(node_points, control_points, u):
    """
    Using de boor algorithm to evaluate the spline at u
    only for cubic spline
    assuming that node points are sorted
    assuming that all values in u are inside valid u[2] <= u[-3] interval


    @param numpy.ndarray node_points     knot sequence or grid
    @param numpy.ndarray control_points    control points or de boor points
    @param int or float or array u     point(s) at which the spline is to be evaluated

    @return int or float numpy.ndarray      spline evaluated at the point(s) u
    """
    # initialisation of recursion
    hot_index = np.searchsorted(node_points, u, side="right") - 1

    dis = np.zeros((4, len(u), control_points.shape[1]))
    for current_j in range(-2, 2):
        current_index = hot_index + current_j
        current_index[current_index < 0] = 0
        current_index[current_index >= len(control_points)] = len(control_points) - 1
        # if error here, probably u outside of assumed interval is the problem
        current_control_points = control_points[current_index]
        dis[current_j + 2] = current_control_points

    # loop recursion
    for current_k in range(1, 4):
        for current_j in range(4 - current_k):
            rightmost_index = hot_index + current_j + 1
            leftmost_index = hot_index + current_j - 3 + current_k
            coeff0 = node_points[rightmost_index] - u
            coeff1 = node_points[rightmost_index] - node_points[leftmost_index]
            valid = coeff1 != 0
            alpha = np.zeros((len(u), 1))
            alpha[valid, 0] = coeff0 / coeff1
            alpha[~valid, 0] = np.nan
            # setting singular second dimension for broadcasting

            # index variable
            dis[current_j] = alpha * dis[current_j] + (1 - alpha) * dis[current_j + 1]
    values = dis[0]
    return values


def _get_automatic_node_points(points):
    """
    Create node points if they are not given.

    @param numpy.ndarray points     points that the spline should go through. Of dimension nxd, where n is the
        number of points and d the dimension

    @return numpy.ndarray      node points
    """
    # distances between points
    inter_distances = np.linalg.norm(np.diff(points, axis=0), axis=1)
    # cumsum to get nodes
    node_points = np.cumsum(inter_distances)
    # adding a starting zero
    node_points = np.insert(node_points, 0, 0)
    return node_points


class Cubic_spline:
    """
    Class for cubic splines.
    """

    def __init__(self, control_points, node_points=None):
        """
        Creates a cubic spline object. If node points are given the constructor sorts them together with the control_points. If node_points are not given the L2 norm between control_points will be used to get a node_points vector.


        @param numpy.ndarray control_points    control points or de boor points for the spline
        @param numpy.ndarray node_points     knot sequence or grid (optional)

        @return Cubic_spline object
        """
        if node_points is None:
            node_points = _get_automatic_node_points(control_points)
        else:
            # should have equal length
            assert len(control_points) == len(node_points)
            # confirm sorting of node points
            argsort = np.argsort(node_points)
            control_points = control_points[argsort]
            node_points = node_points[argsort]

        control_points = np.concatenate(
            ([control_points[0]] * 2, control_points, [control_points[-1]] * 2)
        )
        # padding to avoid indexing errors
        node_points = np.concatenate(
            (
                node_points[0] - np.zeros(2),
                node_points,
                node_points[-1] + np.arange(5) + 1,
            )
        )

        self._node_points = node_points
        self._control_points = control_points
        self._n_dimensions = control_points.shape[1]

    def get_definition_range(self):
        return [self._node_points[2], self._node_points[-4] - 1e-8]

    def __call__(self, u):
        """
        De boor's algorithm to  evaluate spline.

        @param int or float or array u     point(s) at which the spline is to be evaluated

        @return numpy.ndarray      spline evaluated at the point(s) u
        """
        if isinstance(u, (int, float)):
            u = np.array([u])

        s = np.zeros((len(u), self._n_dimensions))
        valid_u = (self._node_points[2] <= u) & (u <= self._node_points[-3])
        # setting all values not valid as nan
        s[~valid_u] = np.nan
        s[valid_u] = _de_boor(self._node_points, self._control_points, u[valid_u])

        # if not input is array output is expected to not be array
        s = s.squeeze()
        return s

    def plot(self, show=True):
        """
        Method to plot spline of the Cubic_spline class.
        De boor's algorithm to  evaluate spline.

        @param bool                if true matplotlib.pyplot.show() is executed

        @return optionally (matplotlib.figure, matplotlib.axes) if show == False the matplotlib figure and axes is returned for further formatting / saving

        """
        u = np.linspace(self._node_points[2], self._node_points[-3], 100)[:-1]
        s = self(u)
        if self._n_dimensions == 2:
            subplot_kw = None
        elif self._n_dimensions == 3:
            subplot_kw = dict(projection="3d")
        else:
            raise ValueError(
                f"This spline with {self._n_dimensions} dimensions can not be plotted."
            )
        fig, ax = plt.subplots(subplot_kw=subplot_kw)
        ax.plot(*s.T, label="spline")
        ax.plot(*self._control_points.T, "ro--", label="control points")
        ax.plot(*self._control_points[0], "ro", markersize=10)
        if show:
            ax.legend()
            plt.show()
        else:
            return fig, ax
