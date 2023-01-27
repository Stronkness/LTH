% 1.4
y := ADIN(ychan)
e := uref - y
D := ad * D - bd * (y- yold)
v := L*(beta*yref - y) + I + D
if mode = auto then u:= sat(v,umax,umin)
else u := sat(uman,umax,umin)
DAOut(u,uchan)
I := I + (K*H/Ti)*e + (h/Tr)*(u - v)
if increment then uinc := 1
elseif decrement then usinc := -1
else uinc := 0
uman := uman + (h/Tm) * uinc + (h/Tr) * (u - uman)
yold := y

% ad and bd are pre-calculated parameters given by the
% backward difference approximation of the D-term, i.e, as
ad := Td / (Td + N*H)
bd := K*Td*N / (Td + N*h)