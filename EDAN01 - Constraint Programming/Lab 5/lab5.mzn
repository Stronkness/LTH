include "globals.mzn";

% Parameters/Input
int: n;
int: n_commercial;
int: n_residential;
array[int] of int: point_distribution;

% Decision variables
var int: cost;
array[1..n, 1..n] of var 0..1: field;

% Constraints
% Symmetry breakin constraint, Require adjacent rows and adjacent columns in the array "field" to be lexicographically ordered. Adjacent rows and adjacent columns may be equal.
constraint lex2(field);
% Check correct amount of residential and commerical are in the field
constraint sum([field[i,j] == 1 | i in 1..n, j in 1..n]) == n_residential;
constraint sum([field[i,j] == 0 | i in 1..n, j in 1..n]) == n_commercial;           
% Calculate cost
constraint cost = sum([point_distribution[sum([field[i,j] | j in 1..n]) + 1] | i in 1..n]) + 
                  sum([point_distribution[sum([field[j,i] | j in 1..n]) + 1] | i in 1..n]);
% Solve  
solve maximize cost;

% Show output
output[show(cost)];