package twisk.outils;

public class FabriqueNumero {

    private static final FabriqueNumero instance = new FabriqueNumero();
    private int cptEtape;
    private int cptSemaphore;
    private int noPointDeControle;

    /**
     * Constructeur de FabriqueNumero
     */
    private FabriqueNumero() {
        this.cptEtape = 0;
        this.cptSemaphore = 1;
        this.noPointDeControle = 0;
    }

    /**
     *
     * @return l'instance de FabriqueNumero
     */
    public static FabriqueNumero getInstance() {
        return instance;
    }

    /**
     *
     * @return getter cptEtape
     */
    public int getNumeroEtape() {
        this.cptEtape++;
        return this.cptEtape -1 ;
    }

    /**
     * Reset cptEtape à 0 et cptSemaphore à 1
     */
    public void reset() {
        this.cptEtape = 0;
        this.cptSemaphore = 1;
        this.noPointDeControle = 0;
    }

    /**
     *
     * @return getter pour Semaphore
     */
    public int getNumeroSemaphore() {
        this.cptSemaphore++;
        return this.cptSemaphore -1;
    }

    /**
     * @return renvoie l'Identifiant du PointDeControle
     */
    public String getIdentifiantPointDeControle() {
        noPointDeControle++;
        return Integer.toString(noPointDeControle-1);
    }
}