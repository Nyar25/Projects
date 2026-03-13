package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import twisk.exceptions.Alertes;

public class EcouteurDropAccepter implements EventHandler<DragEvent> {

    private VueMondeIG monde;

    /**
     * Constructeur du Drop
     * @param monde La VueMondeIG
     */
    public EcouteurDropAccepter(VueMondeIG monde){

        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.monde = monde;
    }

    /**
     * Permet Le Drag&Drop
     * @param event Le Clic de souris
     */
    public void handle(DragEvent event) {
        Dragboard db = event.getDragboard();
        if(event.getGestureSource() != monde && db.hasString() && db.hasImage()){
            event.acceptTransferModes(TransferMode.MOVE);
        }
    }
}
