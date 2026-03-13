package twisk.vues;

import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import twisk.mondeIG.EtapeIG;
import twisk.mondeIG.MondeIG;
import twisk.exceptions.Alertes;

public class VueMenu extends MenuBar implements Observateur{
    private MondeIG monde;

    /**
     * Constructeur de la vue du menu
     * @param monde Monde qui sera lié à la vue
     */
    public VueMenu(MondeIG monde) {
        super();
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.monde = monde;

        Menu fichier = new Menu("Fichier");
        MenuItem quitter = new MenuItem("Quitter");

        Menu edition = new Menu("Edition");
        MenuItem supprimer = new MenuItem("Supprimer");
        MenuItem renommer = new MenuItem("Renommer la selection");
        MenuItem effacer = new MenuItem("Effacer la selection");

        Menu m = new Menu("Monde");
        MenuItem entree = new MenuItem("Entree");
        MenuItem sortie = new MenuItem("Sortie");

        Menu parametres = new Menu("Parametres");
        MenuItem test = new MenuItem("Set Delai, Set Ecart");

        fichier.getItems().add(quitter);
        edition.getItems().addAll(supprimer,renommer,effacer);
        parametres.getItems().add(test);
        m.getItems().addAll(entree,sortie);
        this.getMenus().addAll(fichier,edition,m,parametres);

        EcouteurRenommer rename = new EcouteurRenommer(monde);
        EcouteurTemps time = new EcouteurTemps(monde);
        EcouteurJeton jeton = new EcouteurJeton(monde);

        // Quitter
        quitter.setOnAction(
                e -> Platform.exit()
        );

        // Suppresion de la selection
        supprimer.setOnAction(
                e -> monde.supprimer()
        );

        // Renommage de la selection
        renommer.setOnAction(rename);

        // Efface les Etapes sélectionnées et les vues associées
        effacer.setOnAction(e -> monde.effacerSelection());

        // Definir Entree
        entree.setOnAction(e -> {
            for(EtapeIG etape : monde){
                if(etape.isSelected()){
                    if(!monde.estEntree(etape)){
                        monde.ajouterEntree(etape);
                    } else {
                        monde.enleverEntree(etape);
                    }
                    monde.enleverEtapeSelectionner(etape);
                }
            }
        });

        // Definir Sortie
        sortie.setOnAction(e -> {
            for(EtapeIG etape : monde){
                if(etape.isSelected()){
                    if(!monde.estSortie(etape)){
                        monde.ajouterSortie(etape);
                    } else {
                        monde.enleverSortie(etape);
                    }
                    monde.enleverEtapeSelectionner(etape);
                }
            }
        });

        // Set le delai et l'écart pour les activites ou le nombre de jetons pour les guichets

        // Utilisation d'une lambda car c'est une initialisation à la création
        test.setOnAction(e -> {

            if (monde.getEtapesSelecteds().size() == 1) {

                EtapeIG etape = monde.getEtapesSelecteds().getFirst();

                if ( etape.estUneActivite() ){
                    time.handle(e);
                }

                if ( etape.estUnGuichet() ){
                    jeton.handle(e);
                }

            monde.netoyageEtapeSelectionnee();

            } else {
                System.out.println("Veuillez sélectionner une seule étape.");
            }
        });


    }

    /**
     * Reagir vide
     */
    @Override
    public void reagir() {

    }
}
