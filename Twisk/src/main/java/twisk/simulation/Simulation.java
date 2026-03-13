package twisk.simulation;

import twisk.monde.Monde;
import twisk.mondeIG.SujetObserve;
import twisk.outils.KitC;

import java.util.Iterator;

public class Simulation extends SujetObserve implements Iterable<Client> {

    private KitC kitC;
    private int nbClients;
    private int refresh;
    private GestionnaireClients gestionnaireClients;

    /**
     * Constructeur de Simulation
     */
    public Simulation(){
        this.kitC = new KitC(new Monde());
        this.nbClients = 6;
        this.refresh = 2;
        this.gestionnaireClients = new GestionnaireClients();
    }

    /**
     * Lance la simulation du monde en appelant les fonctions de kitC pour initialiser le monde
     * Affiche les étapes et simule le passage des clients dans les etapes
     * @param monde le monde
     */
    public void simuler(Monde monde){
        kitC.creerEnvironnement();
        kitC.creerFichier(monde.toC());
        kitC.compiler();
        kitC.construireLaBibliotheque();

        // Bonjour M Provilard, Si vous êtes ici c'est parce que l'application a crash à cause de libTwisk
        // On sait que y'a un bug mais JSP pourquoi + pas le temps
        System.load("/tmp/twisk/libTwisk.so");

        // Appel au define NBETAPES du client
        int nbEtapes = monde.nbEtapes();

        // Appel au define NBGUICHETS du client
        int nbGuichets = monde.nbGuichets();

        // Appel au Semaphore des guicehts
        int[] jetons_guichets = monde.getSemaphoresGuichets();

        // Appel de StartSimulatoin défini dans le co
        int[] pids = start_simulation(nbEtapes,nbGuichets,nbClients,jetons_guichets);

        //Affichage du graphe du monde
        System.out.println(monde);

        // Affichage des Pids
        System.out.print("les clients : ");
        for (int i = 0; i < nbClients; i++){
            System.out.print(pids[i]+" ");
        }
        System.out.println("\n");

        int[] tab2 = ou_sont_les_clients(nbEtapes,nbClients);

        gestionnaireClients.setClients(pids);

        // Tant que les clients n'ont pas atteint la dernière étape :
        while(tab2[nbClients+1] < nbClients){


            // Maj des positions client
            tab2 = ou_sont_les_clients(nbEtapes,nbClients);



            // Pour l'ensemble des étapes
            for (int i = 0 ; i < nbEtapes ; i++){

                // Permet de trouver la position d'une étape dans tab2
                int ind = i * (nbClients +1) ;

                for(int j = 0 ; j < tab2.length ; j++){

                    gestionnaireClients.allerA(j, monde.getEtape(i), ind);

                }

                // Affichage des étapes
                System.out.print("etape " + monde.getEtape(i).getNumero() + " (" + monde.getEtape(i).getNom() + ") " + tab2[ind] + " client");

                if(tab2[ind] > 1){
                    System.out.print("s : ");
                } else {
                    System.out.print(" : ");
                }
                // Affichage des ID à chaque étape
                if(tab2[ind] > 0){
                    // j = Premier client de l'étape.
                    // j <= ind + tab[ind] = Dernier client de l'étape
                    for (int j = ind + 1 ; j <= ind + tab2[ind] ; j++){
                        System.out.print(tab2[j]+" ");
                    }
                }
                System.out.print("\n");
            }
            System.out.print("\n");
            try{
                Thread.sleep(refresh*1000);
                this.notifierObservateurs();
            } catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        gestionnaireClients.nettoyer();
        nettoyage();
    }

    /**
     * Setter de nbClients
     * @param nbClients Nombre de clients
     */
    public void setNbClients(int nbClients) {
        this.nbClients = nbClients;
    }

    /**
     * Getter de NbClients
     */
    public int getNbClients() {
        return nbClients;
    }


    /**
     * 0 <= nbClients < 50
     * @param nbEtapes Nombres d'étapes
     * @param nbGuichets Nombres de Guichet
     * @param nbClients Nombre de Clients
     * @param tabJetonsGuichet Jetons
     * @return retourne le tableau des numéros des processus créés (pid)
     */
    public native int[] start_simulation (int nbEtapes, int nbGuichets, int nbClients, int[] tabJetonsGuichet);

    /**
     *
     * @param nbEtapes Nombre d'étapes
     * @param nbClients Nombre de clients
     * @return retourne l’adresse du tableau où sont stockées les informations des clients
     */
    public native int[] ou_sont_les_clients(int nbEtapes, int nbClients);

    /**
     * Fait un peu de ménage dans les structures de données créé (segment de mémoire partagée et sémaphores)
     */
    public native void nettoyage();

    /**
     *
     * @return Iterateur du gestionnaireClients
     */
    @Override
    public Iterator<Client> iterator() {
        return gestionnaireClients.iterator();
    }

    /**
     * Getter gu GestionnaireClients
     * @return Renvoie le GestionnaireClients
     */
    public GestionnaireClients getGest() {
        return gestionnaireClients;
    }
}
