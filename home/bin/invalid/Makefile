all:
	gcc -o generate generate.c -Wall -ansi -pedantic
	./generate < invalid.txt > invalid.c
	gcc -c -o invalid.o invalid.c

clean:
	rm -f generate invalid.c invalid.o
