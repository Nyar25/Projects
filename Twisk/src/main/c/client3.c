#include <stdio.h>
#include <stdlib.h>
#include "../ressources/codeC/def.h"

#define SASENTREE 0
#define SASSORTIE 1
#define ACTIVITE1 2
#define GUICHET1 3
#define SEMAPHORE_GUICHET1 1
#define ACTIVITE3 4 
#define NBETAPES 5
#define NBGUICHETS 1

int getNbEtapes(){
    return NBETAPES;
}

int getNbGuichets(){
    return NBGUICHETS;
}

int* getSemaphoresGuichets(){
    int* tab = malloc(sizeof(int)*NBGUICHETS);
    tab[0] = SEMAPHORE_GUICHET1;
    return tab;
}

void simulation (int ids){
    entrer(SASENTREE);
    delai(4,2);
    transfert(SASENTREE,ACTIVITE1);
    delai(6,1);
    transfert(ACTIVITE1,GUICHET1);
    P(ids,SEMAPHORE_GUICHET1);
    transfert(GUICHET1,ACTIVITE3);
    delai(5,2);
    V(ids,SEMAPHORE_GUICHET1);
    transfert(ACTIVITE3,SASSORTIE);
}