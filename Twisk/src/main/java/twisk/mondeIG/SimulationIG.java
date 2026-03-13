package twisk.mondeIG;

import twisk.exceptions.MondeException;
import twisk.monde.*;
import twisk.outils.ClassLoaderPerso;
import twisk.outils.FabriqueNumero;
import twisk.outils.TailleComposants;
import twisk.simulation.Client;
import twisk.simulation.GestionnaireClients;
import twisk.vues.Observateur;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SimulationIG implements Observateur {

    private MondeIG monde;
    private CorrespondancesEtapes correspondances;
    private GestionnaireClients gest;
    private Class<?> c;
    private Object o;

    /**
     * Constructeur de Simulation IG
     * J'ajoute par introspection à la liste des Observateurs
     * @param monde le monde
     */
    public SimulationIG(MondeIG monde) {
        this.monde = monde;
        // Obtention de la classe ClassLoaderPerso
        ClassLoaderPerso tmpCLoader = new ClassLoaderPerso(SimulationIG.class.getClassLoader());
        try{
            // Chargement de la classe Simulation
            c = tmpCLoader.loadClass("twisk.simulation.Simulation");
            o = c.newInstance();
            Method mts = c.getMethod("ajouterObservateur", Observateur.class);
            mts.invoke(o,this);
            Method m2 = c.getMethod("getGest");
            this.gest = (GestionnaireClients) m2.invoke(o);
        } catch(ClassNotFoundException e){
            System.err.println("La classe n'as pas été trouvée");
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException e) {
            System.err.println("Erreur lors de l'instanciation de la classe Simulation");
            e.printStackTrace();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Appel par Introspection simuler de Monde
     * @throws MondeException les exceptions
     */
    public void simuler() throws MondeException{
        this.verifierMondeIG();
        Monde m = creerMonde();

        try {
            Method meth = c.getMethod("simuler",Monde.class);
            // Appel fabriqueMonde1
            meth.invoke(o,m);

        } catch (NoSuchMethodException e) {
            System.err.println("Erreur lors de la récupération de la méthode");
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            System.err.println("Erreur dans l'appel de la méthode");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            System.err.println("Erreur dans l'instanciation ou dans l'appel d'une méthode");
            e.printStackTrace();
        }
    }

    /**
     * Verifie que les minimum des conditions pour lancer une simulation est remplit, comme une entrée - sortie défini
     * ou les contraines sur les AR et les guichets
     * @throws MondeException
     */
    private void verifierMondeIG() throws MondeException {

        monde.estVideEntree();
        monde.estVideSortie();
        monde.entreeSansPredecesseurs();
        monde.aSuccesseurs();
        monde.aPredecesseurs();
        monde.guichetsCorrect();
        monde.definirActivitesRestreinte();

    }

    /**
     * Fonction qui créer un Monde à partir du MondeIG
     * @return un Monde
     */
    private Monde creerMonde() {

        FabriqueNumero.getInstance().reset();
        Monde m = new Monde();
        this.correspondances = new CorrespondancesEtapes();

        // Création de toutes les Etapes dans le Monde
        for(EtapeIG etapeig : monde){
            if(etapeig.estUneActivite()){
                ActiviteIG activiteIG = (ActiviteIG) etapeig;
                Activite activite = new Activite(activiteIG.getNom(), activiteIG.getDelai(), activiteIG.getEcart());
                m.ajouter(activite);
                correspondances.ajouter(etapeig,activite);
            } else if(etapeig.estUnGuichet()){
                GuichetIG guichetIG = (GuichetIG) etapeig;
                Guichet guichet = new Guichet(guichetIG.getNom(),guichetIG.getNbJetons());
                m.ajouter(guichet);
                correspondances.ajouter(etapeig,guichet);
            } else if(etapeig.estUneActiviteRestreinte()){
                ActiviteRestreinte activiterestreinte = new ActiviteRestreinte(etapeig.getNom());
                m.ajouter(activiterestreinte);
                correspondances.ajouter(etapeig,activiterestreinte);
            }

            // On vérifie si l'étape est une entrée ou une sortie dans MondeIG pour les ajouter dans Monde
            if(monde.estEntree(etapeig)){
                m.aCommeEntree(correspondances.get(etapeig));
            }
            if(monde.estSortie(etapeig)){
                m.aCommeSortie(correspondances.get(etapeig));
            }

        }

        // Ajout des successeurs de chaque Etape par rapport à EtapeIG
        for(EtapeIG etapeig : monde){
            Etape depart = correspondances.get(etapeig);
            for(EtapeIG succ : etapeig.getSuccesseurs()){
                Etape arrive = correspondances.get(succ);
                depart.ajouterSuccesseur(arrive);
            }
        }
        return m;

    }

    /**
     * Fonction qui doit permet de visualiser les clients sous la forme de petit cercle
     */
    public void reagir(){
        monde.viderClients();
        int ecart = 0;
        int rayon = TailleComposants.getInstance().getRayonClient();
        int hauteurEtape = TailleComposants.getInstance().getHauteurEtape();
        // Pour chaque EtapeIG du monde et chaque client qui existe
        for(EtapeIG etapeIG : monde){
            for(Client client : gest){
                // On vérifie si l'étape du client correspond à son équivalent
                if(correspondances.get(etapeIG).equals(client.getEtape())){
                    ecart += 3*rayon*monde.getNbClientsIG();
                    ClientIG clientIG = new ClientIG(etapeIG.getPosX()+rayon+ecart,etapeIG.getPosY()+hauteurEtape/2,1,etapeIG);
                    monde.ajouterClient(clientIG);
                }
            }
        }
        monde.notifierObservateurs();
    }
}
