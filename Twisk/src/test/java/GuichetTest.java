import org.junit.jupiter.api.*;
import twisk.monde.*;
import twisk.outils.FabriqueNumero;

import static org.junit.jupiter.api.Assertions.*;

public class GuichetTest extends EtapeTest{
    @BeforeEach
    void setUp() {
        FabriqueNumero.getInstance().reset();
    }

    @Test
    void testConstructeur2(){
        assertThrows(IllegalArgumentException.class, () -> {Guichet g = new Guichet("GuichetThrows",0);});
        Guichet g = new Guichet("Guichet",9);
        assertEquals("Guichet",g.getNom(),"Nom pas instancier");
        assertEquals(9,g.getNbJetons(),"Jetons pas instancier");
    }

    @Test
    void testToString(){
        Guichet ac = new Guichet("Guichet 1");
        ac.ajouterSuccesseur(new Guichet("Guichet 2"));
        assertEquals("Guichet 1 : 1 successeur - Guichet 2",ac.toString(),"Fonction toString mal faite");
    }

    @Test
    void testSemaphore(){
        FabriqueNumero.getInstance().reset();
        Guichet activiteres0 = new Guichet("Guichet 1");
        Guichet activite1 = new Guichet ("Guichet 2");

        assert (activiteres0.getNbSemaphore() == 1 ):"Le numero doit être de 1";
        assert (activite1.getNbSemaphore() == 2):"Le numero doit être de 2";
    }

    @Test
    public void toCtest(){
        SasEntree e = new SasEntree();
        SasSortie s = new SasSortie();
        Activite activite1 = new Activite("Activite 1");
        Guichet guichet1 = new Guichet("Guichet1");
        ActiviteRestreinte Activiterestreinte1 = new ActiviteRestreinte("Activiterestreinte1");
        Activite activite2 = new Activite("Activite2");

        e.ajouterSuccesseur(activite1);
        activite1.ajouterSuccesseur(guichet1);
        guichet1.ajouterSuccesseur(Activiterestreinte1);
        Activiterestreinte1.ajouterSuccesseur(activite2);
        activite2.ajouterSuccesseur(s);

        String res = "P(ids,SEMAPHORE_GUICHET1);\n" +
                "transfert(Guichet1,Activiterestreinte1);\n" +
                "delai(4,1);\n" +
                "V(ids,SEMAPHORE_GUICHET1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activiterestreinte1,Activite2);\n" +
                "break;\n" +
                "}\n" +
                "delai(4,1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activite2,SASSORTIE);\n" +
                "break;\n" +
                "}\n";

        assertEquals(res,guichet1.toC(),"La fonction toC marche pas comme voulu");
    }

    @Test
    void testGetTemps(){
        Guichet g1 = new Guichet("Guichet 1");
        assertEquals(-1,g1.getTemps(),"Erreur dans getTemps");
    }

    @Test
    void testGetEcartTemps(){
        Guichet g1 = new Guichet("Guichet 1");
        assertEquals(-1,g1.getEcartTemps(),"Erreur dans getEcartTemps");
    }
}
