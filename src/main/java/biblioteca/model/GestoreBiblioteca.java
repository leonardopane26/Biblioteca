/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @class GestoreBiblioteca
 * @brief Classe che coordina le operazioni tra l'interfaccia e i dati.
 * @details Implementa la logica di business della biblioteca, inclusi i vincoli sui prestiti,
 * la gestione dell'inventario e le funzioni di ricerca/filtraggio. Utilizza il pattern
 * Singleton per accedere all'Archivio.
 * @author lpane
 * @date 2026-04-14
 */
public class GestoreBiblioteca {
    /** @brief Riferimento all'archivio dei dati (Singleton). */
    private Archivio archivio;
    /** @brief Numero massimo di libri che un utente può avere in prestito contemporaneamente. */
    private final int MAX_PRESTITI = 3;
    
    /**
     * @brief Costruttore di GestoreBiblioteca.
     * @details Inizializza il riferimento all'archivio ottenendo l'istanza univoca di Archivio.
     */
    public GestoreBiblioteca(){
        this.archivio = Archivio.getInstance();
    }
    
    /**
     * @brief Carica tutti i file CSV (Libri, Utenti, Prestiti) all'avvio.
     * @details Gestisce internamente le eccezioni di caricamento stampando un errore su console.
     */
    public void caricaDatiIniziali(){
        try {
            archivio.caricaLibri();
            archivio.caricaUtenti();
            archivio.caricaPrestiti();
        } catch (Exception e) {
            System.err.println("Errore nel caricamento: " + e.getMessage());
        }
    }
    
