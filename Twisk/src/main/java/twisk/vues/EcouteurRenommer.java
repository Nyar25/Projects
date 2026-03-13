package twisk.vues;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextInputDialog;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

import java.util.Optional;

public class EcouteurRenommer implements EventHandler<ActionEvent> {
    private MondeIG monde;

    /**
     * Constructeur de l'écouteur de l'item du menu Renommer
     * @param monde Monde qui sera lié à l'écouteur
     */
    public EcouteurRenommer(MondeIG monde) {
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.monde = monde;
    }

    /**
     * Réaction du clic de l'item Renommer
     * @param event Event qui correspond au clic de l'item Renommer
     */
    public void handle(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Renommer");
        dialog.setHeaderText("Renommer les activités sélectionnés");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent() && monde.getEtapesSelecteds().size()==1) {
            for(EtapeIG etape : monde.getEtapesSelecteds()){
                monde.setNomEtape(etape,result.get());
                monde.getEtapesSelecteds().clear();
            }
        }
    }
}
