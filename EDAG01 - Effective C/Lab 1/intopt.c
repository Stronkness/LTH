#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **args) {
    int m, n;
    double *c, *b;
    double **a;

    scanf("%d %d", &m, &n);

    c = calloc(1, sizeof(double));
    b = calloc(m, sizeof(double));
    a = calloc(m, sizeof(double*));

    for (int i = 0; i < n; i++) {
        scanf("%lf", &c[i]);
    }

    for (int i = 0; i < m; i++) {
        a[i] = calloc(n, sizeof(double));

        for (int x = 0; x < n; x++) {
            scanf("%lf", &a[i][x]);
        }
    }

    for (int i = 0; i < m; i++) {
        scanf("%lf", &b[i]);
    }

    printf("m = %d, n = %d\n", m, n);
    printf("max z = ");
    for (int i = 0; i < n; i++) {
        printf("%10.3lf", c[i]);
        if (i < n - 1) {
            printf(" + ");
        } else {
            printf("\n");
        }
    }
    for (int y = 0; y < m; y++) {
        printf("        ");
        for (int x = 0; x < n; x++) {
            printf("%10.3lf", a[y][x]);
            if (x < n - 1) {
                printf(" + ");
            } else {
                printf(" \u2264 %lf", b[y]);
            }
        }
        printf("\n");
    }

    printf("\n");
    free(c);
    free(b);
    for (int i = 0; i < m; i++) {
        free(a[i]);
    }
    free(a);
}