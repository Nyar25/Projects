#include <stdio.h>
#include <stdlib.h>
#include "../ressources/codeC/def.h"

#define SEMAPHORE_GUICHET1 1
#define SEMAPHORE_GUICHET2 2
#define SEMAPHORE_GUICHET3 3

#define SASENTREE 0
#define SASSORTIE 1
#define ACTIVITE1 2
#define GUICHET1 3
#define ACTIVITE2 4 
#define GUICHET2 5
#define ACTIVITE3 6
#define GUICHET3 7
#define NBETAPES 8
#define NBGUICHETS 3

int getNbEtapes(){
    return NBETAPES;
}

int getNbGuichets(){
    return NBGUICHETS;
}

void simulation (int ids){
    entrer(SASENTREE);
    transfert(SASENTREE,GUICHET1);
    P(ids,SEMAPHORE_GUICHET1);
    transfert(GUICHET1,ACTIVITE1);
    delai(4,2);
    V(ids,SEMAPHORE_GUICHET1);
    transfert(ACTIVITE1,GUICHET2);
    P(ids,SEMAPHORE_GUICHET2);
    transfert(GUICHET2,ACTIVITE2);
    delai(6,1);
    V(ids,SEMAPHORE_GUICHET2);
    transfert(ACTIVITE2,GUICHET3);
    P(ids,SEMAPHORE_GUICHET3);
    transfert(GUICHET3,ACTIVITE3);
    delai(5,4);
    V(ids,SEMAPHORE_GUICHET3);
    transfert(ACTIVITE3,SASSORTIE);
}