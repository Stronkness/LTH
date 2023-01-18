% 0.1
A = [0,1;1,0];
eig(A);

% 0.2
%help ss
B = [1;0];
C = [0,1];
D = 0; % nothing
sys = ss(A,B,C,D);
%help tf
res_2 = tf(sys);

% 0.3
%help zpk
res_3 = zpk(sys);

% 0.4a
s = tf('s');
G = 1/(s^2 + 0.6*s + 1);
G.InputDelay = 1.5;
% or
G = exp(-1.5*s)/(s^2 + 0.6*s + 1);
%step(G); % stable 
%b
%bode(G);
%nyquist(G);
% The Nyquist curve encircles the critical point −1, which means that the closed-loop system will be unstable.

% 0.5
eq = [1 1.4 1];
eq_r = roots(eq);
K = place(A,B,eq_r);

% 0.6
eq = [1 2.8 4];
eq_r = roots(eq);
L = place(A',C',eq_r)';

% 0.7
G_nodelay = 1/(s^2 + 0.6*s + 1);
G_c = 2*(1 + 1/(s*1.5) + 0.5*s);
%bode(G_c);
%margin(G_c*G_nodelay);

% 0.8
SYS1 = G_c*G_nodelay;
SYS2 = 1;
G_cl = feedback(SYS1,SYS2);
G_cl = minreal(G_cl);

G_cl = SYS1/(SYS2 + SYS1);
G_cl = minreal(G_cl);

% 0.9
%step(G_cl);
gain = dcgain(G_cl);

% 0.10
H = ss([1 1; 0 0], [0 -1;1 0], [1 0], 0, 1);
%step(H);
pole(H);

% 0.11
G1 = tf(1,[1 3 3 1]);
G2 = zpk(1/(s + 1)^3);
G3 = tf(1,[1 2.99 3 1]);
G4 = zpk(1/(s + 0.99)^3);
pole(G1);
pole(G2);
pole(G3);
pole(G4);
