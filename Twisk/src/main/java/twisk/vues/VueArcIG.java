package twisk.vues;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import twisk.mondeIG.ArcIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class VueArcIG extends Pane {
    private ArcIG arc;

    /**
     * Constructeur de la vue
     * @param arc Arc dont on va modéliser la vue
     */
    public VueArcIG(MondeIG monde, ArcIG arc) {
        super();

        if (arc == null) {
            Alertes.afficherErreur("Erreur : Le arc est null.");
            throw new IllegalArgumentException("arc null");
        }

        this.arc = arc;
        Line line = new Line();
        Polyline pointe = new Polyline();
        line.setStartX(arc.getDepartX());
        line.setStartY(arc.getDepartY());
        line.setEndX(arc.getArriveX());
        line.setEndY(arc.getArriveY());
        line.setStrokeWidth(3);
        // Pointe de fleche
        double longueurCote = 15.;
        double angle = 30.;

        if (arc.getArriveX() > arc.getDepartX()) {
            angle += 180;
        }

        // Calcul des coordonnées
        // Vecteur x et y
        double vecteurX = arc.getArriveX()-arc.getDepartX();
        double vecteurY = arc.getArriveY()-arc.getDepartY();
        // Valeur de arc tangante
        double arcTan = Math.atan(vecteurY/vecteurX);
        // Calcul des angles pour les 2 autres points
        double newAnglePointB = arcTan+Math.toRadians(angle);
        double newAnglePointC = arcTan-Math.toRadians(angle);
        // Calcul des coordonnées des 2 points
        double abscissePointB = arc.getArriveX() + longueurCote*Math.cos(newAnglePointB);
        double ordonneePointB = arc.getArriveY() + longueurCote*Math.sin(newAnglePointB);
        double abscissePointC = arc.getArriveX() + longueurCote*Math.cos(newAnglePointC);
        double ordonneePointC = arc.getArriveY() + longueurCote*Math.sin(newAnglePointC);

        pointe.getPoints().addAll(arc.getArriveX(),arc.getArriveY(),abscissePointB,ordonneePointB,abscissePointC,ordonneePointC,arc.getArriveX(),arc.getArriveY());
        pointe.setStrokeWidth(2);
        if(arc.isSelected()){
            line.setStyle("-fx-stroke: red");
            pointe.setStyle("-fx-stroke: red");
            pointe.setFill(Color.RED);
        } else {
            line.setStyle("-fx-stroke: orange");
            pointe.setStyle("-fx-stroke: green");
            pointe.setFill(Color.GREEN);
        }
        EcouteurArc ecouteurArc = new EcouteurArc(monde,arc);
        this.setOnMouseClicked(ecouteurArc);
        this.setPickOnBounds(false);
        getChildren().addAll(line,pointe);
    }
}
