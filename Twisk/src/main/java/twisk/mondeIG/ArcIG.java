package twisk.mondeIG;

import twisk.exceptions.Alertes;

public class ArcIG {
    private PointDeControleIG depart;
    private PointDeControleIG arrive;
    private boolean selected;

    /**
     * Constructeur d'un arc
     * @param depart Point de départ
     * @param arrive Point d'arrivée
     */
    public ArcIG(PointDeControleIG depart, PointDeControleIG arrive) {

        if (depart == null ) {
            Alertes.afficherErreur("Depart null ");
            throw new IllegalArgumentException(" Depart null ");
        }
        if (arrive == null) {
            Alertes.afficherErreur("Arrive null ");
            throw new IllegalArgumentException(" Arriver null");
        }

        this.depart = depart;
        this.arrive = arrive;
        this.selected = false;
    }

    /**
     * Getter de l'abscisse du départ de l'arc
     * @return L'abscisse de l'attribut depart
     */
    public int getDepartX() {
        return depart.getCentreX();
    }

    /**
     * Getter de l'ordonnée du départ de l'arc
     * @return L'ordonnée de l'attribut depart
     */
    public int getDepartY() {
        return depart.getCentreY();
    }

    /**
     * Getter de l'abscisse de l'arrivée de l'arc
     * @return L'abscisse de l'attribut arrive
     */
    public double getArriveX() {
        return arrive.getCentreX();
    }

    /**
     * Getter de l'ordonnée de l'arrivée de l'arc
     * @return L'ordonnée de l'attribut arrive
     */
    public double getArriveY() {
        return arrive.getCentreY();
    }

    /**
     * Getter du point de départ
     * @return L'attribut depart
     */
    public PointDeControleIG getDepart() {
        return depart;
    }

    /**
     * Getter du point d'arrivée
     * @return L'attribut arrive
     */
    public PointDeControleIG getArrive() {
        return arrive;
    }

    /**
     * Getter du booléen qui permet de savoir si l'arc est sélectionné ou non
     * @return L'attribut selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter du booléen qui permet de savoir si l'arc est sélectionné ou non
     */
    public void setSelected() {
        this.selected = !this.selected;
    }
}
