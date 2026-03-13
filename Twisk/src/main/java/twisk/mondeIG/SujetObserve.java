package twisk.mondeIG;

import twisk.vues.Observateur;

import java.util.LinkedList;

public class SujetObserve {
    private LinkedList<Observateur> obs;

    /**
     * Constructeur du SujetObserve
     */
    public SujetObserve() {
        obs = new LinkedList<>();
    }

    /**
     * Fonction qui ajoute un observateur dans la liste
     * @param v Observateur à ajouter
     */
    public void ajouterObservateur(Observateur v) {
        obs.add(v);
    }

    /**
     * Fonction qui met à jour les composantes graphiques
     */
    protected void notifierObservateurs() {
        for (Observateur o : obs) o.reagir();
    }
}