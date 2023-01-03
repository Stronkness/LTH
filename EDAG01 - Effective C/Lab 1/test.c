#include <stdio.h>
#include <math.h>

int main()
{
        double  pi = 4 * atan(1.0);

        printf("%lf\n", pi);
        printf("%+lf\n", pi);
        printf("%-lf\n", -pi);
        printf("% lf\n", pi);
}