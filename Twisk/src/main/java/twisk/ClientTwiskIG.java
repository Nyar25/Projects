package twisk;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import twisk.mondeIG.MondeIG;
import twisk.outils.TailleComposants;
import twisk.vues.VueMenu;
import twisk.vues.VueMondeIG;
import twisk.vues.VueOutils;

public class ClientTwiskIG extends Application {

    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Twisk");
        MondeIG monde = new MondeIG();
        BorderPane root = new BorderPane();
        root.setBottom(new VueOutils(monde));
        root.setCenter(new VueMondeIG(monde));
        root.setTop(new VueMenu(monde));
        Scene scene = new Scene(root, TailleComposants.getInstance().getLargeurScene(),TailleComposants.getInstance().getHauteurScene());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}