package twisk.vues;

import javafx.event.EventHandler;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import twisk.exceptions.Alertes;

public class EcouteurSource implements EventHandler<MouseEvent> {
    private VueEtapeIG etape;

    /**
     * Constructeur de l'écouteur
     * @param etape Vue
     */
    public EcouteurSource(VueEtapeIG etape) {
        if (etape == null) {
            Alertes.afficherErreur("Erreur : Le etape est null.");
            throw new IllegalArgumentException("etape null");
        }
        this.etape = etape;
    }

    /**
     * Réaction de la sélection de l'étape
     * @param event Event qui correspond à la sélection
     */
    public void handle(MouseEvent event) {
        Dragboard dragboard = etape.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent();
        WritableImage writer = etape.snapshot(null,null);
        content.putString(etape.getEtapeID());
        content.putImage(writer);
        dragboard.setContent(content);
        event.consume();
    }
}
