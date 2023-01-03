include "globals.mzn";

% Declare data from data-sheet
int: beef = 1;
int: bun = 2;
int: cheese = 3;
int: onion = 4;
int: pickles = 5;
int: lettuce = 6;
int: ketchup = 7;
int: tomato = 8;

array[beef..tomato] of int: sodium = [50, 330, 310, 1, 260, 3, 160, 2];
array[beef..tomato] of int: fat = [17, 9, 6, 2, 0, 0, 0 , 0];
array[beef..tomato] of int: calories = [220, 260, 70, 10, 5, 4, 20, 9];
array[beef..tomato] of int: cost = [25, 15, 10, 9, 3, 4, 2, 4];

var 1..5: n_beef;
var 1..5: n_bun;
var 1..5: n_cheese;
var 1..5: n_onions;
var 1..5: n_pickles;
var 1..5: n_lettuce;
var 1..5: n_ketchup;
var 1..5: n_tomato;
var int: price = 0;

constraint n_ketchup = n_lettuce;
constraint n_pickles = n_tomato;
constraint 
  n_beef*sodium[beef] + n_bun*sodium[bun] + n_cheese*sodium[cheese] +           n_onions*sodium[onion] + n_pickles*sodium[pickles] + n_lettuce*sodium[lettuce]
  + n_ketchup*sodium[ketchup] + n_tomato*sodium[tomato] < 3000
;
constraint 
  n_beef*fat[beef] + n_bun*fat[bun] + n_cheese*fat[cheese] +           n_onions*fat[onion] + n_pickles*fat[pickles] + n_lettuce*fat[lettuce]
  + n_ketchup*fat[ketchup] + n_tomato*fat[tomato] < 150
;
constraint 
  n_beef*calories[beef] + n_bun*calories[bun] + n_cheese*calories[cheese] +           n_onions*calories[onion] + n_pickles*calories[pickles] + n_lettuce*calories[lettuce]
  + n_ketchup*calories[ketchup] + n_tomato*calories[tomato] < 3000
;

%Solver for ingredients when price is 280
constraint 
  n_beef*cost[beef] + n_bun*cost[bun] + n_cheese*cost[cheese] +           n_onions*cost[onion] + n_pickles*cost[pickles] + n_lettuce*cost[lettuce]
  + n_ketchup*cost[ketchup] + n_tomato*cost[tomato] = 280
;

solve satisfy;
% -------------------------------------

% % Solver for maximize price
% solve maximize price;

% price = n_beef*cost[beef] + n_bun*cost[bun] + n_cheese*cost[cheese] +           n_onions*cost[onion] + n_pickles*cost[pickles] + n_lettuce*cost[lettuce]
%   + n_ketchup*cost[ketchup] + n_tomato*cost[tomato];