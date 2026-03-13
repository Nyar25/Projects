package twisk.exceptions;

import javafx.scene.control.Alert;

public class TwiskException extends Exception {
    /**
     * Constructeur de l'exception
     * @param message Message d'erreur
     */
    public TwiskException(String message) {

        Alert dialogW = new Alert(Alert.AlertType.WARNING);
        dialogW.setHeaderText(null);
        dialogW.setContentText(message);
        dialogW.showAndWait();

    }
}
