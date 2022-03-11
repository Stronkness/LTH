% Fixed-Point Iteration algorithm 
 % Find the fixed point of g(x).

 g = @(x) (2*x + 2)^(1/3);
 p0 = input('Please enter initial approximation, p0:  ');
 n = input('Please enter no. of ierations, n: ');
 tol = input('Please enter tolerance, tol: ');

 i = 1;
 while i <= n
     p = g(p0);
     if abs(p-p0) < tol
        fprintf('\nApproximate solution p = %11.8f \n\n', p)
        break;
     else
        i = i+1;
        p0 = p;
     end
     disp(p)
     disp(tol)
 end
 if i > n
     disp('No Convergent');
 end