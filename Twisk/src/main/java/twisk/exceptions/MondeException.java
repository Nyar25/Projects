package twisk.exceptions;

import javafx.scene.control.Alert;

public class MondeException extends Exception {
  /**
   * Constructeur de l'exception
   * @param message Message d'erreur
   */
  public MondeException(String message) {

    Alert dialogW = new Alert(Alert.AlertType.WARNING);
    dialogW.setTitle(" Erreur du Monde");
    dialogW.setHeaderText(null);
    dialogW.setContentText(message);
    dialogW.showAndWait();

  }
}
