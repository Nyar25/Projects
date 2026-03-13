import org.junit.jupiter.api.Test;
import twisk.monde.*;
import twisk.simulation.Simulation;

import static org.junit.jupiter.api.Assertions.*;

public class SasSortieTest extends ActiviteTest{
    @Test
    void testToString(){
        SasSortie sas = new SasSortie();
        assertEquals("SASSORTIE : 0 successeur - ",sas.toString(),"Fonction toString mal faite");
    }

    @Test
    void testToCSasSortie(){
        SasSortie sas = new SasSortie();
        assertEquals("",sas.toC(),"Fonction toC mal faite");
    }
}