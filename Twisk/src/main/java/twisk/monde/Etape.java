package twisk.monde;

import twisk.outils.FabriqueNumero;

import java.text.Normalizer;
import java.util.Iterator;

public abstract class Etape implements Iterable<Etape> {
    private String nom;
    private GestionnaireEtapes successeurs;
    private int numero;

    /**
     *
     * @param nom Nom de l'étape
     */
    public Etape(String nom) {
        if (nom == null) {
            throw new IllegalArgumentException("Le nom de l'étape ne peut pas être null.");
        }
        String newNom = Normalizer.normalize(nom, Normalizer.Form.NFD);
        newNom = newNom.replaceAll("\\p{M}", "");
        this.nom = newNom;
        this.successeurs = new GestionnaireEtapes();
        this.numero = FabriqueNumero.getInstance().getNumeroEtape();
    }

    /**
     *
     * @param e ajout un successeur à l'étape
     */
    public void ajouterSuccesseur(Etape... e){
        this.successeurs.ajouter(e);
    }

    /**
     *
     * @return abstract de estUneActivite
     */
    public abstract boolean estUneActivite();

    /**
     *
     * @return abstract de estUnGuichet
     */
    public abstract boolean estUnGuichet();

    /**
     *
     * @return iterateur Etape
     */
    public Iterator<Etape> iterator(){
        return this.successeurs.iterator();
    }

    /**
     *
     * @return getter nom de l'étape
     */
    public String getNom() {
        return nom;
    }

    /**
     *
     * @return getter Successeurs
     */
    public GestionnaireEtapes getSuccesseurs() {
        return successeurs;
    }

    /**
     *
     * @return nombre de Successeurs
     */
    public int nbSuccesseurs() {
        return this.successeurs.nbEtapes();
    }

    /**
     *
     * @return getter du numero de l'étape
     */
    public int getNumero() {
        return numero;
    }

    /**
     *
     * @return ToString Etape
     */
    @Override
    public String toString() {
        return nom;
    }

    /**
     *
     * @return abstract du toC de Etape.java
     */
    public abstract String toC();

    /**
     * Renvoie l'étape voulue
     * @param i paramètre qui sera souvent à 1
     * @return une étape
     */
    public Etape getNextEtape(int i){

        if (i < 0) {
            throw new IllegalArgumentException("L'indice doit être positif  : " + i);
        }
        if (i >= successeurs.nbEtapes()) {
            throw new IndexOutOfBoundsException("L'indice " + i + " dépasse le nb de successeurs (" + successeurs.nbEtapes() + ").");
        }
        return this.successeurs.getEtape(i);
    }

    /**
     * Fonction qui donne le nom d'une étape à un indice donné
     * @param i indice de l'étape dont on veut le nom
     * @return Le nom d'une étape
     */
    public String getNomSuccesseur(int i){

        if (i <= 0) {
            throw new IllegalArgumentException("L'indice i doit être positif : " + i);
        }

        Etape res = this;
        while(i>0){
            res = res.getNextEtape(0);
            i--;
        }
        return res.getNom();
    }

    public String getNomDefine(String nom){
        nom = nom.replace("±", "");
        return nom.replace(" ","_");
    }

    /**
     *
     * @return abstract de getTemps de Etape.java
     */
    public abstract int getTemps();

    /**
     *
     * @return abstract de getEcartTemps de Etape.java
     */
    public abstract int getEcartTemps();
}
