package twisk.outils;

import twisk.monde.Etape;
import twisk.monde.Monde;
import twisk.simulation.GestionnaireClients;

import java.io.*;
import java.nio.file.*;
import java.text.Normalizer;

public class KitC {
    private Monde monde;

    /**
     * Constructeur de KitC
     * @param monde Le monde
     */
    public KitC(Monde monde){
        this.monde = monde;
    }

    /**
     * Creer les fichiers C dans le répertoire /tmp/twisk/ depuis ressources/CodeC/
     */
    public void creerEnvironnement(){
        // Crée un path vers tmp/twisk
        Path directory = Paths.get("/tmp/twisk");
        try {
            // Création du répertoire twisk sous /tmp.
            // Ne déclenche pas d’erreur si le répertoire existe déjà
            Files.createDirectories(directory);
            // Copie des fichiers programmeC.o et def.h sous /tmp/twisk
            String[] liste = {"programmeC.o", "def.h", "codeNatif.o"};
            for (String nom : liste) {
                InputStream src = getClass().getResourceAsStream("/codeC/" + nom);
                Path dest = directory.resolve(nom);
                Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * Créer un fichier client.c qui va contenir le code donné en paramètre
     * @param CodeC Le code qui va être contenu dans client.c
     */
    public void creerFichier(String CodeC){
        // Destination
        Path destination = Paths.get("/tmp/twisk/");

        try {
            // Création si nécessaire
            Files.createDirectories(destination);

            // Définition du chemin complet du fichier destination
            Path fichierDestination = destination.resolve("client.c");

            // Verification
            if(Files.notExists(fichierDestination)){
                Files.createFile(fichierDestination);
            }

            // Écriture du CodeC dans le fichierDestination
            // TRUNCATE_EXISTING = on efface le contenu du fichier avant de faire l'écriture, pour éviter d'avoir des trucs chelous dans client.c
            Files.writeString(fichierDestination, CodeC, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Construit la bibliotheque dans tmp/twisk
     */
    public void construireLaBibliotheque(){
        ProcessBuilder pbuilder = new ProcessBuilder("gcc","-shared","/tmp/twisk/programmeC.o","/tmp/twisk/codeNatif.o","/tmp/twisk/client.o","-o","/tmp/twisk/libTwisk.so");

        try {
            pbuilder.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Permet de lancer la compilation : gcc -Wall -ansi -pedantic -fPIC -c /tmp/twisk/client.c -o - /tmp/twisk/client.o
     */
    public void compiler(){
        // Commande de compilation donnée par le TP5.5
        ProcessBuilder pb = new ProcessBuilder("gcc","-Wall","-ansi","-pedantic","-fPIC","-c","/tmp/twisk/client.c","-o","/tmp/twisk/client.o");

        try {
            pb.inheritIO().start().waitFor();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }


    /**
     *
     * @return Permet de créer les defines pour l'éxécution du Code C
     */
    public String faireDefine(){
        StringBuilder sb = new StringBuilder();
        int ind = 0;
        for(Etape etape : monde){
            String nom = Normalizer.normalize(etape.getNom(), Normalizer.Form.NFD);
            nom = nom.replaceAll("\\p{M}", "");
            nom = nom.replace(" ", "_");
            nom = nom.replace("±", "");
            sb.append("#define ").append(nom).append(" ").append(ind).append("\n");
            ind++;
        }
        sb.append("#define NBETAPES " + monde.nbEtapes() + "\n" );
        sb.append("#define NBGUICHETS " + monde.nbGuichets() + "\n" );
        for(int i=1;i<=monde.nbGuichets();i++){
            sb.append("#define SEMAPHORE_GUICHET").append(i).append(" ").append(i).append("\n");
        }
        sb.append("\nint* getSemaphoresGuichets(){\n");
        sb.append("int* tab = malloc(sizeof(int)*NBGUICHETS);\n");
        if(monde.nbGuichets() == 0){
            sb.append("NULL;\n\n");
        }
        for(int i=0;i<monde.nbGuichets();i++){
            sb.append("tab[").append(i).append("] = SEMAPHORE_GUICHET").append(i+1).append(";\n");
        }
        sb.append("return tab;\n");
        sb.append("}\n");
        return sb.toString();
    }
}