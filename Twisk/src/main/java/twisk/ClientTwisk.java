package twisk;

import twisk.monde.*;
import twisk.outils.ClassLoaderPerso;
import twisk.outils.FabriqueNumero;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ClientTwisk {

    static Monde fabriqueMonde1() {

        Monde monde = new Monde();
        Activite e1 = new Activite("E1"); // 2
        ActiviteRestreinte ar2 = new ActiviteRestreinte("AR2"); // 3
        Activite s1 = new Activite("S1"); // 4
        ActiviteRestreinte ar1 = new ActiviteRestreinte("AR1"); // 5
        Guichet g1 = new Guichet("G1"); // 6
        Guichet g2 = new Guichet("G2"); // 7
        monde.aCommeEntree(e1);
        monde.ajouter(e1, g1, ar1, g2, ar2, s1);
        monde.aCommeSortie(s1);
        e1.ajouterSuccesseur(g1);
        g1.ajouterSuccesseur(ar1);
        ar1.ajouterSuccesseur(g2);
        g2.ajouterSuccesseur(ar2);
        ar2.ajouterSuccesseur(s1);
        return monde;
    }

    static Monde fabriqueMonde2() {
        Monde monde2 = new Monde();
        Activite e1 = new Activite("E1"); // 2
        ActiviteRestreinte ar2 = new ActiviteRestreinte("AR2"); // 7
        Activite s1 = new Activite("S1"); // 4
        ActiviteRestreinte ar1 = new ActiviteRestreinte("AR1"); // 4
        Guichet g1 = new Guichet("G1"); // 3
        Guichet g2 = new Guichet("G2"); // 5
        Guichet g3 = new Guichet("G3"); // 9
        ActiviteRestreinte ar3 = new ActiviteRestreinte("AR2"); // 8
        Activite e2 = new Activite("E2"); // 6

        monde2.aCommeEntree(e1);
        monde2.ajouter(e1, g1, ar1, g2, e2, ar2, g3, s1);
        monde2.aCommeSortie(s1);
        e1.ajouterSuccesseur(g1);
        g1.ajouterSuccesseur(ar1);
        ar1.ajouterSuccesseur(g2);
        g2.ajouterSuccesseur(e2);
        e2.ajouterSuccesseur(ar2);
        ar2.ajouterSuccesseur(g3);
        g3.ajouterSuccesseur(ar3);
        ar3.ajouterSuccesseur(s1);

        return monde2;
    }

    /**
     * Utilisation de l'introspection pour charger un nouveau classLoader et appeler la simulation de la fabriqueMonde1 et fabriqueMonde2
     * @param args
     */
    public static void main(String[] args) {
        Monde m1 = fabriqueMonde1();
        FabriqueNumero.getInstance().reset();
        Monde m2 = fabriqueMonde2();

        try {
            // Obtention de la classe ClassLoaderPerso
            ClassLoader tmpCLoader = new ClassLoaderPerso(ClientTwisk.class.getClassLoader());

            // Chargement de la classe Simulation
            Class<?> tmp = tmpCLoader.loadClass("twisk.simulation.Simulation");
            Object o = tmp.newInstance();
            Method meth1 = tmp.getMethod("setNbClients",int.class);
            Method meth2 = tmp.getMethod("simuler",Monde.class);
            // 5 clients
            meth1.invoke(o,5);
            // Appel fabriqueMonde1
            meth2.invoke(o,m1);
            // Appel fabriqueMonde2
            meth2.invoke(o,m2);

        } catch (ClassNotFoundException e) {
            System.err.println("La classe n'as pas été trouvée");
            e.printStackTrace();
        } catch (InstantiationException e) {
            System.err.println("Erreur lors de l'instanciation de la classe Simulation");
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            System.err.println("Erreur lors de la récupération de la méthode");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Erreur dans l'appel de la méthode");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Erreur dans l'instanciation ou dans l'appel d'une méthode");
            e.printStackTrace();;
        }
    }
}