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

struct node_t {
    int m; /* Constraints. */
    int n; /* Decision variables. */
    int k; /* Parent branches on x k . */
    int h; /* Branch on x h . */
    double xh; /* x h . */
    double ak; /* Parent a k . */
    double bk; /* Parent b k . */
    double* min; /* Lower bounds. */
    double* max; /* Upper bounds. */
    double** a; /* A . */
    double* b; /* b . */
    double* x; /* x . */
    double* c; /* c . */
    double z; /* z. */
};

struct set_element {
    struct set_element *next;
    struct node_t *node;
};


struct set {
    struct set_element *head;
};

struct set_element *create_element(struct node_t *node) {
    struct set_element *element = (struct set_element *)malloc(sizeof(struct set_element));

    element->next = NULL;
    element->node = node;

    return element;
}

void push(struct set *set, struct node_t *node) {
    struct set_element *element = set->head;

    if (element == NULL) {
        element = create_element(node);
        set->head = element;
        return;
    }

    while (1) {
        if (element->node == node) {
            return;
        }

        if (element->next != NULL) {
            element = element->next;
        } else {
            break;
        }
    }

    struct set_element *new_element = create_element(node);
    element->next = new_element;
}

struct node_t *pop(struct set *set) {
    struct node_t *node = set->head->node;
    struct set_element *element = set->head;
    set->head = set->head->next;

    free(element);
    return node;
}

double simplex(int m, int n, double** a, double* b, double* c, double* x, double y);
double xsimplex(int m, int n, double** a, double* b, double* c, double* x, double y, int* var, bool h);
void pivot(struct simplex_t* s, int row, int col);
int initial(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var);
int init(struct simplex_t* s, int m, int n, double** a, double* b, double* c, double* x, double y, int* var);
void prepare(struct simplex_t* s, int k);
int select_nonbasic(struct simplex_t* s);
struct node_t *initial_node(int m, int n, double** a, double* b, double* c);
struct node_t *extend(struct node_t *p, int m, int n, double **a, double *b, double *c, int k, double ak, double bk);
int is_integer(double* xp);
int integer(struct node_t* p);
void bound(struct node_t* p, struct set *h, double* zp, double* x);
int branch(struct node_t* q, double z);
double intopt(int m, int n, double **a, double *b, double *c, double *x);
void free_node(struct node_t* node);

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

    //double result = simplex(m,n,a,b,c,xvalues,0.0);
    double result = intopt(m, n, a, b, c, xvalues);
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

/* START OF SIMPLEX */

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

/* END OF SIMPLEX */

/* START OF BRANCH AND BOUND */

struct node_t *initial_node(int m, int n, double** a, double* b, double* c) {
    struct node_t* p = (struct node_t*)malloc(sizeof(struct node_t));
    p->a = calloc(m + 1, sizeof(double*));
    for(int i = 0; i < m+1; i++){
        p->a[i] = calloc(n + 1, sizeof(double));
    }
    p->b = calloc(m + 1, sizeof(double));
    p->c = calloc(n + 1, sizeof(double));
    p->x = calloc(n + 1, sizeof(double));
    p->min = calloc(n, sizeof(double));
    p->max = calloc(n, sizeof(double));
    p->m = m;
    p->n = n;

    for(int i = 0; i < m; i++){
        for(int j = 0; j < n+1; j++){
            p->a[i][j] = a[i][j];
        }
    }

    for(int i = 0; i < m; i++){
        p->b[i] = b[i];
    }

    for(int i = 0; i < n; i++){
        p->c[i] = c[i];
    }

    for(int i = 0; i < n; i++){
        p->min[i] = -INFINITY;
        p->max[i] = INFINITY;
    }

    return p;
}

struct node_t *extend(struct node_t *p, int m, int n, double **a, double *b, double *c, int k, double ak, double bk) {
    struct node_t *q = (struct node_t *)malloc(sizeof(struct node_t));
    q->k = k;
    q->ak = ak;
    q->bk = bk;

    if (ak > 0 && p->max[k] < INFINITY) {
        q->m = p->m;
    } else if (ak < 0 && p->min[k] > 0) {
        q->m = p->m;
    } else {
        q->m = p->m + 1;
    }

    q->n = p->n;
    q->h = -1;
    q->a = (double **)calloc(q->m + 1, sizeof(double*)); // note normally q.m > m

    for(int i = 0; i < q->m + 1; i++) {
        q->a[i] = (double *)calloc(q->n + 1, sizeof(double));
    }

    q->b = (double *)calloc(q->m + 1, sizeof(double));
    q->c = (double *)calloc(q->n + 1, sizeof(double));
    q->x = (double *)calloc(q->n + 1, sizeof(double));
    q->min = (double *)calloc(n, sizeof(double));
    q->max = (double *)calloc(n, sizeof(double));

