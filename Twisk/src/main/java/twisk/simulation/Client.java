package twisk.simulation;

import twisk.monde.Etape;

public class Client {

    private int numeroClient;
    private int rang;
    private Etape etape;

    /**
     * Constructeur de Client
     * @param numero le Numero du client
     */
    public Client (int numero){
        this.numeroClient = numero;
    }

    /**
     * Permet de deplacer un Client à une Etape et un rang
     * @param etape l'étape souhaité
     * @param rang Le rang souhaité
     */
    public void allerA(Etape etape, int rang){
        this.etape = etape;
        this.rang = rang;
    }

    /**
     * Getter d'étape
     * @return Une Etape
     */
    public Etape getEtape() {
        return etape;
    }
}
