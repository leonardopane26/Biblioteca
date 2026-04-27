/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class LibroTest {
    
    private Libro libro;
    private List<String> autori;
    
    public LibroTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Strating LibroTest class...");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Finished LibroTest...");
    }
    
    @BeforeEach
    public void setUp() {
        autori = new ArrayList<>(Arrays.asList( "Primo Autore" , "Secondo Autore"));
        libro = new Libro("Nuovo libro" , autori , 2016 , "1234567" , 10);
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getTitolo method, of class Libro.
     */
    @Test
    public void testGetTitolo() {
       assertEquals("Nuovo libro" , libro.getTitolo());
    }

    /**
     * Test of getAutori method, of class Libro.
     */
    @Test
    public void testGetAutori() {
        System.out.println("getAutori");
        assertEquals(autori , libro.getAutori());
    }

    /**
     * Test of getAnnoPubblicazione method, of class Libro.
     */
    @Test
    public void testGetAnnoPubblicazione() {
        System.out.println("getAnnoPubblicazione");
        assertEquals(2016, libro.getAnnoPubblicazione());
    }

    /**
     * Test of getIsbn method, of class Libro.
     */
    @Test
    public void testGetIsbn() {
        System.out.println("getIsbn");
        assertEquals("1234567" , libro.getIsbn());
    }

    /**
     * Test of getNumeroCopieTotali method, of class Libro.
     */
    @Test
    public void testGetNumeroCopieTotali() {
        System.out.println("getNumeroCopieTotali");
        assertEquals(10 , libro.getNumeroCopieTotali());
    }

    /**
     * Test of incrementaCopie method, of class Libro.
     */
    @Test
    public void testIncrementaCopie() {
        System.out.println("incrementaCopie");
        
        libro.incrementaCopie();
        assertEquals(11 , libro.getNumeroCopieTotali());
    }

    /**
     * Test of decrementaCopie method, of class Libro.
     */
    @Test
    public void testDecrementaCopie() {
        System.out.println("decrementaCopie");
        
        libro.decrementaCopie();
        assertEquals(9 , libro.getNumeroCopieTotali());
        
        libro.setNumeroCopieTotali(0);
        libro.decrementaCopie();
        assertEquals(0 , libro.getNumeroCopieTotali());
    }

    /**
     * Test of getAutoriFormattati method, of class Libro.
     */
    @Test
    public void testGetAutoriFormattati() {
        System.out.println("getAutoriFormattati");
        
        assertEquals("Primo Autore, Secondo Autore" , libro.getAutoriFormattati());
        
        libro.setAutori(Arrays.asList("Unico Autore"));
        assertEquals("Unico Autore" , libro.getAutoriFormattati());
        
        libro.setAutori(new ArrayList<>());
        assertEquals("Autore sconosciuto" , libro.getAutoriFormattati());
        
        libro.setAutori(null);
        assertEquals("Autore sconosciuto" , libro.getAutoriFormattati());
    }

    /**
     * Test of equals method, of class Libro.
     */
    @Test
    public void testEqualsEHashCode() {
        System.out.println("equals e hashcode");
        
        Libro libroUguale = new Libro("Altro titolo" , null , 2024 , "1234567" , 5);
        Libro libroDiverso = new Libro("Diverso" , null , 2024 , "000000" , 10);
        
        assertTrue(libro.equals(libroUguale));
        assertFalse(libro.equals(libroDiverso));
        assertEquals(libro.hashCode() , libroUguale.hashCode());
    }

    /**
     * Test of toString method, of class Libro.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        String atteso = "Nuovo libro (2016) - ISBN: 1234567";
        assertEquals(atteso, libro.toString());
    }
    
}
