/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import biblioteca.utils.LibroComparator;
import biblioteca.utils.PrestitoComparator;
import biblioteca.utils.UtenteComparator;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
public class GestoreBibliotecaTest {
    
    private GestoreBiblioteca gestore;
    private Utente utente;
    private Libro libro;
    private LocalDate oggi;
    
    public GestoreBibliotecaTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        System.out.println("Starting GestoreBibliotecaTest...");
    }
    
    @AfterAll
    public static void tearDownClass() {
        System.out.println("Finished GestoreBibliotecaTest...");
    }
    
    @BeforeEach
    public void setUp() {
        gestore = new GestoreBiblioteca();
        oggi = LocalDate.now();
        
        Archivio.getInstance().getListaLibri().clear();
        Archivio.getInstance().getListaUtenti().clear();
        Archivio.getInstance().getListaPrestiti().clear();
        
        utente = new Utente("Leonardo" , "Pane" , "0612707333" , "leonardo@gmail.com");
        libro = new Libro("Nuovo Libro" , new ArrayList<>(Arrays.asList("autore")) , 2026 , "00001" , 1);
        
        gestore.aggiungiLibro(libro);
        gestore.registraUtente(utente);
        gestore.effettuaPrestito(utente, libro, oggi, oggi.plusDays(10));
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of caricaDatiIniziali method, of class GestoreBiblioteca.
     */
    @Test
    public void testCaricaDatiIniziali() {
        System.out.println("caricaDatiIniziali");
        gestore.caricaDatiIniziali();
        
        assertNotNull(gestore.getListaUtenti());
        assertNotNull(gestore.getListaLibri());
        assertNotNull(gestore.getListaPrestiti());
    }

    /**
     * Test of salvaStato method, of class GestoreBiblioteca.
     */
    @Test
    public void testSalvaStato() {
        System.out.println("salvaStato");
        assertDoesNotThrow(() -> gestore.salvaStato());
    }

    /**
     * Test of aggiungiLibro method, of class GestoreBiblioteca.
     */
    @Test
    public void testAggiungiLibro() {
        System.out.println("aggiungiLibro");
        
        assertEquals(1 , gestore.getListaLibri().size());
        assertEquals(libro , gestore.cercaLibroPerISBN("00001"));
        
        Libro duplicato = new Libro("LibroTest" , new ArrayList<>(Arrays.asList("autore finto")) , 2020 , "00001" , 1);
        gestore.aggiungiLibro(duplicato);
        assertEquals(1 , gestore.getListaLibri().size());
    }

    /**
     * Test of rimuoviLibro method, of class GestoreBiblioteca.
     */
    @Test
    public void testRimuoviLibro() {
        System.out.println("rimuoviLibro");
        
        assertEquals(1 , gestore.getListaLibri().size());
        
        gestore.rimuoviLibro(libro.getIsbn());
        assertEquals(0 , gestore.getListaLibri().size());
        assertNull(gestore.cercaLibroPerISBN("00001"));
    }

    /**
     * Test of rimuoviUtente method, of class GestoreBiblioteca.
     */
    @Test
    public void testRimuoviUtente() {
        System.out.println("rimuoviUtente");
        
        Utente daRimuovere = new Utente("Mario" , "Rossi" , "01" , "mario@gmail.com");
        gestore.registraUtente(daRimuovere);
        String esitoCorretto = gestore.rimuoviUtente("01");
        assertEquals("Utente rimosso con successo." , esitoCorretto);
        assertEquals(1 , gestore.getListaUtenti().size());
        
        gestore.registraUtente(daRimuovere);
        
        String esitoFallito = gestore.rimuoviUtente("0612707333");
        assertEquals("Impossibile rimuovere: l'utente ha ancora libri da restituire!" , esitoFallito);
        assertEquals(2 , gestore.getListaUtenti().size());
    }

    /**
     * Test of cercaLibroPerISBN method, of class GestoreBiblioteca.
     */
    @Test
    public void testCercaLibroPerISBN() {
        System.out.println("cercaLibroPerISBN");
        
        Libro trovato = gestore.cercaLibroPerISBN(libro.getIsbn());
        assertNotNull(trovato);
        assertEquals(trovato.getTitolo() , libro.getTitolo());
        
        Libro nonTrovato = gestore.cercaLibroPerISBN("123456");
        assertNull(nonTrovato);
    }

    /**
     * Test of cercaLibriPerTitolo method, of class GestoreBiblioteca.
     */
    @Test
    public void testCercaLibriPerTitolo() {
        System.out.println("cercaLibriPerTitolo");
        List<Libro> risultati = gestore.cercaLibriPerTitolo(libro.getTitolo());
        assertFalse(risultati.isEmpty());
        assertTrue(risultati.contains(libro));
        
        String parteTitolo = libro.getTitolo().substring(0, 5).toLowerCase();
        List<Libro> risultatiParziali = gestore.cercaLibriPerTitolo(parteTitolo);
        assertTrue(risultatiParziali.contains(libro));
        
        List<Libro> vuota = gestore.cercaLibriPerTitolo("Titolo assente");
        assertTrue(vuota.isEmpty());
    }

    /**
     * Test of cercaUtentePerCognome method, of class GestoreBiblioteca.
     */
    @Test
    public void testCercaUtentePerCognome() {
        System.out.println("cercaUtentePerCognome");
        
        List<Utente> risultati = gestore.cercaUtentePerCognome(utente.getCognome());
        assertFalse(risultati.isEmpty());
        assertTrue(risultati.contains(utente));

        String parziale = utente.getCognome().substring(0, 2).toLowerCase();
        List<Utente> risultatiParziali = gestore.cercaUtentePerCognome(parziale);
        assertTrue(risultatiParziali.contains(utente));
        
        List<Utente> vuota = gestore.cercaUtentePerCognome("anonimo");
        assertTrue(vuota.isEmpty());
    }

    /**
     * Test of cercaUtentePerMatricola method, of class GestoreBiblioteca.
     */
    @Test
    public void testCercaUtentePerMatricola() {
        System.out.println("cercaUtentePerMatricola");
        
        Utente trovato = gestore.cercaUtentePerMatricola(utente.getMatricola());
        assertNotNull(trovato);
        assertEquals(utente.getMatricola() , trovato.getMatricola());
        
        Utente nonTrovato = gestore.cercaUtentePerMatricola("00000000");
        assertNull(nonTrovato);
    }

    /**
     * Test of filtraLibriDisponibili method, of class GestoreBiblioteca.
     */
    @Test
    public void testFiltraLibriDisponibili() {
        System.out.println("filtraLibriDisponibili");
        
        libro.setNumeroCopieDisponibili(1);
        List<Libro> disponibili = gestore.filtraLibriDisponibili();
        assertTrue(disponibili.contains(libro));

        libro.setNumeroCopieDisponibili(0);
        List<Libro> terminato = gestore.filtraLibriDisponibili();
        assertFalse(terminato.contains(libro));
    }

    /**
     * Test of filtraPrestitUtente method, of class GestoreBiblioteca.
     */
    @Test
    public void testFiltraPrestitUtente() {
        System.out.println("filtraPrestitUtente");
        
        Utente utente2 = new Utente("Nome" , "Cognome" , "00002" , "nome@gmail.com");
        gestore.registraUtente(utente2);
        
        Libro libro2 = new Libro("Titolo" , new ArrayList<>() , 2026 , "02" , 1);
        libro2.setNumeroCopieDisponibili(1);
        gestore.aggiungiLibro(libro2);
        
        gestore.effettuaPrestito(utente2, libro2, oggi, oggi.plusDays(30));
        
        List<Prestito> prestitiUtente = gestore.filtraPrestitUtente(utente);
        assertEquals(1 , prestitiUtente.size());
        assertEquals(utente.getMatricola() , prestitiUtente.get(0).getUtente().getMatricola());
        
        assertFalse(prestitiUtente.stream().anyMatch(p -> p.getUtente().equals(utente2)));
    }

    /**
     * Test of registraUtente method, of class GestoreBiblioteca.
     */
    @Test
    public void testRegistraUtente() {
        System.out.println("registraUtente");    
        assertTrue(gestore.getListaUtenti().contains(utente));
    
        int dimensione = gestore.getListaUtenti().size();
        
        Utente doppione = new Utente("Nome" , "Cognome", utente.getMatricola() , "nome@gmail.com");
        assertEquals(dimensione , gestore.getListaUtenti().size());
    }

    /**
     * Test of effettuaPrestito method, of class GestoreBiblioteca.
     */
    @Test
    public void testEffettuaPrestito() {
        System.out.println("effettuaPrestito");
        
        assertEquals(0 , libro.getNumeroCopieDisponibili());
        assertEquals(1 , gestore.getListaPrestiti().size());
        
        Utente utente2 = new Utente("Matteo" , "Pane" , "1234" , "matteo@gmail.com");
        gestore.registraUtente(utente2);
        boolean esitoFallito = gestore.effettuaPrestito(utente2, libro, oggi, oggi.plusDays(1));
        assertFalse(esitoFallito);
        
        Libro l2 = new Libro("Libro 2" , new ArrayList<>() , 2000 , "1000" , 1);
        l2.setNumeroCopieDisponibili(1);
        Libro l3 = new Libro("Libro 3" , new ArrayList<>() , 2001 , "2000" , 1);
        l3.setNumeroCopieDisponibili(1);
        Libro l4 = new Libro("Libro 4" , new ArrayList<>(), 2002 , "3000" , 1);
        l4.setNumeroCopieDisponibili(1);
        
        gestore.effettuaPrestito(utente, l2, oggi, oggi);
        gestore.effettuaPrestito(utente, l3, oggi, oggi);
        
        boolean limite = gestore.effettuaPrestito(utente, l4, oggi, oggi);
        assertFalse(limite);
    }

    /**
     * Test of restituisciLibro method, of class GestoreBiblioteca.
     */
    @Test
    public void testRestituisciLibro() {
        System.out.println("restituisciLibro");
        
        Prestito p = gestore.getListaPrestiti().get(0);
        int copiePrimaDellaRestituzione = libro.getNumeroCopieDisponibili();
        
        String esito = gestore.restituisciLibro(p, oggi);
        
        assertEquals("Restituzione regolare", esito);
        assertEquals(copiePrimaDellaRestituzione + 1 , libro.getNumeroCopieDisponibili());
        assertTrue(gestore.getListaPrestiti().isEmpty());
        
        
    }

    /**
     * Test of getListaUtenti method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetListaUtenti() {
        System.out.println("getListaUtenti");
        
        List<Utente> lista = gestore.getListaUtenti();
        assertNotNull(lista);
        assertTrue(lista.contains(utente));
    }

    /**
     * Test of getListaLibri method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetListaLibri() {
        System.out.println("getListaLibri");
        
        List<Libro> lista = gestore.getListaLibri();
        assertNotNull(lista);
        assertTrue(lista.contains(libro));
    }

    /**
     * Test of getListaPrestiti method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetListaPrestiti() {
        System.out.println("getListaPrestiti");
        
        List<Prestito> lista = gestore.getListaPrestiti();
        assertNotNull(lista);
        assertEquals(1 , lista.size());
        assertEquals(utente.getMatricola() , lista.get(0).getUtente().getMatricola());
    }

    /**
     * Test of getPrestitiInRitardo method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetPrestitiInRitardo() {
        System.out.println("getPrestitiInRitardo");
        
        Utente ritardatario = new Utente("Nome" , "Cognome" , "1111" , "nome@gmail.com");
        gestore.registraUtente(ritardatario);
        
        Libro libro2 = new Libro("Libro" , new ArrayList<>(Arrays.asList()) , 2026 , "12345" , 1);
        libro2.setNumeroCopieDisponibili(1);
        gestore.aggiungiLibro(libro2);
        
        gestore.effettuaPrestito(ritardatario, libro2, oggi, oggi.minusDays(10));
        
        List<Prestito> ritardi = gestore.getPrestitiInRitardo();
        assertEquals(1 , ritardi.size());
        assertEquals("12345" , ritardi.get(0).getLibro().getIsbn());
    }

    /**
     * Test of getCatalogoOrdinato method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetCatalogoOrdinato() {
        System.out.println("getCatalogoOrdinato");
        
        Libro libroA = new Libro("A-Libro", new ArrayList<>(), 2024, "03", 1);
        Libro libroZ = new Libro("Z-Libro", new ArrayList<>(), 2024, "04", 1);
        gestore.aggiungiLibro(libroA);
        gestore.aggiungiLibro(libroZ);
        
        LibroComparator comparatorTitolo = new LibroComparator();
        
        List<Libro> ordinata = gestore.getCatalogoOrdinato(comparatorTitolo);
        
        assertEquals("A-Libro" , ordinata.get(0).getTitolo());
        assertEquals("Nuovo Libro" , ordinata.get(1).getTitolo());
        assertEquals("Z-Libro" , ordinata.get(2).getTitolo());
    }

    /**
     * Test of getUtentiOrdinati method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetUtentiOrdinati() {
        System.out.println("getUtentiOrdinati");
        
        Utente u1 = new Utente("Angelo" , "Z" , "000000001" , "Angelo@gmail.com");
        Utente u2 = new Utente("Zeno" , "A" , "0000000002" , "Zeno@gmail.com");
        gestore.registraUtente(u1);
        gestore.registraUtente(u2);
        
        UtenteComparator comparatorCognome = new UtenteComparator();
        
        List<Utente> ordinati = gestore.getUtentiOrdinati(comparatorCognome);
        
        assertEquals("A" , ordinati.get(0).getCognome());
        assertEquals("Pane" , ordinati.get(1).getCognome());
        assertEquals("Z" , ordinati.get(2).getCognome());
    }

    /**
     * Test of getPrestitiOrdinati method, of class GestoreBiblioteca.
     */
    @Test
    public void testGetPrestitiOrdinati() {
        System.out.println("getPrestitiOrdinati");
        
        Libro l2 = new Libro("Libro 2" , new ArrayList<>(), 2000 , "1926" , 1);
        l2.setNumeroCopieDisponibili(1);
        gestore.aggiungiLibro(l2);
        
        gestore.effettuaPrestito(utente, l2, oggi, oggi.plusDays(5));
        
        PrestitoComparator comparator = new PrestitoComparator();
        List<Prestito> ordinati = gestore.getPrestitiOrdinati(comparator);
    }
    
}
