package twisk.mondeIG;

import twisk.outils.FabriqueNumero;

public class GuichetIG extends EtapeIG{
    private int nbJetons;

    /**
     * Constructeur du guichet
     * @param nom Nom du guichet
     * @param larg Largeur du guichet
     * @param haut Hauteur du guichet
     */
    public GuichetIG(String nom, int larg, int haut, int jetons) {
        super(nom, larg, haut);
        assert jetons > 0;
        this.nbJetons = jetons;
        this.setIdentifiant(Integer.toString(FabriqueNumero.getInstance().getNumeroEtape()));
    }

    public int getNbJetons() {
        return nbJetons;
    }

    public void setNbJetons(int nbJetons) {
        this.nbJetons = nbJetons;
    }

    /**
     *
     * @return Boolean pour savoir si c'est un Guichet
     */
    @Override
    public boolean estUnGuichet() {
        return true;
    }

}