    /**
     * @brief Salva lo stato attuale dell'archivio su file persistenti.
     */
    public void salvaStato(){
        try {
            archivio.salvaArchivio();
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }
    
    /**
     * @brief Aggiunge un nuovo libro al catalogo.
     * @param l Il libro da aggiungere.
     * @details Il libro viene aggiunto solo se l'ISBN non è già presente nell'archivio.
     */
    public void aggiungiLibro(Libro l){
        if(archivio.cercaLibroPerISBN(l.getIsbn()) == null){
            archivio.getListaLibri().add(l);
        }
    }
    
    /**
     * @brief Rimuove un libro dal catalogo tramite ISBN.
     * @param isbn Il codice ISBN del libro da eliminare.
     */
    public void rimuoviLibro(String isbn){
        Libro l = archivio.cercaLibroPerISBN(isbn);
        if(l != null){
            archivio.getListaLibri().remove(l);
        }
    }
    
    /**
     * @brief Rimuove un utente dal sistema.
     * @param matricola La matricola dell'utente da rimuovere.
     * @return Stringa descrittiva dell'esito (successo o motivo del fallimento).
     * @details Impedisce la rimozione se l'utente ha ancora dei prestiti attivi.
     */
    public String rimuoviUtente(String matricola) {
        Utente u = archivio.cercaUtentePerMatricola(matricola);

        if (u == null) return "Utente non trovato.";

        boolean haPrestiti = archivio.getListaPrestiti().stream()
                .anyMatch(p -> p.getUtente().getMatricola().equals(matricola) && !p.isRestituito());

        if (haPrestiti) {
            return "Impossibile rimuovere: l'utente ha ancora libri da restituire!";
        }

        archivio.getListaUtenti().remove(u);
        return "Utente rimosso con successo.";
    }
    
    /**
     * @brief Cerca un libro tramite codice ISBN.
     * @param isbn ISBN da cercare.
     * @return L'oggetto Libro trovato o null.
     */
    public Libro cercaLibroPerISBN(String isbn) {
        Libro libroTrovato = archivio.cercaLibroPerISBN(isbn);
        return libroTrovato; 
    }
    
    /**
     * @brief Cerca libri il cui titolo contiene la stringa specificata.
     * @param titolo Parte del titolo da cercare.
     * @return Lista di libri che soddisfano il criterio.
     */
    public List<Libro> cercaLibriPerTitolo(String titolo){
        return archivio.getListaLibri().stream()
                .filter(l -> l.getTitolo().toLowerCase().contains(titolo.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    /**
     * @brief Cerca utenti tramite cognome.
     * @param cognome Parte del cognome da cercare.
     * @return Lista di utenti che soddisfano il criterio.
     */
    public List<Utente> cercaUtentePerCognome(String cognome) {
    return archivio.getListaUtenti().stream()
            .filter(u -> u.getCognome().toLowerCase().contains(cognome.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    /**
     * @brief Cerca un utente tramite matricola.
     * @param matricola Matricola da cercare.
     * @return L'oggetto Utente trovato o null.
     */
    public Utente cercaUtentePerMatricola(String matricola) {
        Utente utenteTrovato = archivio.cercaUtentePerMatricola(matricola);
        return utenteTrovato;
    }
    
    /**
     * @brief Filtra il catalogo mostrando solo i libri con almeno una copia disponibile.
     * @return Lista di libri disponibili.
     */
    public List<Libro> filtraLibriDisponibili(){
        return archivio.getListaLibri().stream()
                .filter(l -> l.getNumeroCopieDisponibili() > 0)
                .collect(Collectors.toList());
    }
    
    /**
     * @brief Recupera tutti i prestiti di un determinato utente.
     * @param u L'utente di cui filtrare i prestiti.
     * @return Lista di oggetti Prestito.
     */
    public List<Prestito> filtraPrestitUtente(Utente u){
        return archivio.filtraPrestitiPerUtente(u);
    }
    
    /**
     * @brief Registra un nuovo utente nel sistema.
     * @param u L'utente da registrare.
     */
    public void registraUtente(Utente u){
        if(archivio.cercaUtentePerMatricola(u.getMatricola()) == null){
            archivio.getListaUtenti().add(u);
        }
    }
    
    /**
     * @brief Esegue la logica di creazione di un nuovo prestito.
     * @param u L'utente richiedente.
     * @param l Il libro richiesto.
     * @param inizio Data di inizio prestito.
     * @param scadenza Data di scadenza.
     * @return true se il prestito è registrato, false se l'utente ha raggiunto il limite o il libro è esaurito.
     * @details Se il prestito va a buon fine, il numero di copie disponibili del libro viene decrementato.
     */
    public boolean effettuaPrestito(Utente u, Libro l , LocalDate inizio, LocalDate scadenza){
        long prestitiAttivi = archivio.getListaPrestiti().stream()
                                      .filter(p -> p.getUtente().getMatricola().equals(u.getMatricola())&&!p.isRestituito())
                                      .count();
        
        if(prestitiAttivi >= MAX_PRESTITI){
            return false;
        }
        
        if(l.getNumeroCopieDisponibili() > 0){
            Prestito nuovo = new Prestito(l , u , inizio , scadenza);
            archivio.getListaPrestiti().add(nuovo);
  
            l.setNumeroCopieDisponibili(l.getNumeroCopieDisponibili() - 1);
            return true;
        }
        return false;
    }
    
    /**
     * @brief Gestisce la restituzione di un libro.
     * @param p Il prestito da chiudere.
     * @param dataRest La data effettiva di riconsegna.
     * @return Messaggio che indica se la restituzione è regolare o in ritardo.
     * @details Aggiorna le copie disponibili del libro e rimuove il prestito dalla lista attiva.
     */
    public String restituisciLibro(Prestito p , LocalDate dataRest){
        p.setDataRestituzioneEffettiva(dataRest);
        
        String messaggio;
        
        if(p.isInRitardo()){
            messaggio = "ATTENZIONE: Restituzione in RITARDO!";
        }else{
            messaggio = "Restituzione regolare";
        }
        
        p.getLibro().setNumeroCopieDisponibili(p.getLibro().getNumeroCopieDisponibili() + 1);
        
        archivio.getListaPrestiti().remove(p);
        
        return messaggio;
    }
    
    /** @return La lista completa degli utenti. */
    public List<Utente> getListaUtenti() {
        return archivio.getListaUtenti(); 
    }
    
    /** @return La lista completa dei libri. */
    public List<Libro> getListaLibri(){
        return archivio.getListaLibri();
    }
    
    /** @return La lista di tutti i prestiti registrati. */
    public List<Prestito> getListaPrestiti(){
        return archivio.getListaPrestiti();
    }
    
    /**
     * @brief Individua tutti i prestiti scaduti e non ancora restituiti.
     * @return Lista dei prestiti in ritardo rispetto alla data odierna.
     */
    public List<Prestito> getPrestitiInRitardo(){
        LocalDate oggi = LocalDate.now();
        return archivio.getListaPrestiti().stream()
                .filter(p -> !p.isRestituito() && oggi.isAfter(p.getDataFine()))
                .collect(Collectors.toList());
    }
    
    /**
     * @brief Restituisce il catalogo libri ordinato secondo un criterio specifico.
     * @param c Il comparatore che definisce l'ordine (es. per Titolo, per Anno).
     * @return Lista ordinata di libri.
     */
    public List<Libro> getCatalogoOrdinato(Comparator<Libro> c){
        return archivio.getListaLibri().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
    
    /**
     * @brief Restituisce la lista utenti ordinata.
     * @param c Il comparatore per gli utenti.
     * @return Lista ordinata di utenti.
     */
    public List<Utente> getUtentiOrdinati(Comparator<Utente> c){
        return archivio.getListaUtenti().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
    
    /**
     * @brief Restituisce la lista prestiti ordinata.
     * @param c Il comparatore per i prestiti.
     * @return Lista ordinata di prestiti.
     */
    public List<Prestito> getPrestitiOrdinati(Comparator<Prestito> c){
        return archivio.getListaPrestiti().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
}
