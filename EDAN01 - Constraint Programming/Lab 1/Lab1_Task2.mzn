include "globals.mzn";

int: n;
int: n_prefs;
array[1..n_prefs, 1..2] of int: prefs;

array[1..n] of var 1..n: persons;
array[1..n_prefs] of var 1..n_prefs: distance;

constraint alldifferent(persons);
constraint 
   forall(p in 1..n_prefs)(distance[p] = abs(persons[prefs[p,1]] - persons[prefs[p,2]]))
;

var int: max_dist = max(distance);

solve minimize max_dist;