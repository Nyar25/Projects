package twisk.mondeIG;

import twisk.exceptions.Alertes;

public class ClientIG {
    private int centreX;
    private int centreY;
    private int couleur;
    private EtapeIG etape;

    /**
     * Constructeur de ClientIG
     * @param centreX la Position X
     * @param centreY La Position Y
     * @param couleur La couleur
     * @param etape L'Etape
     */
    public ClientIG(int centreX, int centreY, int couleur, EtapeIG etape) {

        if (centreX < 0) {
            Alertes.afficherErreur("Le centre X ne peut pas être négatif.");
            throw new IllegalArgumentException("centreX doit être >= 0");
        }
        if (centreY < 0) {
            Alertes.afficherErreur("Le centre Y ne peut pas être négatif.");
            throw new IllegalArgumentException("centreY doit être >= 0");
        }
        if (couleur <= 0) {
            Alertes.afficherErreur("La couleur doit être strictement positive.");
            throw new IllegalArgumentException("couleur doit être > 0");
        }
        if (etape == null) {
            Alertes.afficherErreur("L'étape associée au client ne peut pas être nulle.");
            throw new IllegalArgumentException("etape ne peut pas être null");
        }
        this.centreX = centreX;
        this.centreY = centreY;
        this.couleur = couleur;
        this.etape = etape;
    }

    /**
     * Getter CentreX
     * @return Renvoie la position X
     */
    public int getCentreX() {
        return centreX;
    }

    /**
     * Getter CentreY
     * @return Renvoie la position Y
     */
    public int getCentreY() {
        return centreY;
    }

    /**
     * Getter couleur
     * @return entier
     */
    public int getCouleur() {
        return couleur;
    }

    /**
     * Getter Etape
     * @return L'EtapeIG
     */
    public EtapeIG getEtape() {
        return etape;
    }
}