#include <stdio.h>
#include "timebase.h"

#define N (1024)

#define TYPE	float

TYPE a[N*N];
TYPE b[N*N];
TYPE c[N*N];

void matmul()
{
	size_t	i, j, k;

	TYPE* p_a = a;
	TYPE* end_a = &a[N*N];
	TYPE* p_b = b;
	TYPE* end_b = &b[N*N];
	TYPE* p_c = c;
	TYPE* end_c = &c[N*N];

	while(p_a < end_a){
		while(p_b < end_b){
			while(p_c < end_c){
				*p_a++ = *p_b++ * *p_c++;
			}
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
