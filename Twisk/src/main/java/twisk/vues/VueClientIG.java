package twisk.vues;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import twisk.mondeIG.ClientIG;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

public class VueClientIG extends Circle{

    /**
     * Constructeur de VueClientIG
     * @param client le clientIG qu'on veut représenter sous la forme d'un cercle
     */
    public VueClientIG(ClientIG client) {
        super();

        if (client == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.setCenterX(client.getCentreX());
        this.setCenterY(client.getCentreY());
        this.setRadius(TailleComposants.getInstance().getRayonClient());
        this.setFill(Color.BLUE);

    }
}
