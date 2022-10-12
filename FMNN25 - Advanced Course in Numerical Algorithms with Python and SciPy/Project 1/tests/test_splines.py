import numpy as np
import matplotlib.pyplot as plt

import splines

# Randomize a set of numbers used for control points and node points
np.random.seed(5)

test_control_points = np.random.randn(9, 2)
test_node_points = np.random.randn(9)


def test_spline_basis(show):
    """
    Test the basis functions of the spline.
    The node basis of all indexes must add up to 1 to be valid.
    If this is not the case then the spline does'nt work.
    This can also be verified manually by viewing the plots

    To use the cubic splines full potential, the amount of control
    points must be 3 or more
    """
    spline = splines.Cubic_spline(test_control_points, test_node_points)
    node_points = spline._node_points
    print("n node points", len(node_points))
    print("n control points", len(spline._control_points))
    K = len(node_points) - 2  # minus two for extra padded node
    L = K - 2
    print("K = ", K, "L = ", L)

    # the valid interval of u:s for s
    u = np.linspace(*spline.get_definition_range(), 100)

    sum_values = np.zeros(len(u))
    if show:
        plt.figure()
    for j in range(L + 1):
        spline_basis = splines._get_spline_basis(node_points, j)
        values = spline_basis(u)
        sum_values += values
        if show:
            plt.plot(u, values, label=f"j={j}")
    if show:
        plt.title("test_spline_basis")
        plt.plot(u, sum_values, label="sum")
        plt.legend()
        plt.show()

    assert np.all(np.abs(sum_values - 1) < 1e-8), "node basis does not add to 1"


def test_spline_de_boor(show):
    """
    Tests if de Boor and Explicit spline estimation is lower than floating value 1e-8.
    This checks if all the estimation points in de Boor and Explicit spline
    is relatively close to each other, hence verifying that the estimation works.

    Plots the result with de Boor as the color blue and Explicit as orange
    following the control points.
    """
    spline = splines.Cubic_spline(test_control_points, test_node_points)

    u = np.linspace(*spline.get_definition_range(), 100)
    # using de_boor to calculate
    s1 = spline(u)

    node_points = spline._node_points
    control_points = spline._control_points
    K = len(node_points) - 2
    L = K - 2

    # print("n node points", len(node_points))
    # print("n control points", len(spline._control_points))
    # print("K = ", K, "L = ", L)

    # using definition with base functions to calculate s, not optimal
    s2 = np.zeros((len(u), control_points.shape[1]))
    for j in range(L + 1):
        base_function = splines._get_spline_basis(node_points, j)
        s2 += control_points[j] * base_function(u)[:, np.newaxis]

    assert np.all(
        np.abs(s1 - s2) < 1e-8
    ), "de_boor not equal to explicit spline estimation"

    if show:
        plt.figure()
        plt.title("test_spline_de_boor")
        plt.plot(*control_points.T, "--ro")
        plt.plot(*control_points[0], "ro", markersize=10)
        plt.plot(*s1.T, linewidth=3, label="de boor")
        plt.plot(*s2.T, label="explicit")
        plt.legend()
        plt.show()


def test_spline_plot(show):
    """
    Plots the Explicit cubic spline with the chosen control and node points
    """
    spline = splines.Cubic_spline(test_control_points, test_node_points)
    fig, ax = spline.plot(show=False)
    if show:
        ax.set_title("test_spline_plot")
        plt.show()


def test_spline_3d_plot(show):
    """
    Plots the Explicit cubic spline with the chosen control and node points
    """
    control_points = np.random.randn(9, 3)
    spline = splines.Cubic_spline(control_points)
    fig, ax = spline.plot(show=False)
    if show:
        ax.set_title("test_spline_3d_plot")
        plt.show()


def test_spline_automatic_node_points():
    points = np.array([[0, 0], [0, 1], [1, 1], [1, 3]])
    node_point = splines._get_automatic_node_points(points)
    expected_node_points = np.array([0, 1, 2, 4])
    assert np.all(
        np.abs(node_point - expected_node_points) < 1e-8
    ), "expected_node_points not recieved"
