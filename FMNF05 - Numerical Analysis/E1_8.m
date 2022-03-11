%Secant method algorithm

f = @(x) (exp(x)+sin(x)-4);
x0 = 1;
x1 = 2;
e = 10^-6;
N = 15;

for i=1:N
    x2 = (x0*f(x1)-x1*f(x0))/(f(x1)-f(x0));
    fprintf('x%d = % .50f\n', i, x2);
    if abs(x2-x1) < e
        return
    end
    x0 = x1;
    x1 = x2;
end
disp('Could not find solution');