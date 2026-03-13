import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import twisk.monde.*;
import twisk.outils.FabriqueNumero;

import static org.junit.jupiter.api.Assertions.*;

public class MondeTest {
    @BeforeEach
    void setUp(){
        FabriqueNumero.getInstance().reset();
    }

    @Test
    public void testConstructeur(){
        Monde monde = new Monde();
        assertNotNull(monde);
        assertNotNull(monde.getLesEtapes());
        assertNotNull(monde.getEntree());
        assertNotNull(monde.getSortie());
        assertNotNull(monde.getKitC());
        assertNotNull(monde.getSemaphores());
    }

    @Test
    public void testACommeEntree(){
        Monde monde1 = new Monde();
        Monde monde2 = new Monde();
        Monde monde3 = new Monde();
        Monde monde4 = new Monde();
        Monde monde5 = new Monde();
        Monde monde6 = new Monde();
        Monde monde7 = new Monde();

        Activite activite1 = new Activite("test");
        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");
        Guichet guichet1 = new Guichet("test");

        Activite activite2 = new Activite("Test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");
        Guichet guichet2 = new Guichet("test");

        monde1.aCommeEntree(activite1, activite2);
        assert(monde1.getEntree().nbSuccesseurs()==2): " Le nombre de succeseur doit être de 2 ";

        monde2.aCommeEntree(activiteRestreinte1, activiteRestreinte2);
        assert(monde2.getEntree().nbSuccesseurs()==2): " Le nombre de succeseur doit être de 2 ";

        monde3.aCommeEntree(guichet1, guichet2);
        assert(monde3.getEntree().nbSuccesseurs()==2): " Le nombre de succeseur doit être de 2 ";

        monde4.aCommeEntree(activite1, activiteRestreinte1);
        assert(monde4.getEntree().nbSuccesseurs()==2): " Le nombre de succeseur doit être de 2 ";

        monde5.aCommeEntree(activiteRestreinte1, guichet1);
        assert(monde5.getEntree().nbSuccesseurs()==2): " Le nombre de succeseur doit être de 2 ";

        monde6.aCommeEntree(activiteRestreinte1, guichet1, activiteRestreinte1);
        assert(monde6.getEntree().nbSuccesseurs()==3): " Le nombre de succeseur doit être de 3 ";

        assertThrows(IllegalArgumentException.class,() -> {monde7.aCommeEntree();});
    }


    @Test
    public void testACommeSortie() {
        // Création des instances de Monde
        Monde monde1 = new Monde();
        Monde monde2 = new Monde();
        Monde monde3 = new Monde();
        Monde monde4 = new Monde();
        Monde monde5 = new Monde();
        Monde monde6 = new Monde();
        Monde monde7 = new Monde();

        // Création des activités et guichets pour les tests
        Activite activite1 = new Activite("test");
        Activite activite2 = new Activite("test");
        Activite activite3 = new Activite("test");

        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");
        ActiviteRestreinte activiteRestreinte3 = new ActiviteRestreinte("test");
        ActiviteRestreinte activiteRestreinte4 = new ActiviteRestreinte("test");
        ActiviteRestreinte activiteRestreinte5 = new ActiviteRestreinte("test");
        ActiviteRestreinte activiteRestreinte6 = new ActiviteRestreinte("test");

        Guichet guichet1 = new Guichet("test");
        Guichet guichet2 = new Guichet("test");
        Guichet guichet3 = new Guichet("test");
        Guichet guichet4 = new Guichet("test");
        Guichet guichet5 = new Guichet("test");
        Guichet guichet6 = new Guichet("test");

        // Tests avec une seule sortie
        monde1.aCommeSortie(activite1);
        assert activite1.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        monde2.aCommeSortie(activiteRestreinte1);
        assert activiteRestreinte1.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        monde3.aCommeSortie(guichet1);
        assert guichet1.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        // Tests avec deux sorties
        monde1.aCommeSortie(activite2, activite3);
        assert activite2.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert activite3.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";


        monde2.aCommeSortie(activiteRestreinte2, activiteRestreinte3);
        assert activiteRestreinte2.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert activiteRestreinte3.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        monde3.aCommeSortie(guichet2, guichet3);
        assert guichet2.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert guichet3.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        // Tests combinés
        monde4.aCommeSortie(activiteRestreinte4, guichet4);
        assert activiteRestreinte4.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert guichet4.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        monde5.aCommeSortie(activiteRestreinte5, guichet5);
        assert activiteRestreinte5.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert guichet5.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        monde6.aCommeSortie(activiteRestreinte6, guichet6);
        assert activiteRestreinte6.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";
        assert guichet6.nbSuccesseurs() == 1 : "Le nombre de successeurs doit être de 1";

        assertThrows(IllegalArgumentException.class,() -> {monde7.aCommeSortie();});
    }

