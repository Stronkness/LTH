CC	= cc
CFLAGS	= -pedantic -Wall -Werror -g -Os -funsigned-char
OUT	= a.out
FILE	= freq

execute: compile
	./a.out < input > output
	diff output correct
	./a.out < input.z > output
	diff output correct.z

compile: clean $(FILE).o malloc.o
	$(CC) freq.o malloc.o

$(FILE).o: $(FILE).c
	$(CC) -include wecc.h -c $(FILE).c $(CFLAGS)

malloc.o: malloc.c
	$(CC) -c malloc.c $(CFLAGS) 

clean:
	rm -f *.s *.o $(OUT) $(NOTE) result score output
