package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class EcouteurEtape implements EventHandler<MouseEvent> {
    private MondeIG monde;
    private EtapeIG etape;

    /**
     * Constructeur de l'écouteur d'étape
     * @param monde Monde qui sera lié à l'écouteur
     * @param etape Etape qui sera lié à l'écouteur
     */
    public EcouteurEtape(MondeIG monde, EtapeIG etape) {

        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        if (etape == null) {
            Alertes.afficherErreur("Erreur : Le etape est null.");
            throw new IllegalArgumentException("etape null");
        }
        this.monde = monde;
        this.etape = etape;
    }

    /**
     * Réaction du clic de l'étape
     * @param event Event qui correspond au clic du point de contrôle
     */
    @Override
    public void handle(MouseEvent event) {
        monde.clicEtape(etape);
        if(etape.isSelected()){
            monde.ajouterEtapeSelectionner(etape);
        } else {
            monde.enleverEtapeSelectionner(etape);
        }
     }
}
