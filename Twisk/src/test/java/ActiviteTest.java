import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import twisk.monde.Activite;
import twisk.monde.ActiviteRestreinte;
import twisk.monde.SasEntree;
import twisk.monde.SasSortie;
import twisk.outils.FabriqueNumero;

import static org.junit.jupiter.api.Assertions.*;

public class ActiviteTest extends EtapeTest {
    @BeforeEach
    public void setUp() {
        FabriqueNumero.getInstance().reset();
    }

    @Test
    void testConstructeur(){
        Activite activite = new Activite("fdsfdsgufds");
        assertNotNull(activite);
        assertEquals("fdsfdsgufds", activite.getNom(),"Nom mal instancier");
        assertEquals(4,activite.getTemps(),"Mauvais temps instancier");
        assertEquals(1,activite.getEcartTemps(),"Mauvais ecartTemps");
    }

    @Test
    void testConstructeur2(){
        assertThrows(IllegalArgumentException.class, () -> {Activite ac = new Activite("ActivitéThrows",0,8);});
        assertThrows(IllegalArgumentException.class, () -> {Activite ac = new Activite("ActivitéThrows3",2,-1);});
        assertThrows(IllegalArgumentException.class, () -> {Activite ac = new Activite("ActivitéThrows3",2,8);});
        assertThrows(IllegalArgumentException.class, () -> {ActiviteRestreinte ar = new ActiviteRestreinte("activiteRestreinte", 0, 1);},"Erreur valeurs temps");
        assertThrows(IllegalArgumentException.class, () -> {ActiviteRestreinte ar = new ActiviteRestreinte("activiteRestreinte", 2, -1);},"Erreur valeurs temps");
        assertThrows(IllegalArgumentException.class, () -> {ActiviteRestreinte ar = new ActiviteRestreinte("activiteRestreinte", 2, 4);},"Erreur valeurs temps");
        Activite ac = new Activite("Activité",5,3);
        assertNotNull(ac,"ac est null alors qu'il ne devrait pas");
        assertEquals("Activite",ac.getNom(),"Nom pas instancier");
        assertEquals(5,ac.getTemps(),"Temps pas instancier");
        assertEquals(3,ac.getEcartTemps(),"Ecart de temps pas instancier");
    }

    @Test
    void testToString(){
        Activite ac = new Activite("Activité 1");
        ac.ajouterSuccesseur(new Activite("Activité 2"));
        assertEquals("Activite 1 : 1 successeur - Activite 2",ac.toString(),"Fonction toString mal faite");
    }

    @Test
    public void testToCActivite(){
        SasEntree sas = new SasEntree();
        SasSortie s = new SasSortie();
        Activite activite1 = new Activite("Activité1",3,2);
        Activite activite2 = new Activite("Activité2",5,1);

        sas.ajouterSuccesseur(activite1);
        activite1.ajouterSuccesseur(activite2);
        activite2.ajouterSuccesseur(s);

        String res = "delai(3,2);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activite1,Activite2);\n" +
                "break;\n" +
                "}\n" +
                "delai(5,1);\n" +
                "switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activite2,SASSORTIE);\n" +
                "break;\n" +
                "}\n";
        assertEquals(res,activite1.toC(),"Fonction toC marche pas comme voulu");
    }

    @Test
    void testGetNumeroSuccesseur(){
        Activite a1 = new Activite("A1");
        Activite a2 = new Activite("A2");
        Activite a3 = new Activite("A3");
        Activite a4 = new Activite("A4");

        a1.ajouterSuccesseur(a2);
        a2.ajouterSuccesseur(a3);
        a3.ajouterSuccesseur(a4);

        assertEquals(1,a1.getNumeroSuccesseur(),"Pas le bon numéro");
        assertEquals(2,a2.getNumeroSuccesseur(),"Pas le bon numéro");
        assertEquals(3,a3.getNumeroSuccesseur(),"Pas le bon numéro");
    }
}
