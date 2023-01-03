include "globals.mzn";

% Weight 1 to 9 for all Weights
var 1..9: a;
var 1..9: b;
var 1..9: c;
var 1..9: d;
var 1..9: e;
var 1..9: f;
var 1..9: g;
var 1..9: h;
var 1..9: i;

constraint
  % All must be different
  alldifferent([a,b,c,d,e,f,g,h,i]) /\
  % Left chandelier
  (2*a = 1*b + 2*c) /\
  % Right chandelier - 1
  (1*f = 1*e + 2*d) /\
  % Right chandelier - 2
  (3*i = 1*h + 2*g) /\
  % Left and Right constraints
  3*(a+b+c) = 2*(d+e+f) + 3*(g+h+i)
;

solve satisfy;