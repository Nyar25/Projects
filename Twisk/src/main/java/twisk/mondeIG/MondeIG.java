package twisk.mondeIG;

import twisk.exceptions.MondeException;
import twisk.exceptions.TwiskException;
import twisk.exceptions.Alertes;
import twisk.outils.TailleComposants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class MondeIG extends SujetObserve implements Iterable<EtapeIG>{

    private HashMap<String,EtapeIG> etapes;
    private LinkedList<ArcIG> arcs;
    private LinkedList<EtapeIG> etapesSelecteds;
    private LinkedList<ArcIG> arcsSelecteds;
    private LinkedList<EtapeIG> entrees;
    private LinkedList<EtapeIG> sorties;
    private LinkedList<ClientIG> clients;

    /**
     * Constructeur du MondeIG
     */
    public MondeIG(){
        super();
        etapes = new HashMap<>();
        arcs = new LinkedList<>();
        this.ajouterEtape("Activite");
        etapesSelecteds = new LinkedList<>();
        arcsSelecteds = new LinkedList<>();
        entrees = new LinkedList<>();
        sorties = new LinkedList<>();
        clients = new LinkedList<>();
        this.ajouterEntree(etapes.get("0"));
        this.ajouterSortie(etapes.get("0"));
    }

    /**
     * Fonction qui ajoute une étape du type voulue
     * @param type Type d'étape que l'on veut ajouter
     */
    public void ajouterEtape(String type){
        if(type.equals("Activite")){
            int largeur = TailleComposants.getInstance().getLargeurEtape();
            int hauteur = TailleComposants.getInstance().getHauteurEtape();
            ActiviteIG ac = new ActiviteIG(type + " " + etapes.size(),largeur,hauteur,4,1);
            this.etapes.put(ac.getIdentifiant(),ac);
            this.notifierObservateurs();
        } else if(type.equals("Guichet")){
            int largeur = TailleComposants.getInstance().getLargeurEtape();
            int hauteur = TailleComposants.getInstance().getHauteurEtape();
            GuichetIG guichetIG = new GuichetIG(type + " " + etapes.size(),largeur,hauteur,1);
            this.etapes.put(guichetIG.getIdentifiant(),guichetIG);
            this.notifierObservateurs();
        }
    }

    /**
     * Fonction qui ajoute un arc valide dans la liste
     * @param pt1 Point de départ de l'arc
     * @param pt2 Point d'arrivée de l'arc
     * @throws TwiskException Classe d'exception de Twisk
     */
    public void ajouter(PointDeControleIG pt1, PointDeControleIG pt2) throws TwiskException{
        if(pt1.getEtape() == pt2.getEtape()){
            throw new TwiskException("Arc invalide : c'est une boucle");
        } else {
            boolean trouve = false;
            Iterator<ArcIG> it = iteratorArcs();
            while(it.hasNext() && !trouve){
                ArcIG test = it.next();
                if(pt1.getEtape() == test.getDepart().getEtape() && pt2.getEtape() == test.getArrive().getEtape()){
                    throw new TwiskException("Arc invalide : arc déjà créer");
                }
            }
            ArcIG arc = new ArcIG(pt1, pt2);
            this.arcs.add(arc);
            pt1.getEtape().ajouterSuccesseur(pt2.getEtape());
            pt2.getEtape().ajouterPredecesseur(pt1.getEtape());
            this.notifierObservateurs();
        }
    }

    /**
     * Fonction qui change le booléen pour savoir si le point de contrôle
     * @param pt Point de contrôle
     */
    public void clicPointDeControle(PointDeControleIG pt){
        pt.setSelected();
        this.notifierObservateurs();
    }

    /**
     * Fonction qui change le booléen qui permet de savoir si on a sélectionné ou non une étape
     * @param etape Etape qu'on a sélectionné
     */
    public void clicEtape(EtapeIG etape){
        etape.setSelected();
        this.notifierObservateurs();
    }

    /**
     * Fonction qui change le booléen qui permet de savoir si on a sélectionné ou non un arc
     * @param arc Arc qu'on a sélectionné
     */
    public void clicArc(ArcIG arc){
        arc.setSelected();
        this.notifierObservateurs();
    }

    /**
     * Fonction qui vérifie si on peut ajouter un arc
     * @return vrai si on a sélectionné 2 points de contrôle, sinon renvoie faux
     */
    public boolean peutAjouterArc(){
        int res = 0;
        for(EtapeIG e : etapes.values()){
            for(PointDeControleIG p : e){
                if(p.isSelected()){
                    res++;
                }
            }
        }
        return res == 2;
    }

    /**
     * Donne l'itérateur d'étapes sur la HashMap
     * @return L'itérateur d'étapes
     */
    @Override
    public Iterator<EtapeIG> iterator(){
        return this.etapes.values().iterator();
    }

    /**
     * Donne l'itérateur d'arcs sur la LinkedList
     * @return L'itérateur d'arcs
     */
    public Iterator<ArcIG> iteratorArcs(){
        return this.arcs.iterator();
    }

    /**
     * Fonction qui ajoute l'étape sélectionnée dans la liste
     * @param etape Etape qu'on veut ajouter
     */
    public void ajouterEtapeSelectionner(EtapeIG etape){
        etapesSelecteds.add(etape);
    }

    /**
     * Fonction qui supprime une étape sélectionnée dans la liste
     * @param etape Etape à supprimer
     */
    public void enleverEtapeSelectionner(EtapeIG etape){
        etapesSelecteds.remove(etape);
    }

    /**
     * Fonction qui ajoute l'arc sélectionné dans la liste
     * @param arcIG Arc qu'on veut ajouter
     */
    public void ajouterArcSelectionner(ArcIG arcIG){
        arcsSelecteds.add(arcIG);
    }

    /**
     * Fonction qui supprime un arc sélectionné dans la liste
     * @param arcIG Arc à supprimer
     */
    public void enleverArcSelectionner(ArcIG arcIG){
        arcsSelecteds.remove(arcIG);
    }

    /**
     * Getter de la liste d'étapes sélectionné
     * @return L'attribut etapesSelecteds
     */
    public LinkedList<EtapeIG> getEtapesSelecteds() {
        return etapesSelecteds;
    }

    /**
     * Fonction qui supprime tous les élements sélectionnés
     */
    public void supprimer(){
        for(EtapeIG e : etapesSelecteds){
            for(ArcIG arc : arcs){
                if(arc.getDepart().getEtape().equals(e)){
                    arc.getArrive().getEtape().retirerPredecesseur(e);
                }
                if(arc.getArrive().getEtape().equals(e)){
                    arc.getDepart().getEtape().retirerSuccesseur(e);
                }
            }
            arcs.removeIf(arc -> arc.getDepart().getEtape().equals(e) || arc.getArrive().getEtape().equals(e));
            if(this.entrees.contains(e)){
                entrees.remove(e);
            }
            if(this.sorties.contains(e)){
                sorties.remove(e);
            }
            etapes.remove(e.getIdentifiant());
        }
        for(ArcIG a : arcsSelecteds){
            a.getDepart().getEtape().retirerSuccesseur(a.getArrive().getEtape());
            a.getArrive().getEtape().retirerPredecesseur(a.getDepart().getEtape());
            arcs.remove(a);
        }
        etapesSelecteds.clear();
        arcsSelecteds.clear();
        this.notifierObservateurs();
    }

    /**
     * Setter du nom d'une étape
     * @param etape Etape dont le nom va être changé
     * @param nom Nouveau nom de l'étape
     */
    public void setNomEtape(EtapeIG etape, String nom){
        etape.setNom(nom);
        etape.setSelected();
        this.notifierObservateurs();
    }

    /**
     * Fonction qui permet de reinitialiser la sélection des étapes et des arcs
     */
    public void effacerSelection(){
        for(EtapeIG etape : etapesSelecteds){
            etape.setSelected();
        }
        for(ArcIG arc : arcsSelecteds){
            arc.setSelected();
        }
        etapesSelecteds.clear();
        arcsSelecteds.clear();
        this.notifierObservateurs();
    }

    /**
     * @param entree Ajouter une EtapeIG en temps d'entrée
     */
    public void ajouterEntree(EtapeIG entree) {
        this.entrees.add(entree);
        entree.setSelected();
        this.notifierObservateurs();
    }

    /**
     * @param entree Enlève l'EtapeIG de la LinkedList des entrées
     */
    public void enleverEntree(EtapeIG entree) {
        this.entrees.remove(entree);
        entree.setSelected();
        this.notifierObservateurs();
    }

    /**
     *
     * @param sortie Ajoute l'EtapeIG à la LinkedList des sorties
     */
    public void ajouterSortie(EtapeIG sortie) {
        this.sorties.add(sortie);
        sortie.setSelected();
        this.notifierObservateurs();
    }

    /**
     * @param sortie Enleve l'EtapeIG à la LL des sorties
     */
    public void enleverSortie(EtapeIG sortie) {
        this.sorties.remove(sortie);
        sortie.setSelected();
        this.notifierObservateurs();
    }

    /**
     * Vider la LL des EtapesSelectionnes
     */
    public void netoyageEtapeSelectionnee(){
        this.getEtapesSelecteds().clear();
        this.notifierObservateurs();
    }

    /**
     * Verifie sur la LinkedList entrees contient l'etape
     * @param entree une etaptIG à tester
     * @return boolean
     */
    public boolean estEntree(EtapeIG entree) {
        return this.entrees.contains(entree);
    }

    /**
     * Verifie sur la LinkedList sorties contient l'etape
     * @param sortie une etapeIG à tester
     * @return boolean
     */
    public boolean estSortie(EtapeIG sortie) {
        return this.sorties.contains(sortie);
    }

    /**
     * Verification dans SimulationIG pour savoir s'il y a bien des entrees.
     * @throws MondeException Classe d'exception de MondeIG
     */
    public void estVideEntree() throws MondeException{
        if(this.entrees.isEmpty()){
            throw new MondeException("Il y a aucune entree");
        }
    }

    /**
     * Empeche qu'une entrée n'ai pas de Predecesseur
     * @throws MondeException L'erreur
     */
    public void entreeSansPredecesseurs() throws MondeException{
        for(EtapeIG etape : this){
            if(entrees.contains(etape) && etape.getNbPredecesseurs()>0){
                throw new MondeException("L'entrée ne doit pas avoir de prédecesseurs");
            }
        }
    }

    /**
     * Verification dans SimulationIG pour savoir s'il y a bien des sorties.
     * @throws MondeException Classe d'exception de MondeIG
     */
    public void estVideSortie() throws MondeException{
        if(this.sorties.isEmpty()){
            throw new MondeException("Il y a aucune sortie");
        }
    }



    /**
     *
     * @throws MondeException Classe d'exception de MondeIG
     */
    public void aSuccesseurs() throws MondeException{
        for(EtapeIG etape : this){
            if(etape.getNbSuccesseurs()==0 && !this.sorties.contains(etape)){
                throw new MondeException("Il y a une etape qui n'as pas de successeurs");
            }
        }
    }

    /**
     *
     * @throws MondeException Classe d'exception de MondeIG
     */
    public void aPredecesseurs() throws MondeException{
        for(EtapeIG etape : this){
            if(etape.getNbPredecesseurs()==0 && !this.entrees.contains(etape)){
                throw new MondeException("Il y a une etape qui n'as pas de predecesseurs");
            }
        }
    }

    /**
     *
     * @throws MondeException Classe d'exception de MondeIG
     */
    public void guichetsCorrect() throws MondeException{
        for(EtapeIG etape : this){
            if(etape.estUnGuichet()){
                if(etape.getNbSuccesseurs()!=1) {
                    throw new MondeException("Le guichet est censé avoir qu'un seul successeur");
                } else if(!etape.getSuccesseur(0).estUneActivite()){
                    throw new MondeException("Le successeur d'un guichet doit être une activite");
                } else if(this.sorties.contains(etape)){
                    throw new MondeException("Un guichet n'est pas une sortie");
                } else if(this.entrees.contains(etape.getSuccesseur(0))){
                    throw new MondeException("Une activité restreinte n'est pas une entrée");
                }
            }
        }
    }

    /**
     * Defini comme ActiviteRestreinte les Etapes qui ont un Guichet avant eux
     */
    public void definirActivitesRestreinte() {
        for(EtapeIG etape : this){
            if(etape.estUnGuichet()){
                etape.getSuccesseur(0).setEstActiviteRestreinte();
            }
        }
    }

    /**
     * Ajouter un ClientIG à la LL de ClientIG
     * @param c Un ClientIG
     */
    public void ajouterClient(ClientIG c){

        if (c == null) {
            Alertes.afficherErreur("Le client ne peut pas être null.");
            throw new IllegalArgumentException("client ne peut pas être null");
        }
        this.clients.add(c);
    }

    /**
     * Vider le contenu de la LL clients
     */
    public void viderClients(){
        this.clients.clear();
    }

    /**
     * @return Getter des ClientsIG
     */
    public LinkedList<ClientIG> getClientsIG() {
        return clients;
    }

    /**
     * @return Renvoie la Taille de la LL des clients
     */
    public int getNbClientsIG(){
        return clients.size();
    }
}
