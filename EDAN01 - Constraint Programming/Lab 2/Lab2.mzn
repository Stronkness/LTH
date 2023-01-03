include "subcircuit.mzn";

int: graph_size;
int: start;
int: n_dests;
int: n_edges;
array[1..n_dests] of int: dest;
array[1..n_edges] of int: from;
array[1..n_edges] of int: to;
array[1..n_edges] of int: cost;

array[1..n_edges*2] of int: whole_cost = cost ++ cost;

var int: total_cost;
var int: same_cost;
var int: result_cost;
array[1..n_dests, 1..graph_size] of var int: nodes;
%1 = used
array[1..n_edges*n_dests] of var opt int: used_edges = [1 | i in 1..n_dests, j in 1..n_edges where nodes[i, from[j]] = to[j] \/ nodes[i, to[j]] = from[j]];

function set of int: possible_next(int: node) = {to[n] | n in 1..n_edges where from[n] = node};
function set of int: possible_backtrack(int: node) = {from[n] | n in 1..n_edges where to[n] = node};

constraint forall(i in 1..n_dests, j in 1..graph_size where (j = dest[i])==false) (nodes[i,j] in (possible_next(j) union possible_backtrack(j) union {j}));
constraint forall(i in 1..n_dests, j in 1..graph_size where (j = dest[i])) (nodes[i,j] in {start});

constraint forall(i in 1..n_dests)(subcircuit(row(nodes, i)));

constraint total_cost = sum(i in 1..n_edges*n_dests where used_edges[i]=1 /\ if i < n_edges /\ n_dests > 1 then (used_edges[i+n_edges] = 1)==false endif)(whole_cost[i]);

solve minimize total_cost;

output[show(total_cost)];
output[show(nodes)];