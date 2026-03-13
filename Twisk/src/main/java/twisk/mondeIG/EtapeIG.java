package twisk.mondeIG;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

import java.util.Iterator;
import java.util.LinkedList;

public abstract class EtapeIG implements Iterable<PointDeControleIG>{
    private String nom;
    private String identifiant;
    private int posX;
    private int posY;
    private int largeur;
    private int hauteur;
    private LinkedList<PointDeControleIG> points;
    private boolean selected;
    private LinkedList<EtapeIG> predecesseurs;
    private LinkedList<EtapeIG> successeurs;
    private boolean estActiviteRestreinte;

    /**
     * Constructeur de l'étape
     * @param nom Nom de l'étape
     * @param larg Largeur de l'étape
     * @param haut Hauteur de l'étape
     */
    public EtapeIG(String nom, int larg, int haut) {

        if (nom == null) {
            Alertes.afficherErreur("Le nom de l'étape ne peut pas être null.");
            throw new IllegalArgumentException("nom ne peut pas être null");
        }
        if (larg < 0) {
            Alertes.afficherErreur("La largeur doit être positive ou nulle.");
            throw new IllegalArgumentException("larg doit être >= 0");
        }
        if (haut < 0) {
            Alertes.afficherErreur("La hauteur doit être positive ou nulle.");
            throw new IllegalArgumentException("haut doit être >= 0");
        }

        this.nom = nom;
        this.posX = TailleComposants.getInstance().CreationPosX();
        this.posY = TailleComposants.getInstance().CreationPosY();
        this.largeur = larg;
        this.hauteur = haut;
        this.points = new LinkedList<>();
        this.ajoutPointsDeControle();
        this.selected = false;
        this.predecesseurs = new LinkedList<>();
        this.successeurs = new LinkedList<>();
        this.estActiviteRestreinte = false;
    }

    public void setIdentifiant(String id ){
        this.identifiant = id ;
    }


    /**
     * Getter de l'abscisse de l'étape (coin en haut à gauche)
     * @return L'attribut posX
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Getter de l'ordonnée de l'étape (coin en haut à gauche)
     * @return L'attribut posY
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Setter de l'abscisse de l'étape
     * @param posX Nouvelle abscisse de l'étape
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * Setter de l'ordonnée de l'étape
     * @param posY Nouvelle ordonnée de l'étape
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * Getter de la largeur de l'étape
     * @return L'attribut largeur
     */
    public int getLargeur() {
        return largeur;
    }

    /**
     * Getter de la hauteur de l'étape
     * @return L'attribut hauteur
     */
    public int getHauteur() {
        return hauteur;
    }

    /**
     * Getter du nom de l'étape
     * @return L'attribut nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Setter du nom de l'étape
     * @param nom Nouveau nom de l'étape
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Getter de l'identifiant de l'étape
     * @return L'attribut identifiant
     */
    public String getIdentifiant() {
        return identifiant;
    }

    /**
     * Fonction qui crée et ajoute les points de contrôle
     */
    private void ajoutPointsDeControle() {
        int hauteurLabel = TailleComposants.getInstance().getHauteurLabel();
        int padding = TailleComposants.getInstance().getPadding();
        points.add(new PointDeControleIG(this,posX+largeur/2+padding/2,posY));
        points.add(new PointDeControleIG(this,posX+largeur+padding/2,posY+hauteur/2+hauteurLabel/2+padding));
        points.add(new PointDeControleIG(this,posX+largeur/2+padding/2,posY+hauteur+padding+hauteurLabel+padding/2));
        points.add(new PointDeControleIG(this,posX,posY+hauteur/2+hauteurLabel/2+padding));
    }

    /**
     * Getter de l'itérateur de la LinkedList de PointDeContrôle
     * @return L'itérateur des points de contrôle
     */
    public Iterator<PointDeControleIG> iterator() {
        return this.points.iterator();
    }

    /**
     * Getter du booléen pour savoir si une étape est sélectionné
     * @return L'attribut selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Setter du booléen pour savoir si une étape est sélectionné
     */
    public void setSelected() {
        this.selected = !this.selected;
    }

    /**
     * Fonction qui met à jour les coordonnées des points de contrôle
     */
    public void updatePointsDeControle() {
        int hauteurLabel = TailleComposants.getInstance().getHauteurLabel();
        int padding = TailleComposants.getInstance().getPadding();
        points.getFirst().setCentreX(posX+largeur/2+padding/2);
        points.getFirst().setCentreY(posY);
        points.get(1).setCentreX(posX+largeur+padding/2);
        points.get(1).setCentreY(posY+hauteur/2+hauteurLabel/2+padding);
        points.get(2).setCentreX(posX+largeur/2+padding/2);
        points.get(2).setCentreY(posY+hauteur+padding+hauteurLabel+padding/2);
        points.getLast().setCentreX(posX);
        points.getLast().setCentreY(posY+hauteur/2+hauteurLabel/2+padding);
    }

    /**
     * Ajout à la LinkLedList Predecesseur une etape
     * @param etape L'étape qu'on veut ajouter
     */
    public void ajouterPredecesseur(EtapeIG etape){
        this.predecesseurs.add(etape);
    }

    /**
     * Ajoute à la LinkedList Successeur une etape
     * @param etape L'étape qu'on veut ajouter
     */
    public void ajouterSuccesseur(EtapeIG etape){
        this.successeurs.add(etape);
    }

    /**
     * Retirer à la LinkedList Predecesseur une etape
     * @param etape L'étape qu'on veut retirer
     */
    public void retirerPredecesseur(EtapeIG etape){
        this.predecesseurs.remove(etape);
    }

    /**
     * Retirer à la LinkedList Successeur une etape
     * @param etape L'etape qu'on veut retirer
     */
    public void retirerSuccesseur(EtapeIG etape){
        this.successeurs.remove(etape);
    }

    /**
     * getter de predecesseur
     * @return La LinkedList d'EtapeIG predecesseurs
     */
    public LinkedList<EtapeIG> getPredecesseurs() {
        return predecesseurs;
    }

    /**
     * getter Successeurs
     * @return la LinkedList d'EtapeIG successeur
     */
    public LinkedList<EtapeIG> getSuccesseurs() {
        return successeurs;
    }

    /**
     * @return Renvoie la taille de Predecesseurs
     */
    public int getNbPredecesseurs() {
        return predecesseurs.size();
    }

    /**
     * @return Renvoie la taille de Sucesseurs
     */
    public int getNbSuccesseurs() {
        return successeurs.size();
    }

    /**
     * Getter de la LinkedList successeur
     * @param ind L'indice auquel on veut acceder
     * @return l'EtapeIG correspondant à l'indice
     */
    public EtapeIG getSuccesseur(int ind) {
        return successeurs.get(ind);
    }

    /**
     * @return Boolean pour savoir si c'est un Guichet. Override par GuichetIG
     */
    public boolean estUnGuichet(){
        return false;
    }

    /**
     * @return Boolean pour savoir si c'est un Guichet. Override par ActiviteIG
     */
    public boolean estUneActivite(){
        return false;
    }

    /**
     *
     * @return Boolean pour savoir si c'est une activiteRestreinte
     */
    public boolean estUneActiviteRestreinte(){
        return this.estActiviteRestreinte;
    }

    /**
     * Savoir qui inverse le boolean estActiviteRestreinte
     */
    public void setEstActiviteRestreinte(){
        this.estActiviteRestreinte = !this.estActiviteRestreinte;
    }
}
