import org.junit.jupiter.api.Test;
import twisk.monde.*;

import static org.junit.jupiter.api.Assertions.*;

public class ActiviteRestreinteTest extends ActiviteTest{
    @Test
    public void toCtest(){
        SasEntree sasEntree = new SasEntree();
        SasSortie sasSortie = new SasSortie();
        Activite activite1 = new Activite("Activite1");
        Guichet guichet1 = new Guichet("Guichet1");
        ActiviteRestreinte activiterestreinte1 = new ActiviteRestreinte("Activiterestreinte1");

        sasEntree.ajouterSuccesseur(activite1);
        activite1.ajouterSuccesseur(guichet1);
        guichet1.ajouterSuccesseur(activiterestreinte1);
        activiterestreinte1.ajouterSuccesseur(sasSortie);

        assertEquals("switch(nb){\n" +
                "case 0:\n" +
                "transfert(Activiterestreinte1,SASSORTIE);\n" +
                "break;\n" +
                "}\n",activiterestreinte1.toC(),"La fonction toC marche pas comme voulu");
    }
}