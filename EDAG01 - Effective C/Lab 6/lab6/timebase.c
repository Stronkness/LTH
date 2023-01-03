#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <ctype.h>

#define PPC64

unsigned long long tbr32(void);
unsigned long long tbr64(void);

static unsigned long long timebase_frequency;
static unsigned long long cpu_frequency;

void ao() { }

void init_timebase(void)
{
	FILE*		fp;
	char*		file = "/proc/cpuinfo";
	char		line[BUFSIZ];
	char*		s;
	char*		t;
	double		f;	/* for reading the CPU frequency. */

	errno = 0;

	fp = fopen(file, "r");

	if (fp == NULL) {
		fprintf(stderr, "cannot open \"%s\" for reading: ", file);
		perror(0);
		fprintf(stderr, "\n");
	} else {
		while (fgets(line, BUFSIZ, fp) != NULL) {
			if (strncmp(line, "clock", 5) == 0
				&& (s = strchr(line, ':')) != NULL) {
				
				s += 1; /* skip ':' */
 
				while (!isdigit(*s) && *s != 0)
					s += 1;

				if (!isdigit(*s)) {
					fprintf(stderr, "expected a digit when reading %s line for clock\n", 
						file);
					return;
				}

				errno = 0;

				f = strtod(s, &t);				

				if (errno != 0) {
					fprintf(stderr, "could not read clock value from input: %s\n", s);
					f = 0;
				}

				if (strncmp(t, "MHz", 3) == 0)
					f *= 1e6;
				else if (strncmp(t, "GHz", 3) == 0)
					f *= 1e9;
				else
					fprintf(stderr, "warning: could not read CPU frequency unit from: %s\n", s);
			//	fprintf(stderr, "init_timebase: CPU frequency: %1.1lf Hz\n", f);
				cpu_frequency = f;

			} else if (strncmp(line, "timebase", 8) == 0
				&& (s = strchr(line, ':')) != NULL) {
				
ao();
				s += 1; /* skip ':' */
 
				while (!isdigit(*s) && *s != 0)
					s += 1;

				if (!isdigit(*s)) {
					fprintf(stderr, "expected a digit when reading %s line for timebase\n", 
						file);
					return;
				}

				errno = 0;
				timebase_frequency = atoll(s);				
				if (errno != 0) {
					fprintf(stderr, "could not read timebase value from input: %s\n", s);
					timebase_frequency = 0;
				}
			}
		}

		fclose(fp);
	}

	//printf("%s: timebase_frequency = %llu and cpu_frequency = %llu\n", __func__, timebase_frequency, cpu_frequency);
}

unsigned long long timebase()
{
#if defined PPC64
	return tbr64();
#elif defined PPC32
	return tbr32();
#else
#error PPC32 or PPC64 must be defined
#endif
}

double timebase_sec(void)
{
	unsigned long long	t = timebase();
	double			s;
	s = timebase() / (double)timebase_frequency;
	return s;
}

double timebase_cycles(unsigned long long timebase_count)
{
	return timebase_count * (double)cpu_frequency / (double)timebase_frequency;
}
