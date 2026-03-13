#include <stdlib.h>
#include <stdio.h>
#include "../ressources/codeC/def.h"
#include "client.h"

#define NBCLIENTS 5
#define REFRESH 2

int main(int argc, char** argv){

    // Appel au define NBETAPES du client 
    int nbEtapes = getNbEtapes();

    // Appel au define NBGUICHETS du client
    int nbGuichets = getNbGuichets();

    // Appel au Semaphore des guicehts 
    int* jetons_guichets = getSemaphoresGuichets();

    // Appel de StartSimulatoin défini dans le co 
    int* pids = start_simulation(nbEtapes,nbGuichets,NBCLIENTS,jetons_guichets);

    // Affichage des Pids 
    printf("les clients : ");
    for (int i = 0; i < NBCLIENTS; i++){
        printf("%d ",pids[i]);
    }
    printf("\n");    

    int* tab2 = ou_sont_les_clients(nbEtapes,NBCLIENTS);
    
    // Tant que les clients n'ont pas atteint la dernière étape : 
    while(tab2[NBCLIENTS+1] < NBCLIENTS){
    
        // Maj des positions client 
        tab2 = ou_sont_les_clients(nbEtapes,NBCLIENTS);
        
        // Pour l'ensemble des étapes
        for (int i = 0 ; i < nbEtapes ; i++){

            // Permet de trouver la position d'une étape dans tab2 
            int ind = i * (NBCLIENTS +1) ;
        
            // Affichage des étapes 
            if(i==0){
                printf("Sas entree : %d client", tab2[ind]);
            } else if(i==1){
                printf("Sas sortie : %d client", tab2[ind]);
            } else {
                printf("etape %d : %d client", i-1 , tab2[ind]);
            }

            if(tab2[ind] > 1){
                printf("s : ");
            } else {
                printf(" : ");
            }
            // Affichage des ID à chaque étape
            if( tab2[ind] > 0){
                // j = Premier client de l'étape. 
                // j <= ind + tab[ind] = Dernier client de l'étape
                for (int j = ind + 1 ; j <= ind + tab2[ind] ; j++){
                    printf("%d ",tab2[j]);
                }
            }
            printf("\n");
        }
        printf("\n");
        sleep(REFRESH);
    }

    nettoyage();

    return 0; 

}