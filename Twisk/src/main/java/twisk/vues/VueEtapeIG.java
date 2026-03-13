package twisk.vues;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

public abstract class VueEtapeIG extends VBox implements Observateur{
    private Label titre;
    private EtapeIG etape;

    /**
     * Constructeur de la vue d'une étape
     * @param monde Monde qui sera lié à la vue
     * @param etape Etape dont on va modéliser la vue
     */
    public VueEtapeIG(MondeIG monde, EtapeIG etape) {
        super();

        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        if (etape == null) {
            Alertes.afficherErreur("Erreur : Le etape est null.");
            throw new IllegalArgumentException("etape null");
        }

        this.etape = etape;
        this.titre = new Label(etape.getNom());
        titre.setPrefWidth(TailleComposants.getInstance().getLargeurEtape());
        titre.setAlignment(Pos.CENTER);
        this.titre.setStyle("-fx-background-color: red");
        if(etape.isSelected()){
            this.setStyle("-fx-border-color: red; -fx-border-width: 3px;");
        } else {
            this.setStyle("-fx-border-color: gray; -fx-border-width: 3px;");
        }

        if (monde.estEntree(etape) && monde.estSortie(etape)) {
            Image im = new Image(getClass().getResourceAsStream("/images/ES.png"), 15, 15, true, true);
            ImageView imageView = new ImageView(im);
            titre.setGraphic(imageView);

        } else if(monde.estEntree(etape)) {
            Image im = new Image(getClass().getResourceAsStream("/images/E.png"), 15, 15, true, true);
            ImageView imageView = new ImageView(im);
            titre.setGraphic(imageView);
        } else if(monde.estSortie(etape)) {
            Image im = new Image(getClass().getResourceAsStream("/images/S.png"), 15, 15, true, true);
            ImageView imageView = new ImageView(im);
            titre.setGraphic(imageView);
        } else {
            titre.setGraphic(null);
        }

        getChildren().add(titre);
        EcouteurEtape ecouteur = new EcouteurEtape(monde,etape);
        EcouteurSource ecouteurSource = new EcouteurSource(this);
        this.setOnMouseClicked(ecouteur);
        this.setOnDragDetected(ecouteurSource);
    }

    /**
     *
     * @return ID de l'étape
     */
    public String getEtapeID() {
        return etape.getIdentifiant();
    }

    /**
     *  Reagir vide
     */
    @Override
    public void reagir() {

    }
}
