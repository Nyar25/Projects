package twisk.vues;

import javafx.scene.layout.HBox;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

public class VueActiviteIG extends VueEtapeIG{

    private HBox clients;

    /**
     * Constructeur de la vue d'une activité
     * @param monde Monde qui sera lié à la vue
     * @param etape Activité dont on va modéliser la vue
     */
    public VueActiviteIG(MondeIG monde, EtapeIG etape) {
        super(monde,etape);

        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        if (etape == null) {
            Alertes.afficherErreur("Erreur : Le etape est null.");
            throw new IllegalArgumentException("etape null");
        }

        int spacing = TailleComposants.getInstance().getPadding();
        clients = new HBox();
        clients.setStyle("-fx-background-color: purple; -fx-border-width: 3px; -fx-background-insets: 0 0 -1 0, 0, 1, 2; -fx-background-radius: 3px, 3px, 2px, 1px;");
        this.setSpacing(spacing);
        clients.setPrefHeight(etape.getHauteur());
        clients.setPrefWidth(etape.getLargeur());
        this.focusTraversableProperty().set(true);
        getChildren().add(clients);
    }
}