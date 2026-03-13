client : client.c
	gcc -Wall -fPIC -c client.c

client2 : client2.c 
	gcc -Wall -fPIC -c client2.c

client3 : client3.c
	gcc -Wall -fPIC -c client3.c

libTwisk : client.o
	gcc -shared ../ressources/codeC/programmeC.o client.o -o libTwisk.so

libTwisk2 : client2.o
	gcc -shared ../ressources/codeC/programmeC.o client2.o -o libTwisk.so

libTwisk3 : client3.o
	gcc -shared ../ressources/codeC/programmeC.o client3.o -o libTwisk.so

main : main.c 
	gcc -Wall -L. main.c -o main -lTwisk