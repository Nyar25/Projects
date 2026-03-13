package twisk.mondeIG;

import twisk.outils.FabriqueNumero;

public class ActiviteIG extends EtapeIG{
    private int delai;
    private int ecart;

    /**
     * Constructeur de l'activité
     * @param nom Nom de l'activité
     * @param larg Largeur de l'activité
     * @param haut Hauteur de l'activité
     */
    public ActiviteIG(String nom, int larg, int haut, int delai, int ecart) {
        super(nom, larg, haut);
        assert delai > 0;
        assert ecart >= 0;
        this.delai = delai;
        this.ecart = ecart;
        this.setIdentifiant(Integer.toString(FabriqueNumero.getInstance().getNumeroEtape()));
    }

    public int getDelai() {
        return delai;
    }

    public int getEcart() {
        return ecart;
    }

    public void setDelai(int delai) {
        this.delai = delai;
    }

    public void setEcart(int ecart) {
        this.ecart = ecart;
    }

    /**
     * Permet de savoir si l'activiteIG est restreinte ou non
     * @return
     */
    @Override
    public boolean estUneActivite(){
        return !this.estUneActiviteRestreinte();
    }
}