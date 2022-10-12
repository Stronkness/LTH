import setuptools

setuptools.setup(
    name="optimizations",
    version="0.0.1",
    author="",
    author_email="",
    description="Optimizations for Project 2, course NUMN21/FMNN25",
    packages=setuptools.find_packages(),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: GNU GPLv3",
        "Operating System :: OS Independent",
    ],
    scripts=[],
    python_requires=">=3.6",
    install_requires=[
        "numpy",
        "scipy",
        "matplotlib",
        "numdifftools",
    ],
)