    @Test
    public void testAjouter(){
        Guichet guichet1 = new Guichet("test");
        Activite activite1 = new Activite("test");
        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");

        Guichet guichet2 = new Guichet("test");
        Activite activite2 = new Activite("test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");

        Guichet guichet3 = new Guichet("test");
        Activite activite3 = new Activite("test");
        ActiviteRestreinte activiteRestreinte3 = new ActiviteRestreinte("test");

        Guichet guichet4 = new Guichet("test");
        Activite activite4 = new Activite("test");
        ActiviteRestreinte activiteRestreinte4 = new ActiviteRestreinte("test");

        Monde monde = new Monde();
        assertEquals(2,monde.nbEtapes(),"Le nombre d'étapes est de 2");

        assertThrows(IllegalArgumentException.class,() -> {monde.ajouter();});

        monde.ajouter(guichet1);
        assertEquals(3,monde.nbEtapes(),"Le nombre d'étapes est de 3");
        monde.ajouter(activite1);
        assertEquals(4,monde.nbEtapes(),"Le nombre d'étapes est de 4");
        monde.ajouter(activiteRestreinte1);
        assertEquals(5,monde.nbEtapes(),"Le nombre d'étapes est de 5");

        monde.ajouter(activiteRestreinte2,guichet2);
        assertEquals(7,monde.nbEtapes(),"Le nombre d'étapes est de 7");

        monde.ajouter(guichet3, activite3);
        assertEquals(9,monde.nbEtapes(),"Le nombre d'étapes est de 9");

        monde.ajouter(activiteRestreinte4,guichet4, activite4);
        assertEquals(12,monde.nbEtapes(),"Le nombre d'étapes est de 2");
    }

    @Test
    public void testNbEtapes(){
        Monde monde = new Monde();
        Guichet guichet1 = new Guichet("test");
        Activite activite1 = new Activite("test");
        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");

        Guichet guichet2 = new Guichet("test");
        Activite activite2 = new Activite("test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");

        assertEquals(2,monde.nbEtapes(),"Le nombre d'étapes doit être de 2");

        monde.ajouter(guichet1,activite1,activiteRestreinte1);
        assertEquals(5,monde.nbEtapes(),"Le nombre d'étapes doit être de 5");

        monde.ajouter(guichet2,activite2,activiteRestreinte2);
        assertEquals(8,monde.nbEtapes(),"Le nombre d'étapes doit être de 8");

    }

    @Test
    public void testNbGuichets(){
        Monde monde = new Monde();
        Guichet guichet1 = new Guichet("test");
        Activite activite1 = new Activite("test");
        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");

        Guichet guichet2 = new Guichet("test");
        Activite activite2 = new Activite("test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");

        Guichet guichet3 = new Guichet("test");
        Activite activite3 = new Activite("test");
        ActiviteRestreinte activiteRestreinte3 = new ActiviteRestreinte("test");

        Guichet guichet4 = new Guichet("test");

        assert ( monde.nbGuichets() == 0 ): " Le nombre de guichet doit être de 0 ";
        monde.ajouter(guichet1);
        assert ( monde.nbGuichets() == 1 ): " Le nombre de guichet doit être de 1 ";

        monde.ajouter(activite1);
        monde.ajouter(activiteRestreinte1);
        assert ( monde.nbGuichets() == 1 ): " Le nombre de guichet doit être de 1 ";

        monde.ajouter(guichet2,guichet3);
        assert ( monde.nbGuichets() == 3): " Le nombre de guichet doit être de 3 ";

        monde.ajouter(activiteRestreinte2,guichet4, activiteRestreinte3,activite2, activite3);
        assert ( monde.nbGuichets() == 4 ): " Le nombre de guichet doit être de 4 ";

    }

    @Test
    public void testNbActivite(){
        Monde monde = new Monde();
        Guichet guichet1 = new Guichet("test");
        Activite activite1 = new Activite("test");
        ActiviteRestreinte activiteRestreinte1 = new ActiviteRestreinte("test");

        Guichet guichet2 = new Guichet("test");
        Activite activite2 = new Activite("test");
        ActiviteRestreinte activiteRestreinte2 = new ActiviteRestreinte("test");

        Guichet guichet3 = new Guichet("test");
        Activite activite3 = new Activite("test");
        ActiviteRestreinte activiteRestreinte3 = new ActiviteRestreinte("test");

        Guichet guichet4 = new Guichet("test");

        assertEquals(2,monde.nbActivite(),"Le nombre d'activités doit être de 2");
        monde.ajouter(guichet1);
        assertEquals(2,monde.nbActivite(),"Le nombre d'activités doit être de 2");

        monde.ajouter(activite1);
        monde.ajouter(activiteRestreinte1);
        assertEquals(4,monde.nbActivite(),"Le nombre d'activités doit être de 4");

        monde.ajouter(guichet2,guichet3);
        assertEquals(4,monde.nbActivite(),"Le nombre d'activités doit être de 4");

        monde.ajouter(activiteRestreinte2,guichet4, activiteRestreinte3,activite2, activite3);
        assertEquals(8,monde.nbActivite(),"Le nombre d'activités doit être de 8");

    }

