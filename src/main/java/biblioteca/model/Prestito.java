/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.time.LocalDate;

/**
 *
 * @author lpane
 */
public class Prestito {
    
    private Libro libro;
    private Utente utente;
    private LocalDate dataInizio;
    private LocalDate dataFine;
    private LocalDate dataRestituzioneEffettiva;

    public Prestito(Libro libro, Utente utente, LocalDate dataInizio, LocalDate dataFine, LocalDate dataRestituzioneEffettiva) {
        this.libro = libro;
        this.utente = utente;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dataRestituzioneEffettiva = null;
    }

    public Libro getLibro() {
        return libro;
    }

    public Utente getUtente() {
        return utente;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }

    public void setDataRestituzioneEffettiva(LocalDate dataRestituzioneEffettiva) {
        this.dataRestituzioneEffettiva = dataRestituzioneEffettiva;
    }
    
    public boolean isInRitardo(){
        return this.dataFine.isBefore(LocalDate.now());
    }
    
    public boolean isRestituito() {
        return dataRestituzioneEffettiva != null;
    }
    
    public String getDettagli(){
        return String.format("Libro: %s | Utente: %s %s (%s) | Scadenza: %s", 
        libro.getTitolo(), 
        utente.getNome(), 
        utente.getCognome(), 
        utente.getMatricola(), 
        this.dataFine);
    }
    
    
}
