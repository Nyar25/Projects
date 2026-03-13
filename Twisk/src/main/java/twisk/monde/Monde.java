package twisk.monde;

import twisk.outils.KitC;

import java.util.Iterator;

public class Monde implements Iterable<Etape> {

    private GestionnaireEtapes lesEtapes;
    private SasEntree entree;
    private SasSortie sortie;
    private KitC kitc;
    private int[] semaphores;

    /**
     * Constructeur Monde
     *
     */
    public Monde() {
        lesEtapes = new GestionnaireEtapes();
        entree = new SasEntree();
        sortie = new SasSortie();
        lesEtapes.ajouter(entree, sortie);
        this.kitc = new KitC(this);
        semaphores = new int[nbGuichets()];
    }

    /**
     *
     * @param etapes set comme Entrée dans le monde
     */
    public void aCommeEntree(Etape... etapes){
        this.entree.ajouterSuccesseur(etapes);
    }

    /**
     *
     * @param etapes set comme Sortie dans le monde
     */
    public void aCommeSortie(Etape... etapes) {
        if (etapes == null) {
            throw new IllegalArgumentException("La liste des étapes ne peut pas être null.");
        }
        if (etapes.length == 0) {
            throw new IllegalArgumentException("La liste des étapes ne peut pas être vide.");
        }
        for (Etape e : etapes) {
            if (e == null) {
                throw new IllegalArgumentException("Une des étapes dans la liste est null.");
            }
            e.ajouterSuccesseur(sortie);
        }
    }

    /**
     *
     * @param etapes ajout des étapes Gestionnaire d'étape
     */
    public void ajouter(Etape... etapes) {
        this.lesEtapes.ajouter(etapes);
    }

    /**
     *
     * @return le nombre d'étape
     */
    public int nbEtapes(){
        return lesEtapes.nbEtapes();
    }

    /**
     *
     * @return le nb de guichet
     */
    public int nbGuichets(){
        int res = 0;
        // Pour toutes les étapes du gestionnaire
        for(Etape e : this){
            // Si l'étape est un guichet alors on incrémente le résultat
            if(e.estUnGuichet()){
                res++;
            }
        }
        return res;
    }

    /**
     *
     * @return Le nb d'activité
     */
    public int nbActivite(){
        int res = 0;
        // Pour toutes les étapes du gestionnaire
        for(Etape e : this){
            // Si l'étape est une activite alors on incrémente le résultat
            if(e.estUneActivite()){
                res++;
            }
        }
        return res;
    }

    /**
     *
     * @return rend le gestionnaire d'étape itérable
     */
    public Iterator<Etape> iterator() {
        return lesEtapes.iterator();
    }

    /**
     *
     * @return getter Gestionnaire d'étape
     */
    public GestionnaireEtapes getLesEtapes() {
        return this.lesEtapes;
    }

    /**
     * Getter du sas d'entrée
     * @return Le sas d'entrée
     */
    public SasEntree getEntree() {
        return this.entree;
    }

    /**
     * Getter du sas de sortie
     * @return Le sas de sortie
     */
    public SasSortie getSortie() {
        return this.sortie;
    }

    /**
     * Getter du KitC
     * @return Le KitC
     */
    public KitC getKitC(){
        return kitc;
    }

    /**
     * Getter de du tableau de semaphores
     * @return Le tableau de sémaphores
     */
    public int[] getSemaphores(){
        return semaphores;
    }

    /**
     *
     * @return ToString monde
     */
    @Override
    public String toString() {
        return lesEtapes.toString();
    }

    /**
     *
     * @param i Indice
     * @return L'étape a l'indice donné
     */
    public Etape getEtape(int i){
        if (i < 0) {
            throw new IllegalArgumentException("L'indice doit être supérieur ou égal à 0.");
        }
        return this.lesEtapes.getEtape(i);
    }

    /**
     * Creer un tableau de Guichets de size nbGuichets. Pour chaque etape, si c'est un guichet, renvoie les Jetons
     * @return renvoie les Semaphores des guichets
     */
    public int[] getSemaphoresGuichets(){
        int[] tab = new int[nbGuichets()];
        for(Etape e : this){
            if(e.estUnGuichet()){
                Guichet g = (Guichet) e;
                tab[g.getNbSemaphore()-1] = g.getNbJetons();
            }
        }
        return tab;
    }


    /**
     *
     * @return Un string contenant l'entête avec les include, les define, et l'appel de void simulation( int ids)
     */
    public String toC(){
        StringBuilder sb = new StringBuilder();
        sb.append("#include <stdlib.h>\n");
        sb.append("#include <stdio.h>\n");
        sb.append("#include <time.h>\n");
        sb.append("#include <unistd.h>\n");
        sb.append("#include \"def.h\"\n\n");
        sb.append(this.kitc.faireDefine());
        sb.append("\n");
        sb.append("int getNbEtapes(){\n");
        sb.append("return NBETAPES;\n");
        sb.append("}\n\n");
        sb.append("int getNbGuichets(){\n");
        sb.append("return NBGUICHETS;\n");
        sb.append("}\n\n");
        sb.append("void simulation(int ids){\n\n");
        sb.append(this.entree.toC());
        sb.append("\n}\n");
        return sb.toString();
    }
}