package twisk.monde;

import twisk.outils.FabriqueNumero;

public class Guichet extends Etape{
    private int nbSemaphore;
    private int nbJetons;

    /**
     * Constructeur de Guichet
     * @param nom nom du guichet
     */
    public Guichet(String nom) {
        super(nom);
        this.nbJetons = 1;
        this.nbSemaphore = FabriqueNumero.getInstance().getNumeroSemaphore();
    }

    /**
     * Constructeur par copie
     * @param nom nom du Guichet
     * @param nb nombre de jetons
     */
    public Guichet(String nom, int nb) {
        super(nom);
        if (nb <= 0) {
            throw new IllegalArgumentException("Le nombre de jetons d'un guichet doit être strictement positif. Reçu : " + nb);
        }
        this.nbJetons = nb;
        this.nbSemaphore = FabriqueNumero.getInstance().getNumeroSemaphore();
    }

    /**
     *
     * @return Si le guichet est une activité
     */
    @Override
    public boolean estUneActivite() {
        return false;
    }

    /**
     *
     * @return Si le guichet est un guichet
     */
    @Override
    public boolean estUnGuichet() {
        return true;
    }

    /**
     *
     * @return le nombre de jeton du guichet
     */
    public int getNbJetons() {
        return nbJetons;
    }

    /**
     *
     * @return le Semaphore du guichet
     */
    public int getNbSemaphore() {
        return nbSemaphore;
    }

    /**
     *
     * @return ToString de Guichet avec les successeurs
     */
    @Override
    public String toString() {
        int nb = nbSuccesseurs();
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" : ");
        sb.append(nb);
        sb.append(" successeur");
        if(nb>1){
            sb.append("s");
        }
        sb.append(" - ");
        for(Etape e : this.getSuccesseurs()){
            sb.append(e.getNom());
            if(nb>1){
                sb.append(" / ");
                nb--;
            }
        }
        return sb.toString();
    }

    /**
     * On sait que la suite d'un Guichet est forcément une activité restreinte
     *
     * @return un StringBuilder ce qui est produit dans le client.c
     * transfert(ACTIVITE1,GUICHET1);
     *     delai(4.1);
     *     P(ids,SEMAPHORE_GUICHET1);
     *     delai(5,4);
     *     transfert(GUICHET1,ACTIVITE3);
     *     delai(5,2);
     *     V(ids,SEMAPHORE_GUICHET1);
     */
    @Override
    public String toC(){
        StringBuilder sb = new StringBuilder();
        sb.append("P(ids,SEMAPHORE_GUICHET").append(this.nbSemaphore).append(");\n");
        sb.append("transfert(").append(this.getNom().replace(" ","_")).append(",").append(getNomSuccesseur(1).replace(" ","_")).append(");\n");
        sb.append("delai(").append(this.getNextEtape(0).getTemps()).append(",").append(this.getNextEtape(0).getEcartTemps()).append(");\n");
        sb.append("V(ids,SEMAPHORE_GUICHET").append(this.nbSemaphore).append(");\n");
        sb.append(this.getNextEtape(0).toC());
        return sb.toString();
    }

    /**
     * Fonction qui retourne -1 car le guichet n'as pas de temps
     * @return -1 car c'est pas censé être utilisé
     */
    @Override
    public int getTemps() {
        return -1;
    }

    /**
     * Fonction qui retourne -1 car le guichet n'as pas d'écart de temps
     * @return -1 car c'est pas censé être utilisé
     */
    @Override
    public int getEcartTemps() {
        return -1;
    }
}
