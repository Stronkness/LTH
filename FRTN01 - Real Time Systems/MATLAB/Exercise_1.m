% 1.2
% The Tustin and ramp invariance methods give the best approximations for this example.
s = tf('s');
G = (1+s)/(1+s/10)^2;
bode(G);
hold on;
h = 0.1;
z = tf('z',h);

% a - Forward difference
sp = (z-1)/h;
H1 = (1+sp)/(1+sp/10)^2;
bode(H1);

% b - Backward difference
sp = (z-1)/(z*h);
H2 = (1+sp)/(1+sp/10)^2;
bode(H2);

% c - Tustin's
H3 = c2d(G,h,'tustin');
bode(H3);

% d - Ramp inveriance (first-order hold)
H4 = c2d(G,h,'foh');
bode(H4);


% 1.5
% a
A = [-3 1; 0 -2];
B = [0; 1];
C = [1 0];
K = place(A,B,roots([1 8 32]))

% b
h = 0.1;
Ktilde = K*(eye(2)+(A-B*K)*h/2);
H1 = ss(A-B*K,[0;0],C,0);
[Phi,Gamma] = c2d(A,B,h);
H2 = ss(Phi-Gamma*K,[0;0],C,0,h);
H3 = ss(Phi-Gamma*Ktilde,[0;0],C,0,h);
[y1,t1] = initial(H1,[1;0],2.5);
[y2,t2] = initial(H2,[1;0],2.5);
[y3,t3] = initial(H3,[1;0],2.5);
plot(t1,y1)
hold on
stem(t2,y2,'r')
stem(t3,y3,'g')

% It is seen that the discrete-time controller using K~
% approximates the continous-time controller much better
% than the discrete-time controller using K.
