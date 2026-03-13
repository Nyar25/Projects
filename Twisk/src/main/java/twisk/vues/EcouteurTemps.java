package twisk.vues;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import twisk.exceptions.TwiskException;
import twisk.mondeIG.ActiviteIG;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class EcouteurTemps implements EventHandler<ActionEvent> {

    public MondeIG monde;

    /**
     * Constructeur de l'écouteur
     * @param monde Le mondeIG
     */
    public EcouteurTemps(MondeIG monde) {
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.monde = monde;
    }

    /**
     * Permet de modifier les delais pour les activites
     * @param actionEvent
     */
    @Override
    public void handle(ActionEvent actionEvent) {

            try {

                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Mettre à jour le delai et l'ecart");
                dialog.setHeaderText("Exemple 4,2 ");
                dialog.getEditor().setPromptText("Exemple 4,2");
                dialog.showAndWait();
                String traitement = dialog.getEditor().getText();

                // Split qui ce fait au niveau de la virgule
                if ( !(traitement.contains(","))){
                    dialog.close();

                } else {

                    String[] mots = traitement.split(",");

                    // S'assure qu'il y a bien 2 string qui vont être Parse
                    if ( mots.length != 2 ){
                        throw new TwiskException("Paramètre(s) vide(s)");
                    }
                    // delai == avant Virgule, ecart == après Virgule
                    int delai = Integer.parseInt(mots[0].trim());
                    int ecart = Integer.parseInt(mots[1].trim());

                    if(delai <= 0){
                        throw new TwiskException("Temps invalide");
                    } else if(ecart < 0){
                        throw new TwiskException("Ecart temps invalide");
                    } else if(ecart >= delai){
                        throw new TwiskException("L'écart temps est supérieur ou égale au temps");
                    } else {
                        // On sait que c'est une activiteIG
                        ActiviteIG etape = (ActiviteIG) monde.getEtapesSelecteds().getFirst();
                        String nom = etape.getNom();
                        // Supprime les " 3s ±1" à la fin
                        nom = nom.replaceAll(" \\d+s ±\\d+$", "");
                        String nouveauNom = nom + " " + delai + "s ±" + ecart;
                        etape.setDelai(delai);
                        etape.setEcart(ecart);
                        monde.setNomEtape(etape, nouveauNom);

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
