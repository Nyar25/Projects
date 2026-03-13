package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent;
import twisk.exceptions.TwiskException;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.mondeIG.PointDeControleIG;
import twisk.exceptions.Alertes;

public class EcouteurPointDeControle implements EventHandler<MouseEvent> {
    private MondeIG monde;
    private PointDeControleIG point;

    /**
     * Constructeur de l'écouteur du point de contrôle
     * @param point Point qui va réagir au clic
     * @param monde Monde qui sera lié à l'écouteur
     */
    public EcouteurPointDeControle(PointDeControleIG point, MondeIG monde) {
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        if (point == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }

        this.monde = monde;
        this.point = point;
    }

    /**
     * Réaction du clic du point de contrôle
     * @param event Event qui correspond au clic du point de contrôle
     */
    @Override
    public void handle(MouseEvent event) {
        try{
            monde.clicPointDeControle(point);
            if(monde.peutAjouterArc()){
                for(EtapeIG e : monde){
                    for(PointDeControleIG p : e){
                        if(p.isSelected() && p!=point){
                            monde.ajouter(p,point);
                            monde.clicPointDeControle(point);
                            monde.clicPointDeControle(p);
                        }
                    }
                }
            }
        } catch(TwiskException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Problème avec l'arc");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            for(EtapeIG etape : monde){
                for(PointDeControleIG p : etape){
                    if(p.isSelected()){
                        monde.clicPointDeControle(point);
                        monde.clicPointDeControle(p);
                    }
                }
            }
        }
    }
}
