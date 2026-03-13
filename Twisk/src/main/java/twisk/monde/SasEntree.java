package twisk.monde;

public class SasEntree extends Activite{

    /**
     * Extends de activité, set le nom à entrée
     */

    public SasEntree(){
        super("SASENTREE");
    }

    /**
     *
     * @return Renvoie un String contenant les infos pour le SasEntree qui remplace les espaces au besoin
     */
    public String toC(){
        StringBuilder builder = new StringBuilder();
        builder.append("int nb;\n");
        builder.append("srand(time(NULL) * getpid()) ;\n");
        builder.append("nb = (int) ((rand()/(float) RAND_MAX)*").append(this.nbSuccesseurs()).append(");\n");
        builder.append("entrer(").append(getNom().replace(" ","_")).append(");\n");
        builder.append("delai(").append(this.getTemps()).append(",").append(this.getEcartTemps()).append(");\n");
        builder.append("switch(nb){\n");
        for(int i=0;i<this.nbSuccesseurs();i++){
            builder.append("case ").append(i).append(":\n");
            builder.append("transfert(").append(this.getNomDefine(this.getNom())).append(",").append(this.getNomDefine(getNomSuccesseur(1))).append(");\n");
            builder.append("break;\n");
        }
        builder.append("}\n");
        builder.append(this.getNextEtape(0).toC());
        return builder.toString();
    }
}
