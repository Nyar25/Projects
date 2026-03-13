package twisk.outils;

import java.util.Random;

public class TailleComposants {
    private static final TailleComposants instance = new TailleComposants();
    private final int HAUTEURCLIENTS = 50;
    private final int LARGEURACTIVITE = 120;
    private final int HAUTEURBOUTON = 58;
    private final int LARGEURBOUTON = 68;
    private final int RAYONPOINTDECONTROLE = 5;
    private final int HAUTEURLABEL = 16;
    private final int PADDING = 10;
    private static final int HAUTEURSCENE = 800;
    private static final int LARGEURSCENE = 1000;
    private static final int RAYONCLIENT = 4;


    /**
     * Constructeur vide
     */
    private TailleComposants(){

    }

    /**
     * @return Renvoie une instance
     */
    public static TailleComposants getInstance(){
        return instance;
    }

    /**
     *
     * @return Renvoie la hauteur defini pour le slcients
     */
    public int getHauteurEtape() {
        return HAUTEURCLIENTS;
    }

    /**
     *
     * @return Renvoie la Largueur deifni pour les activites
     */
    public int getLargeurEtape() {
        return LARGEURACTIVITE;
    }

    /**
     *
     * @return Renvoie la hauteur defini pour les boutons
     */
    public int getHauteurBouton() {
        return HAUTEURBOUTON;
    }

    /**
     *
     * @return Renvoie la Largueur defini pour les Boutons
     */
    public int getLargeurBouton() {
        return LARGEURBOUTON;
    }

    /**
     *
     * @return Renvoie le Rayon des PDC
     */
    public int getRayonPointDeControle(){
        return RAYONPOINTDECONTROLE;
    }

    /**
     *
     * @return Renvoie la Hauteur des Labels
     */
    public int getHauteurLabel(){
        return HAUTEURLABEL;
    }

    /**
     *
     * @return Renvoie le Padding
     */
    public int getPadding(){
        return PADDING;
    }

    /**
     *
     * @return REnvoie la largeur de la Scene
     */
    public int getLargeurScene(){
        return LARGEURSCENE;
    }

    /**
     *
     * @return Renvoie la Hauteur de la scene
     */
    public int getHauteurScene(){
        return HAUTEURSCENE;
    }

    /**
     *
     * @return Renvoie le Rayon pour les clients
     */
    public int getRayonClient(){
        return RAYONCLIENT;
    }

    /**
     * Calcule une position X aléatoire en fonction de la largeur de l'étape.
     * @return Position X aléatoire dans les limites de la scène
     */
    public int CreationPosX() {
        Random r = new Random();
        int maxX = this.getLargeurScene() - this.getLargeurEtape();
        return r.nextInt(Math.max(50, maxX));
    }

    /**
     * Calcule une position Y aléatoire en fonction de la hauteur de l'étape,
     * en tenant compte des autres composants graphiques (bouton, padding, label).
     * @return Position Y aléatoire dans les limites de la scène
     */
    public int CreationPosY() {
        Random r = new Random();
        int hauteurTotale = this.getHauteurEtape() + this.getHauteurLabel() + 2 * this.getPadding();
        int espaceDisponible = this.getHauteurScene() - this.getHauteurBouton() - hauteurTotale;
        return r.nextInt(Math.max(1, espaceDisponible));
    }




}
