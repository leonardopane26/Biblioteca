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
 *
 * @author lpane
 */
public class GestoreBiblioteca {
    private Archivio archivio;
    private final int MAX_PRESTITI = 3;
    
    public GestoreBiblioteca(){
        this.archivio = Archivio.getInstance();
    }
    
    public void caricaDatiIniziali(){
        try {
            archivio.caricaLibri();
            archivio.caricaUtenti();
            archivio.caricaPrestiti();
        } catch (Exception e) {
            System.err.println("Errore nel caricamento: " + e.getMessage());
        }
    }
    
    public void salvaStato(){
        try {
            archivio.salvaArchivio();
        } catch (Exception e) {
            System.err.println("Errore nel salvataggio: " + e.getMessage());
        }
    }
    
    public void aggiungiLibro(Libro l){
        if(archivio.cercaLibroPerISBN(l.getIsbn()) == null){
            archivio.getListaLibri().add(l);
        }
    }
    
    public void rimuoviLibro(String isbn){
        Libro l = archivio.cercaLibroPerISBN(isbn);
        if(l != null){
            archivio.getListaLibri().remove(l);
        }
    }
    
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
    
    public Libro cercaLibroPerISBN(String isbn) {
        Libro libroTrovato = archivio.cercaLibroPerISBN(isbn);
        return libroTrovato; 
    }
    
    public List<Libro> cercaLibriPerTitolo(String titolo){
        return archivio.getListaLibri().stream()
                .filter(l -> l.getTitolo().toLowerCase().contains(titolo.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    public List<Utente> cercaUtentePerCognome(String cognome) {
    return archivio.getListaUtenti().stream()
            .filter(u -> u.getCognome().toLowerCase().contains(cognome.toLowerCase()))
            .collect(Collectors.toList());
    }
    
    public Utente cercaUtentePerMatricola(String matricola) {
        Utente utenteTrovato = archivio.cercaUtentePerMatricola(matricola);
        return utenteTrovato;
    }
    
    public List<Libro> filtraLibriDisponibili(){
        return archivio.getListaLibri().stream()
                .filter(l -> l.getNumeroCopieDisponibili() > 0)
                .collect(Collectors.toList());
    }
    
    public List<Prestito> filtraPrestitUtente(Utente u){
        return archivio.filtraPrestitiPerUtente(u);
    }
    
    public void registraUtente(Utente u){
        if(archivio.cercaUtentePerMatricola(u.getMatricola()) == null){
            archivio.getListaUtenti().add(u);
        }
    }
    
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
    
    public List<Utente> getListaUtenti() {
        return archivio.getListaUtenti(); 
    }
    
    public List<Libro> getListaLibri(){
        return archivio.getListaLibri();
    }
    
    public List<Prestito> getListaPrestiti(){
        return archivio.getListaPrestiti();
    }
    
    public List<Prestito> getPrestitiInRitardo(){
        LocalDate oggi = LocalDate.now();
        return archivio.getListaPrestiti().stream()
                .filter(p -> !p.isRestituito() && oggi.isAfter(p.getDataFine()))
                .collect(Collectors.toList());
    }
    
    public List<Libro> getCatalogoOrdinato(Comparator<Libro> c){
        return archivio.getListaLibri().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
    
    public List<Utente> getUtentiOrdinati(Comparator<Utente> c){
        return archivio.getListaUtenti().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
    
    public List<Prestito> getPrestitiOrdinati(Comparator<Prestito> c){
        return archivio.getListaPrestiti().stream()
                .sorted(c)
                .collect(Collectors.toList());
    }
    
}
