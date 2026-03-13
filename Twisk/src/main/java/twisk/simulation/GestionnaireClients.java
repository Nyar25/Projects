package twisk.simulation;

import twisk.monde.Etape;
import java.util.HashMap;
import java.util.Iterator;

public class GestionnaireClients implements Iterable<Client> {

    private HashMap<Integer, Client> clientHashmap;

    /**
     * Constructeur du GesitonnaireClients qui crée une Hashmap
     */
    public GestionnaireClients() {

        clientHashmap = new HashMap<>();

    }


    /**
     * Remplit la hashmap avec un Int pour identifier les clients
     * @param tabClients nombre de clients
     */
    public void setClients(int... tabClients) {
        for (int nb : tabClients) {
            Client client = new Client(nb);
            clientHashmap.put(nb, client);
        }
    }

    /**
     * Si le client est présent, setter pour l'étape et le rang
     * @param numeroClient Numéro du client
     * @param etape Setter pour l'étape
     * @param rang Setter pour le rang
     */
    public void allerA(int numeroClient, Etape etape, int rang){

        if (clientHashmap.containsKey(numeroClient)) {

            Client client = clientHashmap.get(numeroClient);
            client.allerA(etape, rang);

            System.out.println("Client " + numeroClient + " déplacé à l'étape " + etape.getNom() + " avec le rang " + rang);
        }
    }

    /**
     * Vide la Hashmap
     */
    public void nettoyer(){
        clientHashmap.clear();
    }

    /**
     *
     * @return Hashmap iterable
     */
    public Iterator<Client> iterator(){
        return clientHashmap.values().iterator();
    }
}
