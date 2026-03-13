package twisk.mondeIG;

import twisk.exceptions.Alertes;
import twisk.outils.FabriqueNumero;

public class PointDeControleIG {
    private int centreX;
    private int centreY;
    private String id;
    private EtapeIG etape;
    private boolean selected;

    /**
     * Constructeur d'un point de contrôle
     * @param etape Etape auquel le point est relié
     * @param centreX Abscisse du centre du point
     * @param centreY Ordonnée du centre du point
     */
    public PointDeControleIG(EtapeIG etape, int centreX, int centreY) {
        if (etape == null) {
            Alertes.afficherErreur( " Le PDC est null .");
            throw new IllegalArgumentException(" PDC ne peut pas être null");
        }
        if (centreX < 0) {
            Alertes.afficherErreur("La coordonnée centreX doit être positive ou nulle.");
            throw new IllegalArgumentException("centreX doit être >= 0");
        }
        if (centreY < 0) {
            Alertes.afficherErreur("La coordonnée centreY doit être positive ou nulle.");
            throw new IllegalArgumentException("centreY doit être >= 0");
        }
        this.etape = etape;
        this.centreX = centreX;
        this.centreY = centreY;
        this.id = FabriqueNumero.getInstance().getIdentifiantPointDeControle();
        this.selected = false;
    }

    /**
     * Getter de l'abscisse du centre
     * @return L'attribut centreX
     */
    public int getCentreX() {
        return centreX;
    }

    /**
     * Getter de l'ordonnée du centre
     * @return L'attribut centreY
     */
    public int getCentreY() {
        return centreY;
    }

    /**
     * Setter du booléen de séléction
     */
    public void setSelected() {
        this.selected = !this.selected;
    }

    /**
     * Getter du booléen de séléction
     * @return L'attribut selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Getter d'étaoe
     * @return L'attribut étape
     */
    public EtapeIG getEtape() {
        return etape;
    }

    /**
     * Setter de l'abscisse du centre du point
     * @param centreX Nouvelle abscisse du point
     */
    public void setCentreX(int centreX) {
        this.centreX = centreX;
    }

    /**
     * Setter de l'ordonnée du centre du point
     * @param centreY Nouvelle ordonnée du point
     */
    public void setCentreY(int centreY) {
        this.centreY = centreY;
    }
}
