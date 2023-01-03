#define N	(100)

double		a[N];
double		b[N];
double		c[N];

int main()
{
	int	i;

	for (i = 1; i < N; i += 1)
		a[i] = a[i-1] * b[i] + c[i];
}
