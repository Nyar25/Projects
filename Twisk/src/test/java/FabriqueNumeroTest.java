import org.junit.jupiter.api.BeforeEach;
import twisk.monde.Activite;
import twisk.monde.ActiviteRestreinte;
import twisk.monde.Guichet;
import twisk.outils.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FabriqueNumeroTest {
    @BeforeEach
    void setUp(){
        FabriqueNumero.getInstance().reset();
    }

    @Test
    void testGetInstance() {
        FabriqueNumero instance1 = FabriqueNumero.getInstance();
        FabriqueNumero instance2 = FabriqueNumero.getInstance();

        assertEquals(instance1, instance2,"Les deux instances doivent être les mêmes");
    }

    @Test
    void testGetNumeroEtape() {
        assertEquals(0,FabriqueNumero.getInstance().getNumeroEtape(),"Le numéro est censé être 0");
        assertEquals(1,FabriqueNumero.getInstance().getNumeroEtape(),"Le numéro est censé être 1");
        assertEquals(2,FabriqueNumero.getInstance().getNumeroEtape(),"Le numéro est censé être 2");
    }

    @Test
    void testGetNumeroSemaphore() {
        assertEquals(1,FabriqueNumero.getInstance().getNumeroSemaphore(),"Le numéro est censé être 1");
        assertEquals(2,FabriqueNumero.getInstance().getNumeroSemaphore(),"Le numéro est censé être 2");
        assertEquals(3,FabriqueNumero.getInstance().getNumeroSemaphore(),"Le numéro est censé être 3");
    }

    @Test
    void testReset() {
        Activite a1 = new Activite("A1");
        Activite a2 = new Activite("A2");
        Guichet g1 = new Guichet("G1");
        ActiviteRestreinte ar1 = new ActiviteRestreinte("AR1");
        Guichet g2 = new Guichet("G2");

        assertEquals(5,FabriqueNumero.getInstance().getNumeroEtape(),"Problème dans le reset");
        assertEquals(3,FabriqueNumero.getInstance().getNumeroSemaphore(),"Problème dans le reset");

        FabriqueNumero.getInstance().reset();

        assertEquals(0,FabriqueNumero.getInstance().getNumeroEtape(),"Problème dans le reset");
        assertEquals(1,FabriqueNumero.getInstance().getNumeroSemaphore(),"Problème dans le reset");
    }
}
