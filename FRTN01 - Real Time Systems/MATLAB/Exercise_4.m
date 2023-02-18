% 4.2 a
Phi = [0.78 0; 0.22 1];
C = [0 1];
L = acker(Phi',C',[0 0])'

% b
Phi = [0.78 0; 0.22 1];
C = [0 1];
L = acker(Phi',(C*Phi)',[0 0])'

% 4.3
Phi = [0.5 1; 0.5 0.7];
Gamma = [0.2; 0.1];
C = [1 0];
Phie = [Phi Gamma; zeros(1,2) 1];
Gammae = [Gamma; 0];
Ce = [C 0];
% (a) Observer design
Le = place(Phie', Ce', [0.4 0.5 0.6])'
 % (b) State feedback design
K = place(Phi, Gamma, [0.3+0.3*i 0.3-0.3*i]);
Kv = 1;
Ke = [K Kv]
 % (c) Form complete controller
Gc = ss(Phie-Le*Ce-Gammae*Ke, Le, Ke, 0, 1); % define controller
Gp = ss(Phi, Gamma, C, 0, 1); % define process
figure(1)
step(Gp/(1+Gp*Gc),20) % plot input disturbance response
figure(2)
margin(Gp*Gc) % plot loop transfer function