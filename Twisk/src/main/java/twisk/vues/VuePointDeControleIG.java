package twisk.vues;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import twisk.mondeIG.MondeIG;
import twisk.mondeIG.PointDeControleIG;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

public class VuePointDeControleIG extends Circle {

    /**
     * Constructeur de la vue d'un PointDeControle
     * @param point Point de contrôle
     * @param monde Monde qui sera lié à la vue
     */
    public VuePointDeControleIG(PointDeControleIG point, MondeIG monde) {
        super();
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        if (point == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.setCenterX(point.getCentreX());
        this.setCenterY(point.getCentreY());
        this.setRadius(TailleComposants.getInstance().getRayonPointDeControle());
        if(point.isSelected()){
            this.setFill(Color.RED);
        } else {
            this.setFill(Color.BLUE);
        }
        EcouteurPointDeControle ecouteur = new EcouteurPointDeControle(point,monde);
        this.setOnMouseClicked(ecouteur);
    }
}
