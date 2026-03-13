package twisk.monde;

public class Activite extends Etape {

    private int temps;
    private int ecartTemps;

    /**
     * Constructeur d'activité
     * @param nom de l'activité
     */
    public Activite(String nom) {
        super(nom);
        this.temps = 4;
        this.ecartTemps = 1;
    }

    /**
     * Constructeur d'activité
     * @param nom de l'activité
     * @param t temps fixe
     * @param e temps variable
     */
    public Activite(String nom, int t, int e) {
        super(nom);

        if (t <= 0) {
            throw new IllegalArgumentException("Le temps (t) doit être strictement supérieur à 0. Reçu : " + t);
        }
        if (e <= 0) {
            throw new IllegalArgumentException("L'écart de temps (e) doit être supérieur à 0. Reçu : " + e);
        }
        if (t <= e) {
            throw new IllegalArgumentException("Le temps (t) doit être strictement supérieur à l'écart (e). Reçus : t = " + t + ", e = " + e);
        }
        this.temps = t;
        this.ecartTemps = e;
    }

    /**
     * Implémentation de estUneactivite
     * @return Si c'est une activité (true )
     */
    @Override
    public boolean estUneActivite() {
        return true;
    }

    /**
     * Implémentation de EstUneGuichet
     * @return si C'est un guichet (false)
     */
    @Override
    public boolean estUnGuichet() {
        return false;
    }

    /**
     *
     * @return getter du temps variable
     */
    public int getEcartTemps() {
        return ecartTemps;
    }

    /**
     *
     * @return getter du temps fixe
     */
    public int getTemps() {
        return temps;
    }

    /**
     * ToString de l'activité
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
        for(Etape e : this){
            sb.append(e.getNom());
            if(nb>1){
                sb.append(" / ");
                nb--;
            }
        }
        return sb.toString();
    }

    /**
     *
     * @return le numéro du successeur
     */
    public int getNumeroSuccesseur(){
        return iterator().next().getNumero();
    }

    /**
     *
     * @return un StringBuilder ce qui est produit dans le client.c
     * delai(nb,nb);
     * transfert(cetteEtape,EtapeSuivante);
     */
    public String toC(){
       StringBuilder sb = new StringBuilder();
       sb.append("delai(").append(this.temps).append(",").append(this.ecartTemps).append(");\n");
       sb.append("switch(nb){\n");
       for(int i=0;i<this.nbSuccesseurs();i++){
           sb.append("case ").append(i).append(":\n");
           sb.append("transfert(").append(this.getNomDefine(this.getNom())).append(",").append(this.getNomDefine(getNomSuccesseur(1))).append(");\n");
           sb.append("break;\n");
       }
       sb.append("}\n");
       sb.append(this.getNextEtape(0).toC());
       return sb.toString();
    }
}