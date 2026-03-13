package twisk.vues;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import twisk.exceptions.MondeException;
import twisk.mondeIG.MondeIG;
import twisk.mondeIG.SimulationIG;
import twisk.exceptions.Alertes;

public class EcouteurSimulation implements EventHandler<ActionEvent> {

    private MondeIG monde;

    /**
     * le Constructeur de EcouteurSimulation
     * @param monde Le MondeIG
     */
    public EcouteurSimulation(MondeIG monde) {
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }        this.monde = monde;
    }

    /**
     * Permet d'appeler la simuation pour simuler le temps pour les activtes ou le nb jetons pour les guichets
     * @param event
     */
    public void handle(ActionEvent event) {
        try{
            SimulationIG s = new SimulationIG(monde);
            s.simuler();
        } catch (MondeException e){
            System.out.println("Erreur " + e.getMessage());
        }
    }
}
