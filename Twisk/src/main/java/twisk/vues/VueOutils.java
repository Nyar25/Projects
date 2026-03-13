package twisk.vues;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import twisk.mondeIG.MondeIG;
import twisk.mondeIG.SujetObserve;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

import java.util.LinkedList;

public class VueOutils extends TilePane implements Observateur {

    private LinkedList<Button> boutons;
    private SujetObserve sujet;

    /**
     * Constructeur de la vue d'outils
     * @param monde Monde qui sera lié à la vue
     */
    public VueOutils(MondeIG monde) {
        super();
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        sujet = monde;
        sujet.ajouterObservateur(this);
        boutons = new LinkedList<>();

        // Bouton pour ajouter une Activité
        Button boutonActivite = new Button();
        boutonActivite.setPrefWidth(TailleComposants.getInstance().getLargeurBouton());
        boutonActivite.setPrefHeight(TailleComposants.getInstance().getHauteurBouton());
        Image imageActivite = new Image(getClass().getResourceAsStream("/images/ajout.png"),50,50,true,true);
        boutonActivite.setGraphic(new ImageView(imageActivite));
        Tooltip tooltipAcitivite = new Tooltip("Ajoute une activité");
        tooltipAcitivite.setStyle("-fx-font-size: 16px;");
        boutonActivite.setTooltip(tooltipAcitivite);
        boutonActivite.setOnAction(e -> monde.ajouterEtape("Activite"));
        boutons.add(boutonActivite);
        this.getChildren().add(boutonActivite);

        // Bouton pour ajouter un guichet
        Button boutonGuichet = new Button();
        boutonGuichet.setPrefWidth(TailleComposants.getInstance().getLargeurBouton());
        boutonGuichet.setPrefHeight(TailleComposants.getInstance().getHauteurBouton());
        Image imageGuichet = new Image(getClass().getResourceAsStream("/images/ajout.png"),50,50,true,true);
        boutonGuichet.setGraphic(new ImageView(imageGuichet));
        Tooltip tooltipGuichet = new Tooltip("Ajoute un guichet");
        tooltipGuichet.setStyle("-fx-font-size: 16px;");
        boutonGuichet.setTooltip(tooltipGuichet);
        boutonGuichet.setOnAction(e -> monde.ajouterEtape("Guichet"));
        boutons.add(boutonGuichet);
        this.getChildren().add(boutonGuichet);

        // Bouton pour commencer la simulation
        Button simulation = new Button();
        simulation.setPrefWidth(TailleComposants.getInstance().getLargeurBouton());
        simulation.setPrefHeight(TailleComposants.getInstance().getHauteurBouton());
        Image play = new Image(getClass().getResourceAsStream("/images/play.png"),50,50,true,true);
        simulation.setGraphic(new ImageView(play));
        Tooltip tooltipPlay = new Tooltip("Appuyer pour lancer la simulation");
        tooltipPlay.setStyle("-fx-font-size: 16px;");
        simulation.setTooltip(tooltipPlay);
        EcouteurSimulation simu = new EcouteurSimulation(monde);
        simulation.setOnAction(simu);
        boutons.add(simulation);
        this.getChildren().add(simulation);
    }

    /**
     * Reagir vide
     */
    public void reagir(){

    }
}
