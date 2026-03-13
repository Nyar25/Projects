import org.junit.jupiter.api.Test;
import twisk.monde.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SasEntreeTest extends ActiviteTest{
    @Test
    void testToString(){
        SasEntree sas = new SasEntree();
        sas.ajouterSuccesseur(new Activite("Activité"),new Guichet("Guichet"));
        assertEquals("SASENTREE : 2 successeurs - Activite / Guichet",sas.toString(),"Fonction toString mal faite");
    }

    @Test
    void testToC(){
        SasEntree sas = new SasEntree();
        Activite a1 = new Activite("Activité1");
        Activite a2 = new Activite("Activite2");
        SasSortie sortie = new SasSortie();
        sas.ajouterSuccesseur(a1);
        a1.ajouterSuccesseur(a2);
        a2.ajouterSuccesseur(sortie);
        String res = "int nb;\n" +
                "srand(time(NULL) * getpid()) ;\n" +
                "nb = (int) ((rand()/(float) RAND_MAX)*1);\n" +
                "entrer(SASENTREE);\n" +
                "delai(4,1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(SASENTREE,Activite1);\n" +
                "break;\n" +
                "}\n" +
                "delai(4,1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activite1,Activite2);\n" +
                "break;\n" +
                "}\n" +
                "delai(4,1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activite2,SASSORTIE);\n" +
                "break;\n" +
                "}\n";
        assertEquals(res,sas.toC(),"Fonction toC mal faite");
    }
}
