import org.junit.jupiter.api.Test;
import twisk.monde.Monde;
import twisk.outils.KitC;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class KitCTest {

    @Test
    public void creerFichierTest() {
        KitC kit = new KitC(new Monde());

        String codeC = "#include <stdio.h> \n" +
                "#include <stdlib.h> \n" +
                "\n" +
                "int main(){ \n" +
                "int tmp = 1; \n" +
                "return 0; \n } \n";

        // Appel de la méthode creerFichier
        kit.creerFichier(codeC);

        // Verification
        Path fichierDestination = Paths.get("/tmp/twisk/client.c");
        assertTrue(Files.exists(fichierDestination),"Erreur fichier non crée dans KitC creerFichier");

        if (Files.exists(fichierDestination)) {
            try {
                // Utilisation d'un string
                String contenu = Files.readString(fichierDestination);
                assertTrue(contenu.contains(codeC),"Le code n'a pas été correctement écrit dans client.c");

            } catch (IOException e) {
                System.err.println("Erreur pour le fichier : " + e.getMessage());
            }
        }
    }
}