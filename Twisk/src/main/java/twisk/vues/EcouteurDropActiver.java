package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import twisk.mondeIG.EtapeIG;
import twisk.exceptions.Alertes;

public class EcouteurDropActiver implements EventHandler<DragEvent> {

    public VueMondeIG monde;

    /**
     * Constructeur de EcouteurDropActivier
     * @param monde La VueMondeIG
     */
    public EcouteurDropActiver(VueMondeIG monde){
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.monde = monde;
    }

    /**
     * Permet de mettre à jour la position des EtapeIG
     * @param event Le Drag&Drop
     */
    public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        for(EtapeIG e : monde.getSujet()){
            if(e.getIdentifiant().equals(db.getString())){
                e.setPosX((int)event.getX()-e.getLargeur()/2);
                e.setPosY((int)event.getY()-e.getHauteur()/2);
                e.updatePointsDeControle();
                monde.reagir();
            }
        }
    }
}
