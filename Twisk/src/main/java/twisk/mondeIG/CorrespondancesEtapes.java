package twisk.mondeIG;

import twisk.monde.Etape;

import java.util.HashMap;

public class CorrespondancesEtapes {

    private HashMap<EtapeIG, Etape> correspondances;

    /**
     * Constructeur qui crée une hashmap
     */
    public CorrespondancesEtapes(){
        this.correspondances = new HashMap<>();
    }

    /**
     * Ajoute à la hashmap un etapeIG en key et une etape en value
     * @param etapeIG L'etapeIG
     * @param etape L'etape
     */
    public void ajouter(EtapeIG etapeIG, Etape etape){
        correspondances.put(etapeIG,etape);
    }

    /**
     * Getter de la hahsmap pour obtenir la key
     * @param etapeIG L'etapeIG dans la hashmap
     * @return la Value de l'étapeIG associé via la hashmap
     */
    public Etape get(EtapeIG etapeIG){
        return correspondances.get(etapeIG);
    }
}
