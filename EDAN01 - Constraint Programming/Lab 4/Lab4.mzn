include "globals.mzn";
include "jacop.mzn";

int: del_add;
int: del_mul;
int: number_add;
int: number_mul;
int: n;

set of int : last;
set of int : add;
set of int : mul;
array[1..n] of set of int : dependencies; % the nodes in dependencies[i] depends on the outoput of node i
array[1..n] of var 0..(n*2) : times;
array[1..n] of int : delays = [ (if i in add then del_add else del_mul endif) | i in 1..n];

array[1..n] of var 1..max(number_add, number_mul): resources; %(number_add + number_mul) : resources;
array[1..n, 1..4] of var int: diffAdd;
array[1..n, 1..4] of var int: diffMul;

constraint forall(j in 1..n where j in add)(diffAdd[j,1]=times[j] /\ diffAdd[j,2]=resources[j] /\ diffAdd[j,3]=del_add /\ diffAdd[j,4]=1);
constraint forall(j in 1..n where j in mul)(diffMul[j,1]=times[j] /\ diffMul[j,2]=resources[j] /\ diffMul[j,3]=del_mul /\ diffMul[j,4]=1);
constraint forall (i in 1..n, dep in dependencies[i]) (
  times[dep] >= times[i] + (if i in add then del_add else del_mul endif)
  );
constraint forall (i in 1..n) (
  if i in add then resources[i] in 1..number_add else resources[i] in 1..number_mul endif);
constraint diff2(diffAdd);
constraint diff2(diffMul);

solve :: int_search([times[last[1]] + (if last[1] in add then 1 else 2 endif), times[last[2]] + (if last[2] in add then 1 else 2 endif)], first_fail, indomain_min)
minimize max([times[last[1]] + (if last[1] in add then 1 else 2 endif), times[last[2]] + (if last[2] in add then 1 else 2 endif)]);

output[show(max([times[last[1]] + (if last[1] in add then 1 else 2 endif), times[last[2]] + (if last[2] in add then 1 else 2 endif)]))];