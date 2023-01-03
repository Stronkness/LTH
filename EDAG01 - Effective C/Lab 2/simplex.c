#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

struct simplex_t {
    double y; // y
    int m; // Constraints
    int n; // Decision variables
    int* var; // 0..n-1 are nonbasic
    double* b; // b
    double* x; // x
    double* c; // c
    double** a; // A
};

double simplex(int m, int n, double** a, double* b, double* c, double* x, double y);
double xsimplex(int m, int n, double** a, double* b, double* c, double* x, double y, int* var, bool h);
void pivot(struct simplex_t* s, int row, int col);
int initial(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var);
int init(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var);
int select_nonbasic(struct simplex_t* s);

int main(int argc, char **args){
    int m, n;
    double *c, *b;
    double **a;

    scanf("%d %d", &m, &n);

    c = calloc(n, sizeof(double));
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
    double *xvalues = malloc(m * sizeof(double));
    for (int y = 0; y < m; y++) {
        printf("        ");
        xvalues[y] = 0.0;
        for (int x = 0; x < n; x++) {
            xvalues[y] += a[y][x];
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

    double result = simplex(m,n,a,b,c,xvalues,0.0);
    free(c);
    free(b);
    for (int i = 0; i < m; i++) {
        free(a[i]);
    }
    free(a);

    printf("Result: %lf\n", result);

    return 0;
}

int init(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var){
    int i,k;
    s->m = m;
    s->n = n;
    s->a = a;
    s->b = b;
    s->c = c;
    s->x = x;
    s->y = y;
    s->var = var;
    if(s->var == NULL){
        s->var = (int*)malloc((m+n+1)*sizeof(int));
        for(i = 0; i < m+n; i++){
            s->var[i] = i;
        }
    }
    for(k = 0, i = 1; i < m; i++){
        if(s->b[i] < s->b[k]){
            k = i;
        }
    }
    return k;
}

int initial(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var){
    init(s,m,n,a,b,c,x,y,var);
    return 1;
}

double simplex(int m, int n, double** a, double* b, double* c, double* x, double y){
    return xsimplex(m,n,a,b,c,x,y,NULL,false);
}

double xsimplex(int m, int n, double** a, double* b, double* c, double* x, double y, int* var, bool h){
    const double epsilon = pow(10.0,-6.0);
    struct simplex_t* s = (struct simplex_t*)malloc(sizeof(*s));
    int i, row, col;

    if(!initial(s,m,n,a,b,c,x,y,var)){
        free(s->var);
        return NAN;
    }
    while((col = select_nonbasic(s)) >= 0){
        row = -1;
        for(i = 0; i < m; i++){
            if(a[i][col] > epsilon && (row < 0 || b[i] / a[i][col] < b[row] / a[row][col])) {
                row = i;
            }
        }
        if(row  < 0){
            free(s->var);
            return INFINITY;
        }
        pivot(s,row,col);
    }
    if(h == 0){
        for(i = 0; i < n; i++){
            if(s->var[i] < n){
                x[s->var[i]] = 0;
            }
        }
        for(i = 0; i < m; i++){
            if(s->var[n+i] < n){
                x[s->var[n+1]] = s->b[i];
            }
        }
        free(s->var);
    }else {
        for(i = 0; i < n; i++){
            x[i] = 0;
        }
        for(i = n; i < n+m; i++){
            x[i] = s->b[i-n];
        }
    }
    return s->y;
}

int select_nonbasic(struct simplex_t* s){
    const double epsilon = pow(10.0,-6.0);
    for(int i = 0; i < s->n; i++){
        if(s->c[i] > epsilon){
            return i;
        }
    }
    return -1;
}

void pivot(struct simplex_t* s, int row, int col){
    double** a = s->a;
    double* b = s->b;
    double* c = s->c;
    int m = s->m;
    int n = s->n;
    int i,j,t;
    t = s->var[col];
    s->var[col] = s->var[n+row];
    s->var[n+row] = t;
    s->y = s->y + c[col]*b[row] / a[row][col];
    for(i = 0; i < n; i++){
        if(i != col){
            c[i] = c[i] - c[col] * a[row][i] / a[row][col];
        }
    }
    c[col] = -c[col] / a[row][col];
    for(i = 0; i < m; i++){
        if(i != row){
            b[i] = b[i] - a[i][col] * b[row] / a[row][col];
        }
    }
    for(i = 0; i < m; i++){
        if(i != row){
            for(j = 0; j < n; j++){
                if(j != col){
                    a[i][j] = a[i][j] - a[i][col] * a[row][j] / a[row][col];
                }
            }
        }
    }
    for(i = 0; i < m; i++){
        if(i != row){
            a[i][col] = -a[i][col] / a[row][col];
        }
    }
    for(i = 0; i < n; i++){
        if(i != col){
            a[row][i] = a[row][i] / a[row][col];
        }
    }
    b[row] = b[row] / a[row][col];
    a[row][col] = 1 / a[row][col];
}