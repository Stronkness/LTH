# Project 1 - Splines

This is the first project in the course FMNN25. 

The program creates cubical splines both using the de Boor algorithm and explicit.

The parameters that are used are
- control points: these forms the spline, and
- node points (optional): defines the domain of the spline, and is connected 1:1 with the control points. If no node points are handed, 
they are cumulative by the euclidean norm, starting from zero. 

# Installation

Simply install this package by running:
``` 
pip install [path_to_this_folder]
```

# Running the program 

The program does not have a main function but instead consists of some test cases that runs the program. The main test case, which plots the spline (both with de Boor and explicit) together with the control points, is run with

``` 
python3 -m pytest -k test_spline_plot --show
```
The `--show` option to pytest is here used to show all example plots that are produced. To run the tests silently omit this option.

To change the values of the control or node points, one can do one of the following:
- in the file tests/test_spline.py, change the random number generator seed, or
- create your own lists with control points [and node points].

To run other test cases, change "test_spline_plot" with either of the following:
- test_spline_basis: checks that the basis functions adds up to 1 for all values of the domain, 
- test_spline_de_boor: checks that the spline from the de Boor algorithm is equal (no difference greater than 1e-8) the explicit calculated spline,
- test_spline_automatic_node_points: checks that the node points are generated correctly when no node points are handed by the user. 

One can also use the package in python scripts and programs, for example by writing:
```python
import numpy as np
import splines

# six points in 2D
control_points = np.random.randn(6, 2)
# node_points = np.random.randn(6)  # optional
spline = splines.Cubic_spline(control_points)  # node_points can be added here as well
spline.plot()
```

3D (or nD) splines are also inheritently supported try it by running:
```python
import numpy as np
import splines

# 6 points in 3D
control_points = np.random.randn(6, 3)
spline = splines.Cubic_spline(control_points)
spline.plot()
```

# Documentation

See docstrings in the code.
