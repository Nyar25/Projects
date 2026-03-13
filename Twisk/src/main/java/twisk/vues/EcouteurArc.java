package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import twisk.mondeIG.ArcIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class EcouteurArc implements EventHandler<MouseEvent> {
    private MondeIG monde;
    private ArcIG arc;


    public EcouteurArc(MondeIG monde, ArcIG arc) {
        if (arc == null) {
            Alertes.afficherErreur("Erreur : l'arc passé à EcouteurArc est null.");
            throw new IllegalArgumentException("Arc null");
        }
        if (monde == null) {
            Alertes.afficherErreur("Erreur : le monde passé à EcouteurArc est null.");
            throw new IllegalArgumentException("Monde null");
        }
        this.monde = monde;
        this.arc = arc;
    }

    public void handle(MouseEvent event) {
        monde.clicArc(arc);
        if(arc.isSelected()){
            monde.ajouterArcSelectionner(arc);
        } else {
            monde.enleverArcSelectionner(arc);
        }
    }
}
