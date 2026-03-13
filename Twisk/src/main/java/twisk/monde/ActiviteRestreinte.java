package twisk.monde;

public class ActiviteRestreinte extends Activite {

    /**
     * Constructeur
     * @param nom nom de l'étape restreinte
     */
    public ActiviteRestreinte(String nom) {
        super(nom);
    }

    /**
     * Constructeur
     * @param nom de l'activité
     * @param t temps
     * @param e temps variabe
     */
    public ActiviteRestreinte(String nom, int t, int e) {
        super(nom, t, e);
    }

    /**
     *
     * @return un StringBuilder ce qui est produit dans le client.c
     * delai(nb,nb);
     * transfert(this.Etape,EtapeSuivante);
     */
    public String toC(){
        StringBuilder sb = new StringBuilder();
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
