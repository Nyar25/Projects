import org.junit.jupiter.api.Test;
import twisk.monde.*;
import twisk.outils.FabriqueNumero;

import java.util.Iterator;
import static org.junit.jupiter.api.Assertions.*;

public class EtapeTest {
    @Test
    void testConstructeur(){
        assertThrows(IllegalArgumentException.class,() -> {new Activite(null);});
        Activite activite = new Activite("Activité");
        assertEquals("Activite",activite.getNom(),"Nom invalide");
        assertThrows(IllegalArgumentException.class,() -> {new Guichet(null);});
        Guichet guichet = new Guichet("Guichet");
        assertEquals("Guichet",guichet.getNom(),"Nom invalide");
        assertThrows(IllegalArgumentException.class,() -> {new ActiviteRestreinte(null);});
        ActiviteRestreinte activiteRestreinte = new ActiviteRestreinte("Restreinte");
        assertEquals("Restreinte",activiteRestreinte.getNom(),"Nom invalide");

    }

    @Test
    void testAjouterSuccesseur(){
        Activite ac = new Activite("Activité");
        Guichet g = new Guichet("Guichet");
        ActiviteRestreinte ar = new ActiviteRestreinte("Restreinte");
        assertThrows(IllegalArgumentException.class, () -> {ac.ajouterSuccesseur();});
        assertThrows(IllegalArgumentException.class, () -> {ac.ajouterSuccesseur(null);});
        assertThrows(IllegalArgumentException.class, () -> {ac.ajouterSuccesseur(null,null);});
        assertThrows(IllegalArgumentException.class, () -> {g.ajouterSuccesseur();});
        assertThrows(IllegalArgumentException.class, () -> {g.ajouterSuccesseur(null);});
        assertThrows(IllegalArgumentException.class, () -> {g.ajouterSuccesseur(null,null);});
        assertThrows(IllegalArgumentException.class, () -> {ar.ajouterSuccesseur();});
        assertThrows(IllegalArgumentException.class, () -> {ar.ajouterSuccesseur(null);});
        assertThrows(IllegalArgumentException.class, () -> {ar.ajouterSuccesseur(null,null);});
        assertEquals(0,ac.nbSuccesseurs(),"Echec dans l'ajout des successeurs");
        ac.ajouterSuccesseur(g,ar);
        assertEquals(2,ac.nbSuccesseurs(),"Echec dans l'ajout des successeurs");
    }

    @Test
    void testEstUneActivite(){
        Activite ac = new Activite("Activité");
        Guichet g = new Guichet("Guichet");
        ActiviteRestreinte ar = new ActiviteRestreinte("Restreinte");
        assertTrue(ac.estUneActivite(),"ac devrait être une activité");
        assertTrue(ar.estUneActivite(),"ar devrait être une activité");
        assertFalse(g.estUneActivite(),"g ne devrait pas être une activité");
    }

    @Test
    void testEstUneGuichet(){
        Activite ac = new Activite("Activité");
        Guichet g = new Guichet("Guichet");
        ActiviteRestreinte ar = new ActiviteRestreinte("Restreinte");
        assertTrue(g.estUnGuichet(),"g devrait être un guichet");
        assertFalse(ac.estUnGuichet(),"ac ne devrait pas être un guichet");
        assertFalse(ar.estUnGuichet(),"ar ne devrait pas être un guichet");
    }

    @Test
    void testIterator(){
        int i = 0;
        Activite ac = new Activite("Activité");
        ac.ajouterSuccesseur(new Guichet("Guichet"),new ActiviteRestreinte("Restreinte"));
        Iterator<Etape> it = ac.iterator();
        while(it.hasNext()){
            it.next();
            i++;
        }
        assertEquals(i,ac.nbSuccesseurs(),"Echec dans l'iterator");
    }

    @Test
    void testToString(){
        Etape activite = new Activite("activité");
        Etape guichet = new Guichet("guichet");
        Etape restreinte = new ActiviteRestreinte("actRestreinte");
        Etape sasEntree = new SasEntree();
        Etape sasSortie = new SasSortie();
        assertEquals("activite",activite.getNom(),"Instanciation du nom mal faite");
        assertEquals("guichet",guichet.getNom(),"Instanciation du nom mal faite");
        assertEquals("actRestreinte",restreinte.getNom(),"Instanciation du nom mal faite");
        assertEquals("SASENTREE",sasEntree.getNom(),"Instanciation du nom mal faite");
        assertEquals("SASSORTIE",sasSortie.getNom(),"Instanciation du nom mal faite");
    }

    @Test
    void testNumeroEtape(){
        FabriqueNumero.getInstance().reset();
        Etape activite = new Activite("activité");
        Etape guichet = new Guichet("guichet");
        Etape restreinte = new ActiviteRestreinte("actRestreinte");
        Etape sasEntree = new SasEntree();
        Etape sasSortie = new SasSortie();

        assertEquals(0,activite.getNumero(),"Le numero doit être 0");
        assertEquals(1,guichet.getNumero(),"Le numero doit être 1");
        assertEquals(2,restreinte.getNumero(),"Le numero doit être 2");
        assertEquals(3,sasEntree.getNumero(),"Le numero doit être 3");
        assertEquals(4,sasSortie.getNumero(),"Le numero doit être 4");
    }

    @Test
    void testGetNextEtape(){
        Etape e1 = new Activite("A1");
        Etape e2 = new Activite("A2");
        Etape e3 = new Activite("A3");
        Etape e4 = new Activite("A4");
        e1.ajouterSuccesseur(e2);
        e1.ajouterSuccesseur(e4);
        e2.ajouterSuccesseur(e3);
        assertThrows(IllegalArgumentException.class,() -> e1.getNextEtape(-1));
        assertThrows(IllegalArgumentException.class,() -> e2.getNextEtape(-1));
        assertEquals(e2,e1.getNextEtape(0),"Fonction getNextEtape marche pas");
        assertEquals(e3,e2.getNextEtape(0),"Fonction getNextEtape marche pas");
        assertEquals(e4,e1.getNextEtape(1),"Fonction getNextEtape marche pas");

    }

    @Test
    void testGetNomSuccesseur(){
        Activite a1 = new Activite("A1");
        Guichet g2 = new Guichet("G2");
        Guichet g3 = new Guichet("G3");
        Activite a4 = new Activite("A4");

        a1.ajouterSuccesseur(g2);
        g2.ajouterSuccesseur(g3);
        g3.ajouterSuccesseur(a4);

        assertThrows(IllegalArgumentException.class, () -> a1.getNomSuccesseur(0));
        assertEquals("G2",a1.getNomSuccesseur(1),"Pas le bon nom");
        assertEquals("G3",g2.getNomSuccesseur(1),"Pas le bon nom");
        assertEquals("A4",g3.getNomSuccesseur(1),"Pas le bon nom");
    }
}
