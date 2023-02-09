% 3.2 a
G = ss(0,1,1,0);
G.InputDelay = 0.5;
H = c2d(G,1)

% b
zpk(H)

% c
pole(H)
zero(H)

% d
impulse(H)

% 3.3 a
z = tf('z');
H = z/(z^2-1.5*z+0.5);
H = ss(H)