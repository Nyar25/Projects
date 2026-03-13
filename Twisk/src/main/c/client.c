#include <stdio.h>
#include <stdlib.h>
#include "../ressources/codeC/def.h"

#define SASENTREE 0
#define SASSORTIE 1
#define ACTIVITE 2
#define NBETAPES 3
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
    transfert(SASENTREE,ACTIVITE);
    delai(5,2);
    transfert(ACTIVITE,SASSORTIE);
}