    @Test
    void testToString(){
        Monde m = new Monde();
        Activite e1 = new Activite("E1");
        ActiviteRestreinte e2 = new ActiviteRestreinte("E2");
        Activite s1 = new Activite("S1");
        ActiviteRestreinte s2 = new ActiviteRestreinte("S2");
        m.aCommeEntree(e1,e2);
        Activite a =  new Activite("Etape 1");
        ActiviteRestreinte ar = new ActiviteRestreinte("Etape 2");
        Guichet g = new Guichet("Etape 3");
        m.ajouter(e1,e2,a,g,ar,s1,s2);
        e1.ajouterSuccesseur(a);
        e2.ajouterSuccesseur(a);
        a.ajouterSuccesseur(g);
        g.ajouterSuccesseur(ar);
        ar.ajouterSuccesseur(s1);
        ar.ajouterSuccesseur(s2);
        m.aCommeSortie(s1,s2);
        assertEquals("SASENTREE : 2 successeurs - E1 / E2\n" +
                "SASSORTIE : 0 successeur - \n" +
                "E1 : 1 successeur - Etape 1\n" +
                "E2 : 1 successeur - Etape 1\n" +
                "Etape 1 : 1 successeur - Etape 3\n" +
                "Etape 3 : 1 successeur - Etape 2\n" +
                "Etape 2 : 2 successeurs - S1 / S2\n" +
                "S1 : 1 successeur - SASSORTIE\n" +
                "S2 : 1 successeur - SASSORTIE\n",m.toString(),"toString mal fait");
    }

    @Test
    void testToC(){
        Monde m = new Monde();
        Activite a1 =  new Activite("E2",3,1);
        Guichet g1 = new Guichet("G3");
        ActiviteRestreinte a2 = new ActiviteRestreinte("ER4",2,1);
        Activite a3 =  new Activite("E5",8,5);
        m.aCommeEntree(a1);
        m.ajouter(a1,g1,a2,a3);
        a1.ajouterSuccesseur(g1);
        g1.ajouterSuccesseur(a2);
        a2.ajouterSuccesseur(a3);
        m.aCommeSortie(a3);
        assertEquals("""
                #include <stdlib.h>
                #include <stdio.h>
                #include <time.h>
                #include <unistd.h>
                #include "def.h"
                
                #define SASENTREE 0
                #define SASSORTIE 1
                #define E2 2
                #define G3 3
                #define ER4 4
                #define E5 5
                #define NBETAPES 6
                #define NBGUICHETS 1
                #define SEMAPHORE_GUICHET1 1
                
                int* getSemaphoresGuichets(){
                int* tab = malloc(sizeof(int)*NBGUICHETS);
                tab[0] = SEMAPHORE_GUICHET1;
                return tab;
                }
                
                int getNbEtapes(){
                return NBETAPES;
                }
                
                int getNbGuichets(){
                return NBGUICHETS;
                }
                
                void simulation(int ids){
                
                int nb;
                srand(time(NULL) * getpid()) ;
                nb = (int) ((rand()/(float) RAND_MAX)*1);
                entrer(SASENTREE);
                delai(4,1);
                switch(nb){
                case 0:
                transfert(SASENTREE,E2);
                break;
                }
                delai(3,1);
                switch(nb){
                case 0:
                transfert(E2,G3);
                break;
                }
                P(ids,SEMAPHORE_GUICHET1);
                transfert(G3,ER4);
                delai(2,1);
                V(ids,SEMAPHORE_GUICHET1);
                switch(nb){
                case 0:
                transfert(ER4,E5);
                break;
                }
                delai(8,5);
                switch(nb){
                case 0:
                transfert(E5,SASSORTIE);
                break;
                }
                
                }
                """,m.toC(),"La toC marche pas");
    }

    @Test
    void testGetEtape(){
        Monde m = new Monde();
        assertThrows(IllegalArgumentException.class, () -> m.getEtape(-1));
        assertEquals(m.getEntree(),m.getEtape(0),"La première étape est censé être le sas d'entrée");
        assertEquals(m.getSortie(),m.getEtape(1),"La deuxième étape est censé être le sas de sortie");
        assertThrows(IndexOutOfBoundsException.class, () -> m.getEtape(2));
        Activite a = new Activite("E2",3,1);
        m.ajouter(a);
        assertEquals(a,m.getEtape(2),"La troisième étape est censé être l'activité qu'on viens de créer et ajouté au monde");
    }
}