    for (int i = 0; i < n; i++) {
        q->min[i] = p->min[i];
        q->max[i] = p->max[i];
    }

    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            q->a[i][j] = p->a[i][j];
        }
    }

    for (int i = 0; i < m; i++) {
        q->b[i] = b[i];
    }

    for (int i = 0; i < n; i++) {
        q->c[i] = c[i];
    }

    if (ak > 0) {
        if ((isinf(q->max[k]) && q->max[k] > 0) || (bk < q->max[k])) {
            q->max[k] = bk;
        }
    } else if ((isinf(q->min[k]) && q->min[k] < 0) || (-bk > q->min[k])) {
        q->min[k] = -bk;
    }

    for (int i = m, j = 0; j < n; j++) {
        if (!isinf(q->min[j]) || q->min[j] > 0.0) {
            q->a[i][j] = -1;
            q->b[i] = -q->min[j];
            i++;
        }
        if (!isinf(q->max[j]) || q->max[j] < 0.0) {
            q->a[i][j] = 1;
            q->b[i] = q->max[j];
            i++;
        }
    }

    return q;
}

int is_integer(double* xp){
    double x = *xp;
    double r = round(x);
    if (abs(r-x) < EPSILON) {
        *xp = r;
        return 1;
    } else {
        return 0;
    }
}

int integer(struct node_t* p){
    for (int i = 0; i < p->n; i++) {
        if (!is_integer(&p->x[i])) {
            return 0;
        }
    }
    return 1;
}

void bound(struct node_t* p, struct set *h, double* zp, double* x) {
    if (p->z > *zp) {
        *zp = p->z;
        for(int i = 0; i < p->n; i++){
            x[i] = p->x[i];
        }
        // remove and delete all nodes q in h with q.z < p.z

        while (h->head != NULL && h->head->node->z < p->z) {
            struct set_element *head = h->head;
            h->head = h->head->next;
            free(head->node);
            free(head);
        }

        struct set_element *element_prev = h->head;
        struct set_element *element;

        if (h->head == NULL) {
            return;
        } else {
            element = h->head->next;
        }

        while (element != NULL) {
            struct node_t *q = element->node;
            if (q->z < p->z) {
                element_prev->next = element->next;
                free_node(q);
                free(element);
                element = element_prev->next;
            } else {
                element_prev = element;
                element = element->next;
            }
        }
    }
}

int branch(struct node_t* q, double z) {
    double min, max;

    if (q->z < z) {
        return 0;
    }

    for (int h = 0; h < q->n; h++) {
        if (!is_integer(&q->x[h])) {
            if (isinf(q->min[h]) && q->min[h] < 0.0) {
                min = 0.0;
            } else {
                min = q->min[h];
            }
            max = q->max[h];

            if (floor(q->x[h]) < min || ceil(q->x[h]) > max) {
                continue;
            }

            q->h = h;
            q->xh = q->x[h];

            for(int i = 0; i < q->m + 1; i++) {
                free(q->a[i]);
            }
            free(q->a);
            free(q->b);
            free(q->c);
            free(q->x);

            return 1;
        }
    }

    return 0;
}

void succ(struct node_t *p, struct set *h, int m, int n, double** a, double* b, double* c, int k, double ak, double bk, double* zp, double* x){
    struct node_t* q = extend(p,m,n,a,b,c,k,ak,bk);
    if (q == NULL) {
        return;
    }
    q->z = simplex(q->m, q->n, q->a, q->b, q->c, q->x, 0);
    if (isfinite(q->z)) {
        if (integer(q)) {
            bound(q,h,zp,x);
        } else if(branch(q, *zp)) {
            push(h, q);
            return;
        }
    }
    free_node(q);
}

double intopt(int m, int n, double **a, double *b, double *c, double *x) {
    struct node_t *p = initial_node(m, n, a, b, c);
    struct set h;
    h.head = create_element(p);
    double z = -INFINITY;
    p->z = simplex(p->m, p->n, p->a, p->b, p->c, p->x, 0);
    if(integer(p) || !isfinite(p->z)){
        z = p->z;
        if(integer(p)){
            for(int i = 0; i < n; i++){
                x[i] = p->x[i];
            }
        }
        free_node(p);
        free(h.head);
        return z;
    }
    branch(p,z);
    while(h.head != NULL) {
        struct node_t *p = pop(&h);
        succ(p, &h, m, n, a, b, c, p->h, 1, floor(p->xh), &z, x);
        succ(p, &h, m, n, a, b, c, p->h, -1, -ceil(p->xh), &z, x);
        free_node(p);

    }

    if (z == -INFINITY) {
        return NAN;
    } else {
        return z;
    }
}

void free_node(struct node_t* node){
    free(node->min);
    free(node->max);
    free(node->b);
    free(node->x);
    free(node->c);

    for(int i = 0; i < node->m + 1; i++){
        free(node->a[i]);
    }
    free(node->a);
    free(node);
}
