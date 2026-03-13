package twisk.exceptions;

import javafx.scene.control.Alert;

public class Alertes {

    public static void afficherErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText("Une erreur est survenue");
        alert.setContentText(message);
        alert.showAndWait();
    }

}
