/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDate;
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
public class ArchivioTest {
    
    private Archivio archivio;
    
    public ArchivioTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
        archivio = Archivio.getInstance();
        archivio.getListaLibri().clear();
        archivio.getListaUtenti().clear();
        archivio.getListaPrestiti().clear();
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class Archivio.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        
        Archivio istanza1 = Archivio.getInstance();
        Archivio istanza2 = Archivio.getInstance();

        assertNotNull(istanza1);
        assertSame(istanza1 , istanza2);
    }

    /**
     * Test of caricaLibri method, of class Archivio.
     */
    @Test
    public void testCaricaLibri() throws Exception {
        System.out.println("caricaLibri");
        
            try (PrintWriter pw = new PrintWriter(new FileWriter("libri.csv"))) {
            pw.println("Libro Test,Autore A;Autore B,12345,2026,10,5");
        }

        archivio.caricaLibri();

        List<Libro> libri = archivio.getListaLibri();
        assertEquals(1, libri.size());

        Libro l = libri.get(0);
        assertEquals("Libro Test", l.getTitolo());
        assertEquals("12345", l.getIsbn());
        assertEquals(5, l.getNumeroCopieDisponibili());
        assertEquals(2, l.getAutori().size());
    }

    /**
     * Test of caricaUtenti method, of class Archivio.
     */
    @Test
    public void testCaricaUtenti() throws Exception {
        System.out.println("caricaUtenti");
        
        try (PrintWriter pw = new PrintWriter(new FileWriter("utenti.csv"))) {
            pw.println("Leonardo,Pane,0612707333,leonardo@gmail.com");
        }

        archivio.caricaUtenti();

        List<Utente> utenti = archivio.getListaUtenti();
        assertEquals(1, utenti.size());
        assertEquals("Leonardo", utenti.get(0).getNome());
        assertEquals("0612707333", utenti.get(0).getMatricola());
    }

    /**
     * Test of caricaPrestiti method, of class Archivio.
     */
    @Test
    public void testCaricaPrestiti() throws Exception {
        System.out.println("caricaPrestiti");

        Libro l = new Libro("Test", new ArrayList<>(), 2026, "01", 1);
        Utente u = new Utente("User", "Test", "0001", "test@test.com");
        archivio.getListaLibri().add(l);
        archivio.getListaUtenti().add(u);

        try (PrintWriter pw = new PrintWriter(new FileWriter("prestiti.csv"))) {
            pw.println("01,0001,2026-04-01,2026-04-15");
        }

        archivio.caricaPrestiti();
        List<Prestito> prestiti = archivio.getListaPrestiti();
        assertEquals(1, prestiti.size());
        assertEquals("01", prestiti.get(0).getLibro().getIsbn());
        assertEquals("0001", prestiti.get(0).getUtente().getMatricola());
        assertEquals(LocalDate.of(2026, 4, 1), prestiti.get(0).getDataInizio());
    }

    /**
     * Test of salvaArchivio method, of class Archivio.
     */
    @Test
    public void testSalvaArchivio() throws Exception {
        System.out.println("salvaArchivio");

        Libro l = new Libro("Titolo Test", new ArrayList<>(Arrays.asList("Autore Test")), 2026, "0001", 5);
        archivio.getListaLibri().add(l);

        Utente u = new Utente("Paolo", "Rossi", "11111", "paolo@test.it");
        archivio.getListaUtenti().add(u);

        Prestito p = new Prestito(l, u, LocalDate.now(), LocalDate.now().plusDays(15));
        archivio.getListaPrestiti().add(p);

        archivio.salvaArchivio();

        File fileLibri = new File("libri.csv");
        assertTrue(fileLibri.exists());

        try (BufferedReader br = new BufferedReader(new FileReader(fileLibri))) {
            String riga = br.readLine();
            assertNotNull(riga);
            assertTrue(riga.contains("0001"));
            assertTrue(riga.contains("5"));
        }

        File fileUtenti = new File("utenti.csv");
        assertTrue(fileUtenti.exists());

        try (BufferedReader br = new BufferedReader(new FileReader(fileUtenti))) {
            String riga = br.readLine();
            assertNotNull(riga);
            assertTrue(riga.contains("11111"));
        }

        File filePrestiti = new File("prestiti.csv");
        assertTrue(filePrestiti.exists());

        try (BufferedReader br = new BufferedReader(new FileReader(filePrestiti))) {
            String riga = br.readLine();
            assertNotNull(riga);
            assertTrue(riga.contains("0001"));
            assertTrue(riga.contains("11111"));
        }
    }

    /**
     * Test of cercaLibroPerISBN method, of class Archivio.
     */
    @Test
    public void testCercaLibroPerISBN() {
        System.out.println("cercaLibroPerISBN");
        
        Libro l = new Libro("Libro Ricerca" , new ArrayList<>() , 2026 , "100" , 1);
        archivio.getListaLibri().add(l);
        
        Libro trovato = archivio.cercaLibroPerISBN("100");
        assertNotNull(trovato);
        assertEquals("Libro Ricerca", trovato.getTitolo());
        
        Libro nonTrovato = archivio.cercaLibroPerISBN("000");
        assertNull(nonTrovato);
    }

    /**
     * Test of cercaUtentePerMatricola method, of class Archivio.
     */
    @Test
    public void testCercaUtentePerMatricola() {
        System.out.println("cercaUtentePerMatricola");
        
        Utente u = new Utente("Utente" , "Ricerca" , "03" , "utente@gmail.com");
        archivio.getListaUtenti().add(u);
        
        Utente trovato = archivio.cercaUtentePerMatricola("03");
        assertNotNull(trovato);
        assertEquals("Utente" , trovato.getNome());
        
        Utente nonTrovato = archivio.cercaUtentePerMatricola("1111");
        assertNull(nonTrovato);
    }

    /**
     * Test of filtraPrestitiPerUtente method, of class Archivio.
     */
    @Test
    public void testFiltraPrestitiPerUtente() {
        System.out.println("filtraPrestitiPerUtente");
       
        Utente u1 = new Utente("Leonardo" , "Pane" , "0612707333" , "leonardo@gmail.com");
        Utente u2 = new Utente("Matteo" , "Pane" , "00000001" , "matteo@gmail.com");
        Libro l1 = new Libro("Primo Libro" , new ArrayList<>() , 2026 , "01" , 1);
        Libro l2 = new Libro("Secondo Libro" , new ArrayList<>() , 2026 , "02" , 1);
        
        Prestito attivo = new Prestito(l1 , u1 , LocalDate.now() , LocalDate.now().plusDays(10));
        Prestito restituito = new Prestito(l2 , u1 , LocalDate.now().minusDays(20) , LocalDate.now());
        restituito.setDataRestituzioneEffettiva(LocalDate.now().minusDays(5));
        
        Prestito altroUtente = new Prestito(l1 , u2 , LocalDate.now() , LocalDate.now().plusDays(15));
        
        archivio.getListaPrestiti().add(attivo);
        archivio.getListaPrestiti().add(restituito);
        archivio.getListaPrestiti().add(altroUtente);
        
        List<Prestito> risultato = archivio.filtraPrestitiPerUtente(u1);
        
        assertEquals(1 , risultato.size());
        assertTrue(risultato.contains(attivo));
        assertFalse(risultato.contains(restituito));
        assertFalse(risultato.contains(altroUtente));
    }

    /**
     * Test of getListaLibri method, of class Archivio.
     */
    @Test
    public void testGetListaLibri() {
        System.out.println("getListaLibri");
        assertNotNull(archivio.getListaLibri());
        
        Libro l = new Libro("Test" , new ArrayList<>() , 2026 , "1" , 1);
        archivio.aggiungiLibro(l);
        
        assertTrue(archivio.getListaLibri().contains(l));
    }

    /**
     * Test of getListaUtenti method, of class Archivio.
     */
    @Test
    public void testGetListaUtenti() {
        System.out.println("getListaUtenti");
        assertNotNull(archivio.getListaUtenti());
        
        Utente u = new Utente("Nome" , "Cognome" , "1" , "utente@gmail.com");
        archivio.aggiungiUtente(u);
        
        assertTrue(archivio.getListaUtenti().contains(u));
    }

    /**
     * Test of getListaPrestiti method, of class Archivio.
     */
    @Test
    public void testGetListaPrestiti() {
        System.out.println("getListaPrestiti");
        assertNotNull(archivio.getListaPrestiti());
        
        Libro l = new Libro("Test" , new ArrayList<>() , 2000 , "001" , 1);
        Utente u = new Utente("Nome" , "Cognome" , "01" , "utente@gmail.com");
        Prestito p = new Prestito(l , u , LocalDate.now() , LocalDate.now().plusDays(20));
        
        archivio.aggiungiPrestito(p);
        assertTrue(archivio.getListaPrestiti().contains(p));
    }

    /**
     * Test of aggiungiLibro method, of class Archivio.
     */
    @Test
    public void testAggiungiLibro() {
        System.out.println("aggiungiLibro");
        
        Libro l = new Libro("Titolo" , new ArrayList<>() , 2003 , "001" , 1);
        archivio.aggiungiLibro(l);
        
        assertEquals(1 , archivio.getListaLibri().size());
        assertTrue(archivio.getListaLibri().contains(l));
    }

    /**
     * Test of aggiungiUtente method, of class Archivio.
     */
    @Test
    public void testAggiungiUtente() {
        System.out.println("aggiungiUtente");
        
        Utente u = new Utente("Leonardo" , "Pane" , "0612707333" , "leonardo@gmail.com");
        archivio.aggiungiUtente(u);
        
        assertEquals(1 , archivio.getListaUtenti().size());
        assertTrue(archivio.getListaUtenti().contains(u));
    }

    /**
     * Test of aggiungiPrestito method, of class Archivio.
     */
    @Test
    public void testAggiungiPrestito() {
        System.out.println("aggiungiPrestito");
        
        Libro l = new Libro("Libro" , new ArrayList<>() , 2026 , "01" , 1);
        Utente u = new Utente("Utente" , "Test" , "001" , "utente@test.com");
        Prestito p = new Prestito(l , u , LocalDate.now() , LocalDate.now().plusDays(10));
        
        archivio.aggiungiPrestito(p);
        
        assertEquals(1 , archivio.getListaPrestiti().size());
        assertTrue(archivio.getListaPrestiti().contains(p));
    }

    /**
     * Test of rimuoviPrestito method, of class Archivio.
     */
    @Test
    public void testRimuoviPrestito() {
        System.out.println("rimuoviPrestito");
        
        Libro l = new Libro("Libro" , new ArrayList<>() , 2026 , "01" , 1);
        Utente u = new Utente("Utente" , "Test" , "001" , "utente@test.com");
        Prestito p = new Prestito(l , u , LocalDate.now() , LocalDate.now().plusDays(10));
        
        archivio.aggiungiPrestito(p);
        
        assertEquals(1 , archivio.getListaPrestiti().size());
        
        archivio.rimuoviPrestito(p);
        
        assertEquals(0 , archivio.getListaPrestiti().size());
        assertFalse(archivio.getListaPrestiti().contains(p));
    }
    
}
