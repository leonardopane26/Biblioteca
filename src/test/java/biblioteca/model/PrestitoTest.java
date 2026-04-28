/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
public class PrestitoTest {
    
    private Prestito prestito;
    private Utente utente;
    private Libro libro;
    private LocalDate oggi;
    
    public PrestitoTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Starting PrestitoTest...");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Finished PrestitoTest...");
    }
    
    @BeforeEach
    public void setUp() {
        oggi = LocalDate.now();
        libro = new Libro("Libro", new ArrayList<>(Arrays.asList("autore")), 2026, "0102030405", 5);
        utente = new Utente("Mario", "Rossi", "12345", "mario@test.it");
        
        prestito = new Prestito(libro, utente, oggi, oggi.plusDays(30));
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getLibro method, of class Prestito.
     */
    @Test
    public void testGetLibro() {
        System.out.println("getLibro");
        assertEquals(libro , prestito.getLibro());
    }

    /**
     * Test of getUtente method, of class Prestito.
     */
    @Test
    public void testGetUtente() {
        System.out.println("getUtente");
        assertEquals(utente , prestito.getUtente());
    }

    /**
     * Test of getDataInizio method, of class Prestito.
     */
    @Test
    public void testGetDataInizio() {
        System.out.println("getDataInizio");
        assertEquals(oggi , prestito.getDataInizio());
    }

    /**
     * Test of getDataFine method, of class Prestito.
     */
    @Test
    public void testGetDataFine() {
        System.out.println("getDataFine");
        assertEquals(oggi.plusDays(30) , prestito.getDataFine());
    }

    /**
     * Test of setDataFine method, of class Prestito.
     */
    @Test
    public void testSetDataFine() {
        System.out.println("setDataFine");
        LocalDate dataFineTest = LocalDate.now().plusMonths(2);
        prestito.setDataFine(dataFineTest);
        assertEquals(dataFineTest , prestito.getDataFine());
    }

    /**
     * Test of getDataRestituzioneEffettiva method, of class Prestito.
     */
    @Test
    public void testGetDataRestituzioneEffettiva() {
        System.out.println("getDataRestituzioneEffettiva");
        assertNull(prestito.getDataRestituzioneEffettiva());
    }

    /**
     * Test of setDataRestituzioneEffettiva method, of class Prestito.
     */
    @Test
    public void testSetDataRestituzioneEffettiva() {
        System.out.println("setDataRestituzioneEffettiva");
        LocalDate dataResEffettiva = LocalDate.now().plusWeeks(3);
        prestito.setDataRestituzioneEffettiva(dataResEffettiva);
        assertEquals(dataResEffettiva , prestito.getDataRestituzioneEffettiva());
    }

    /**
     * Test of isInRitardo method, of class Prestito.
     */
    @Test
    public void testIsInRitardo() {
        System.out.println("isInRitardo");
        assertFalse(prestito.isInRitardo());
        
        prestito.setDataFine(oggi);
        assertFalse(prestito.isInRitardo());
        
        prestito.setDataFine(oggi.minusDays(1));
        assertTrue(prestito.isInRitardo());
    }

    /**
     * Test of isRestituito method, of class Prestito.
     */
    @Test
    public void testIsRestituito() {
        System.out.println("isRestituito");
        assertFalse(prestito.isRestituito());
        
        prestito.setDataRestituzioneEffettiva(oggi);
        assertTrue(prestito.isRestituito());
    }

    /**
     * Test of getDettagli method, of class Prestito.
     */
    @Test
    public void testGetDettagli() {
        System.out.println("getDettagli");
        String dettagli = prestito.getDettagli();
        
        assertTrue(dettagli.contains("Libro"));
        assertTrue(dettagli.contains("Mario Rossi"));
        assertTrue(dettagli.contains("12345"));
        assertTrue(dettagli.contains(prestito.getDataFine().toString()));
    }
    
}
