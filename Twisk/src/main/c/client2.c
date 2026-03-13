#include <stdio.h>
#include <stdlib.h>
#include "../ressources/codeC/def.h"

#define SASENTREE 0
#define SASSORTIE 1
#define ACTIVITE1 2
#define ACTIVITE2 3
#define ACTIVITE3 4 

#define NBETAPES 5
#define NBGUICHETS 0

int getNbEtapes(){
    return NBETAPES;
}

int getNbGuichets(){
    return NBGUICHETS;
}

int* getSemaphoresGuichets(){
    return NULL;
}

void simulation (int ids){
    entrer(SASENTREE);
    delai(4,2);
    transfert(SASENTREE,ACTIVITE1);
    delai(6,1);
    transfert(ACTIVITE1,ACTIVITE2);
    delai(5,4);
    transfert(ACTIVITE2,ACTIVITE3);
    delai(5,2);
    transfert(ACTIVITE3,SASSORTIE);
}