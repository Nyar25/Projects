import org.junit.jupiter.api.Test;
import twisk.monde.*;
import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;

public class GestionnaireEtapesTest {
    @Test
    void testConstructeur(){
        GestionnaireEtapes gest = new GestionnaireEtapes();
        assertNotNull(gest);
        assertEquals(0,gest.nbEtapes(),"Echec dans l'instanciation de gest");
    }

    @Test
    void testAjouter(){
        GestionnaireEtapes gest = new GestionnaireEtapes();
        assertEquals(0,gest.nbEtapes(),"Echec de l'instanciation de gest");
        assertThrows(IllegalArgumentException.class,() -> {gest.ajouter();});
        assertThrows(IllegalArgumentException.class, () -> {gest.ajouter(null);});
        assertThrows(IllegalArgumentException.class, () -> {gest.ajouter(null,null);});
        gest.ajouter(new Activite("Activité"),new Guichet("Guichet"),new ActiviteRestreinte("Restreinte"));
        assertEquals(3,gest.nbEtapes(),"Echec de l'ajouter des étapes");
    }

    @Test
    void testIterator(){
        int i = 0;
        GestionnaireEtapes gest = new GestionnaireEtapes();
        assertEquals(i,gest.nbEtapes(),"Echec dans l'iterator");
        gest.ajouter(new Activite("Act"),new Guichet("G"),new ActiviteRestreinte("ActRestreinte"));
        Iterator<Etape> it = gest.iterator();
        while(it.hasNext()){
            it.next();
            i++;
        }
        assertEquals(i,gest.nbEtapes(),"Echec dans l'iterator");
    }

    @Test
    void testGetEtape(){
        GestionnaireEtapes gest = new GestionnaireEtapes();
        assertThrows(IndexOutOfBoundsException.class,() -> gest.getEtape(-1));
        Activite a1 = new Activite("A1");
        Guichet g1 = new Guichet("G1");
        ActiviteRestreinte ar1 = new ActiviteRestreinte("A1Restreinte");
        gest.ajouter(a1,g1,ar1);
        assertEquals(a1,gest.getEtape(0),"Echec dans getEtape");
        assertEquals(g1,gest.getEtape(1),"Echec dans getEtape");
        assertEquals(ar1,gest.getEtape(2),"Echec dans getEtape");
    }

    @Test
    void testToString(){
        GestionnaireEtapes gest = new GestionnaireEtapes();
        Activite a1 = new Activite("a1");
        Guichet g1 = new Guichet("g1");
        ActiviteRestreinte ar1 = new ActiviteRestreinte("ar1");
        gest.ajouter(a1,g1,ar1);
        String res = "a1 : 0 successeur - \ng1 : 0 successeur - \nar1 : 0 successeur - \n";
        assertEquals(res,gest.toString(),"Echec dans le toString");
    }
}
