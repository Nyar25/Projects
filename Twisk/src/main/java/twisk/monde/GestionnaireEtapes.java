package twisk.monde;

import java.util.Iterator;
import java.util.LinkedList;

public class GestionnaireEtapes implements Iterable<Etape> {
    private LinkedList<Etape> etapes;

    /**
     * Constructeur de GestionnaireEtapes
     */
    public GestionnaireEtapes() {
        this.etapes = new LinkedList<>();
    }

    /**
     *
     * @param etapes etape qu'on ajoute au gestionnaire
     */
    public void ajouter(Etape... etapes) {
        if (etapes == null) {
            throw new IllegalArgumentException("Le tableau d'étapes ne peut pas être null.");
        }
        if (etapes.length == 0) {
            throw new IllegalArgumentException("Le tableau d'étapes ne peut pas être vide.");
        }

        for (Etape etape : etapes) {
            if (etape == null) {
                throw new IllegalArgumentException("Une des étapes à ajouter est null.");
            }
            this.etapes.add(etape);
        }
    }
    /**
     *
     * @return le nb d'étape du gestionnaire
     */
    public int nbEtapes(){
        return this.etapes.size();
    }

    /**
     *
     * @return Iterateur Gestionnaire d'étape
     */
    public Iterator<Etape> iterator(){
        return this.etapes.iterator();
    }

    /**
     *
     * @param i Indice
     * @return L'étape a l'indice donné
     */
    public Etape getEtape(int i) {
        if (i < 0 || i >= etapes.size()) {
            throw new IndexOutOfBoundsException("Indice " + i + " invalide. Il doit être compris entre 0 et " + (etapes.size() - 1) + ".");
        }
        return this.etapes.get(i);
    }

    /**
     *
     * @return ToString de Gestionnaire d'étape avec les étapes qu'il gère
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Etape e : etapes){
            sb.append(e.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
