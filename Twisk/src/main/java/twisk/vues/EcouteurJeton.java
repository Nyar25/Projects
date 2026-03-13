package twisk.vues;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import twisk.exceptions.TwiskException;
import twisk.mondeIG.GuichetIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class EcouteurJeton implements EventHandler<ActionEvent> {

    public MondeIG monde;

    /**
     *  Le constructeur de l'Ecouteur Jeton
     * @param monde Le mondeIG
     */
    public EcouteurJeton(MondeIG monde) {
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }

        this.monde = monde;
    }

    /**
     * Change la capacite en Jeton des Guichets
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) {

        try {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Mettre à jour le nombre de Jeton");
            dialog.setHeaderText("Exemple : 4");
            dialog.showAndWait();
            String traitement = dialog.getEditor().getText();

            // Verifier qu'il n'y a des entiers.
            // Utiliser matches et pas contains
            // Source : https://stackoverflow.com/questions/7036324/what-is-the-regex-for-any-positive-integer-excluding-0
            if ( !(traitement.matches("^[1-9]\\d*$"))){

                dialog.close();

            } else {

                int capacite = Integer.parseInt(traitement.trim());
                if (capacite <= 0){
                    throw new TwiskException("Capacite invalide");
                } else {
                    GuichetIG guichetIG = (GuichetIG) monde.getEtapesSelecteds().getFirst();
                    guichetIG.setNbJetons(capacite);
                    guichetIG.setNom( guichetIG.getNom()+" "+ capacite + " jetons " );
                }
            }

        } catch(TwiskException er){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Valeurs invalides");
            alert.setContentText(er.getMessage());
            alert.showAndWait();
        }
    }
}
