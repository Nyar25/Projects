package twisk.outils;

import javafx.concurrent.Task;

import java.util.LinkedList;

public class ThreadsManager {
    private static ThreadsManager instance;
    private LinkedList<Thread> threads;

    /**
     * Constructeur du ThreadsManager
     */
    private ThreadsManager() {
        this.threads = new LinkedList<>();
    }

    /**
     *
     * @return Renvoie une instance du ThreadsManager
     */
    public static ThreadsManager getInstance() {
        if(instance == null) {
            instance = new ThreadsManager();
        }
        return instance;
    }

    /**
     * Crée un thread, l'ajoute à la LL et le lance
     * @param task Un thread
     */
    public void lancer(Task<Void> task) {
        Thread thread = new Thread(task);
        threads.add(thread);
        thread.start();
    }

    /**
     * Detruit tous les threads
     */
    public void detruireTout() {
        for(Thread thread : threads){
            thread.interrupt();
        }
        threads.clear();
    }
}
