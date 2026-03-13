package twisk.vues;

import javafx.scene.layout.Pane;
import twisk.mondeIG.*;
import twisk.exceptions.Alertes;

import java.util.Iterator;

public class VueMondeIG extends Pane implements Observateur {

    private MondeIG sujet;

    /**
     * Constructeur de la vue d'un monde
     * @param monde Monde qui sera lié à la vue
     */
    public VueMondeIG(MondeIG monde) {
        super();
        if (monde == null) {
            Alertes.afficherErreur("Erreur : Le monde est null.");
            throw new IllegalArgumentException("monde null");
        }
        this.sujet = monde;
        sujet.ajouterObservateur(this);
        this.reagir();
        EcouteurDropAccepter dropAccpeter = new EcouteurDropAccepter(this);
        EcouteurDropActiver dropActiver = new EcouteurDropActiver(this);
        this.setOnDragOver(dropAccpeter);
        this.setOnDragDropped(dropActiver);
    }

    /**
     *
     * @return MondeIG
     */
    public MondeIG getSujet() {
        return sujet;
    }

    /**
     * Met à jour la Vue du MondeIG avec les différentes etapes, et les clients
     */
    @Override
    public void reagir() {

        getChildren().clear();
        Iterator<ArcIG> it = sujet.iteratorArcs();
        while(it.hasNext()){
            VueArcIG arc = new VueArcIG(sujet,it.next());
            getChildren().add(arc);
        }

        for(EtapeIG e : sujet){

            // Doublon de GetChildren car il doit se faire dans la même boucle
            if (e.estUnGuichet()){
                VueGuichetIG g = new VueGuichetIG(sujet,e);
                getChildren().add(g);
                g.relocate(e.getPosX(), e.getPosY());
            }
            if (e.estUneActivite()){
                VueActiviteIG v = new VueActiviteIG(sujet, e);
                getChildren().add(v);
                v.relocate(e.getPosX(), e.getPosY());
            }

            for(PointDeControleIG p : e){
                VuePointDeControleIG point = new VuePointDeControleIG(p,sujet);
                getChildren().add(point);
            }
        }

        for(ClientIG c : sujet.getClientsIG()){

            VueClientIG client = new VueClientIG(c);
            System.out.println(client.getCenterX());
            System.out.println(client.getCenterY());
            getChildren().add(client);
        }
    }
}
