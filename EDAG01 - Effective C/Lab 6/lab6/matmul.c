#include <stdio.h>
#include "timebase.h"

#define N (1024)

#define TYPE	float

TYPE a[N][N];
TYPE b[N][N];
TYPE c[N][N];

void matmul()
{
	size_t	i, j, k;

	for (i = 0; i < N; i += 1) {
		for (j = 0; j < N; j += 1) {
			a[i][j] = 0;
			for (k = 0; k < N; k += 1)
				a[i][j] += b[i][k] * c[k][j];
		}
	}
}

int main()
{
	double	begin;
	double	end;

	init_timebase();

	begin = timebase_sec();
	matmul();
	end = timebase_sec();

	printf("T = %1.3lf s\n", end-begin);

	return 0;
}
