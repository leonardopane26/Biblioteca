/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author lpane
 */
public class UtenteTest {
    
    private Utente utente;
    
    public UtenteTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Starting UtenteTest...");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Finished UtenteTest...");
    }
    
    @BeforeEach
    public void setUp() {
        utente = new Utente("Leonardo" , "Pane" , "0612707333" , "leonardo@gmail.com");
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getNome method, of class Utente.
     */
    @Test
    public void testGetNome() {
        System.out.println("getNome");
        assertEquals("Leonardo" , utente.getNome());
    }

    /**
     * Test of setNome method, of class Utente.
     */
    @Test
    public void testSetNome() {
        System.out.println("setNome");
        utente.setNome("nomeUtente");
        assertEquals("nomeUtente" , utente.getNome());
    }

    /**
     * Test of getCognome method, of class Utente.
     */
    @Test
    public void testGetCognome() {
        System.out.println("getCognome");
        assertEquals("Pane" , utente.getCognome());
    }

    /**
     * Test of setCognome method, of class Utente.
     */
    @Test
    public void testSetCognome() {
        System.out.println("setCognome");
        utente.setCognome("cognomeUtente");
        assertEquals("cognomeUtente" , utente.getCognome());
    }

    /**
     * Test of getMatricola method, of class Utente.
     */
    @Test
    public void testGetMatricola() {
        System.out.println("getMatricola");
        assertEquals("0612707333" , utente.getMatricola());
    }

    /**
     * Test of setMatricola method, of class Utente.
     */
    @Test
    public void testSetMatricola() {
        System.out.println("setMatricola");
        utente.setMatricola("123456");
        assertEquals("123456" , utente.getMatricola());
    }

    /**
     * Test of getEmail method, of class Utente.
     */
    @Test
    public void testGetEmail() {
        System.out.println("getEmail");
        assertEquals("leonardo@gmail.com" , utente.getEmail());
    }

    /**
     * Test of setEmail method, of class Utente.
     */
    @Test
    public void testSetEmail() {
        System.out.println("setEmail");
        utente.setEmail("email@gmail.com");
        assertEquals("email@gmail.com" , utente.getEmail());
    }

    /**
     * Test of toString method, of class Utente.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String atteso = "Pane Leonardo (Matricola: 0612707333)";
        assertEquals(atteso , utente.toString());
    }

    /**
     * Test of equals method, of class Utente.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        Utente utenteUguale = new Utente("Paolo" , "Rossi" , "0612707333" , "paolo@gmail.com");
        Utente utenteDiverso = new Utente("Paolo" , "Rossi" , "000000001" , "paolo@gmail.com");
        
        assertTrue(utente.equals(utenteUguale));
        assertFalse(utente.equals(utenteDiverso));
    }
    
}
