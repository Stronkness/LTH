% 5.2 a
A = [0.6 0.5; 0 0.6];
B = [0; 2.34];
C = [0.788 -0.875];
D = -1.64;
n = 3; % number of fractional bits
round(A*2^n)
round(B*2^n)
round(C*2^n)
round(D*2^n)

% b
h = 0.05;
H = ss(A,B,C,D,h);
A3 = round(A*2^n)/2^n;
B3 = round(B*2^n)/2^n;
C3 = round(C*2^n)/2^n;
D3 = round(D*2^n)/2^n;
H3 = ss(A3,B3,C3,D3,h);
step(H,H3)
bode(H,H3)
% The systems behave quite similarly, although the fixed-point version has too
% low static gain.

% c
% 16-bit signed integers can take values from −32768 to 32767. The input is in
% the range −512 to 511. Simulated step responses (or gain computations using
% the command norm) show that the gain from the input to the states may be
% as large as 8.5. Since 512 · 8.5 · 23 = 34816 > 32768, there is a small risk of
% overflow. A more detailed analysis would have to consider the exact order in
% which the various terms in the control law are added
Hx1 = ss(A3,B3,[1 0],0,h); % model with x1 as output
step(Hx1)
Hx2 = ss(A3,B3,[0 1],0,h); % model with x2 as output
step(Hx2)