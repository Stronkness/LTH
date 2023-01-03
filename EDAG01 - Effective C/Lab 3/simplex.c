#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>

#define EPSILON 0.000001

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
void prepare(struct simplex_t* s, int k);
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
        a[i] = calloc(n + 1, sizeof(double));

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

    double *xvalues = calloc(n + m + 1, sizeof(double));

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
    free(xvalues);

    printf("Result: %lf\n", result);

    return 0;
}

void print_simplex(struct simplex_t* s) {
    printf("m = %d, n = %d\n", s->m, s->n);
    printf("max z = ");
    for (int i = 0; i < s->n; i++) {
        printf("%10.3lf", s->c[i]);
        if (i < s->n - 1) {
            printf(" + ");
        } else {
            printf("\n");
        }
    }

    for (int y = 0; y < s->m; y++) {
        printf("        ");
        for (int x = 0; x < s->n; x++) {
            printf("%10.3lf", s->a[y][x]);
            if (x < s->n - 1) {
                printf(" + ");
            } else {
                printf(" \u2264 %lf", s->b[y]);
            }
        }
        printf("\n");
    }
    printf("\n");
}

int init(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var){
    int	local_array[10];
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
    for (i = 0; i < 11; i += 1){
	    local_array[i] = i;
    }
    return k;
}

void prepare(struct simplex_t* s, int k){
    int m = s->m;
    int n = s->n;
    int i;
    // make room for x_{m+n} at s.var[n] by moving s.var[n..n+m-1] one step to the right
    for(i = m + n; i > n; i--){
        s->var[i] = s->var[i-1];
    }
    s->var[n] = m+n;
    // add x_{m+n} to each constraint
    n++;
    for(i = 0; i < m; i++){
        s->a[i][n-1] = -1;
    }
    s->x = (double*)calloc((m + n), sizeof(double));
    s->c = (double*)calloc(n, sizeof(double));
    s->c[n-1] = -1;
    s->n = n;
    pivot(s,k,n-1);
}

int initial(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var){
    int i,j,k;
    double w;
    double* t; // This could cause issues, unknown variable
    k = init(s,m,n,a,b,c,x,y,var);
    if(b[k] >= 0){
        return 1;
    }
    prepare(s,k);
    n = s->n;
    s->y = xsimplex(m, n, s->a, s->b, s->c, s->x, 0.0, s->var, 1);
    for(i = 0; i < m + n; i++){
        if(s->var[i] == m + n - 1) {
            if(fabs(s->x[i]) > EPSILON) {
                free(s->x);
                free(s->c);
                return 0;
            } else {
                break;
            }
        }
    }
    if(i >= n){
        // x_{n+m} is basic. find good nonbasic
        for(j = k = 0; k < n; k++){
            if(fabs(s->a[i-n][k]) > fabs(s->a[i-n][j])){
                j = k;
            }
        }
        pivot(s,i-n,j);
        i = j;
    }
    if(i < n-1){
        // x_{n+m} is nonbasic and not last. swap columns i and n-1
        k = s->var[i];
        s->var[i] = s->var[n-1];
        s->var[n-1] = k;
        for(k = 0; k < m; k++){
            w = s->a[k][n-1];
            s->a[k][n-1] = s->a[k][i];
            s->a[k][i] = w;
        }
    }

    free(s->c);
    s->c = c;
    s->y = y;
    for(k = n-1; k < n+m-1; k++){
        s->var[k] = s->var[k+1];
    }
    n = s->n = s->n-1;
    t = (double*) calloc(n, sizeof(double));
    for(k = 0; k < n; k++) {
        for(j = 0; j < n; j++){
            if(k == s->var[j]){
                // x_k is nonbasic. add c_k
                t[j] = t[j] + s->c[k];
                goto next_k;
            }
        }

        // x_k is basic
        for(j = 0; j < m; j++){
            if(s->var[n+j] == k){
                // x_k is at row j
                break;
            }
        }
        s->y = s->y + s->c[k]*s->b[j];
        for(i = 0; i < n; i++){
            t[i] = t[i] - s->c[k]*s->a[j][i];
        }
    next_k:
        ;
    }
    for(i = 0; i < n; i++){
        s->c[i] = t[i];
    }
    free(t);
    free(s->x);
    return 1;
}

double simplex(int m, int n, double** a, double* b, double* c, double* x, double y){
    return xsimplex(m,n,a,b,c,x,y,NULL,false);
}

double xsimplex(int m, int n, double** a, double* b, double* c, double* x, double y, int* var, bool h){
    struct simplex_t s;
    int i, row, col;

    if(!initial(&s,m,n,a,b,c,x,y,var)){
        free(s.var);
        return NAN;
    }
    print_simplex(&s);
    while((col = select_nonbasic(&s)) >= 0){
        row = -1;
        for(i = 0; i < m; i++){
            if(a[i][col] > EPSILON && (row < 0 || b[i] / a[i][col] < b[row] / a[row][col])) {
                row = i;
            }
        }
        if(row < 0){
            free(s.var);
            return INFINITY;
        }
        pivot(&s,row,col);
        print_simplex(&s);
    }
    if(h == 0){
        for(i = 0; i < n; i++){
            if(s.var[i] < n){
                x[s.var[i]] = 0;
            }
        }
        for(i = 0; i < m; i++){
            if (s.var[n + i] < n) {
                x[s.var[n + i]] = s.b[i];
            }
        }
        free(s.var);
    } else {
        for(i = 0; i < n; i++){
            x[i] = 0;
        }
        for(i = n; i < n+m; i++){
            x[i] = s.b[i-n];
        }
    }
    double result = s.y;
    return result;
}

int select_nonbasic(struct simplex_t* s){
    for(int i = 0; i < s->n; i++){
        if(s->c[i] > EPSILON){
            return i;
        }
    }
    return -1;
}

void pivot(struct simplex_t* s, int row, int col) {
    printf("Row: %d, column: %d\n", row, col);
    double** a = s->a;
    double* b = s->b;
    double* c = s->c;
    int m = s->m;
    int n = s->n;
    int i, j, t;
    t = s->var[col];
    s->var[col] = s->var[n+row];
    s->var[n+row] = t;
    s->y = s->y + c[col]*b[row] / a[row][col];
    for(i = 0; i < n; i++){
        if(i != col){
            c[i] -= (c[col] * (a[row][i] / a[row][col]));
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
    for (int i = 0; i < n; i++) {
        printf("%lf ", c[i]);
    }
    b[row] = b[row] / a[row][col];
    a[row][col] = 1 / a[row][col];
}