#include <stdarg.h>
#include <stdlib.h>
#include <stdio.h>

#include "error.h"

void (error)(char* file, int line, const char* func, char* msg, ...)
{
	va_list		ap;
	static char	colon[] = ": ";

	if (progname == NULL) {
		progname = "";
		colon[0] = 0;
	}

	va_start(ap, msg);

	fprintf(stderr, "%s%serror: detected in file \"%s\", line %u"
		" in function \"%s\": ", progname, colon, file, line, func);

	vfprintf(stderr, msg, ap);

	va_end(ap);

	fputc('\n', stderr);

	exit(EXIT_FAILURE);
}
