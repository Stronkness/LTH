#ifndef edaa25_h

#define edaa25_h

#include <stddef.h>

#define malloc __check_malloc
#define calloc __check_calloc
#define realloc __check_realloc
#define free __check_free

void* __check_malloc(size_t);
void* __check_calloc(size_t, size_t);
void* __check_realloc(void*, size_t);
void  __check_free(void*);

#endif
