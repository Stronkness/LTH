% 2.3
Phi = [0.5 -0.2; 0 0]; % Define system matrices
Gamma = [2; 1];
C = [1 0];
D = 0;
H = ss(Phi,Gamma,C,D,1); % Define discrete-time model
zpk(H) % Calculate pulse transfer function

% 2.4 b
s = zpk('s');
G = 1/s^3;
H01 = c2d(G,0.1) % For h = 0.1
H1 = c2d(G,1) % For h = 1

% 2.6
A = [0 1 ; -1 0]; % Define system matrices
B = [0 ; 1];
C = [1 0];
D = 0;
G = ss(A,B,C,D) % Define continuous-time system
H1 = c2d(G,pi/2); % Sample the oscillator
H2 = c2d(G,pi/4);
step(G,H1,H2,20) % Plot the step response of all systems until 20 s

% 2.7
G = tf([1],[1 3 2 0]);
G = ss(G);
H = c2d(G,1);
tf(H)