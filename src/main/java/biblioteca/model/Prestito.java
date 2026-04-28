/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.time.LocalDate;

/**
 * @class Prestito
 * @brief Gestisce le informazioni relative al prestito di un libro a un utente.
 * @details La classe tiene traccia delle date di inizio, scadenza e dell'eventuale 
 * restituzione effettiva, permettendo di verificare lo stato del prestito in tempo reale.
 * @author lpane
 * @date 2026-04-14
 */
public class Prestito {
    /** @brief Il libro oggetto del prestito. */
    private Libro libro;
    /** @brief L'utente che ha effettuato il prestito. */
    private Utente utente;
    /** @brief Data in cui il libro è stato prelevato. */
    private LocalDate dataInizio;
    /** @brief Data entro la quale il libro deve essere restituito. */
    private LocalDate dataFine;
    /** @brief Data in cui il libro è stato effettivamente riportato (null se ancora in prestito). */
    private LocalDate dataRestituzioneEffettiva;
    
    /**
     * @brief Costruttore della classe Prestito.
     * @param libro Il libro concesso in prestito.
     * @param utente L'utente beneficiario.
     * @param dataInizio Data di inizio del prestito.
     * @param dataFine Data di scadenza prevista.
     */
    public Prestito(Libro libro, Utente utente, LocalDate dataInizio, LocalDate dataFine) {
        this.libro = libro;
        this.utente = utente;
        this.dataInizio = dataInizio;
        this.dataFine = dataFine;
        this.dataRestituzioneEffettiva = null;
    }
    
    /** @return L'oggetto Libro associato al prestito. */
    public Libro getLibro() {
        return libro;
    }
    
    /** @return L'oggetto Utente associato al prestito. */
    public Utente getUtente() {
        return utente;
    }
    
    /** @return La data di inizio prestito. */
    public LocalDate getDataInizio() {
        return dataInizio;
    }
    
    /** @return La data di scadenza del prestito. */
    public LocalDate getDataFine() {
        return dataFine;
    }
    
    /** @param dataFine Imposta una nuova data di scadenza. */
    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }
    
    /** @return La data di restituzione reale, null se non ancora restituito. */
    public LocalDate getDataRestituzioneEffettiva() {
        return dataRestituzioneEffettiva;
    }
    
    /** @param dataRestituzioneEffettiva La data in cui il libro è stato riportato. */
    public void setDataRestituzioneEffettiva(LocalDate dataRestituzioneEffettiva) {
        this.dataRestituzioneEffettiva = dataRestituzioneEffettiva;
    }
    
    /**
     * @brief Verifica se il prestito è oltre la data di scadenza.
     * @return true se la data corrente è successiva alla data di fine prestito, false altrimenti.
     */
    public boolean isInRitardo(){
        return this.dataFine.isBefore(LocalDate.now());
    }
    
    /**
     * @brief Verifica se il libro è stato restituito.
     * @return true se dataRestituzioneEffettiva è stata valorizzata, false se è ancora null.
     */
    public boolean isRestituito() {
        return dataRestituzioneEffettiva != null;
    }
    
    /**
     * @brief Genera una stringa riassuntiva dei dettagli del prestito.
     * @return Stringa formattata contenente Titolo, Utente (Nome Cognome e Matricola) e data di Scadenza.
     */
    public String getDettagli(){
        return String.format("Libro: %s | Utente: %s %s (%s) | Scadenza: %s", 
        libro.getTitolo(), 
        utente.getNome(), 
        utente.getCognome(), 
        utente.getMatricola(), 
        this.dataFine);
    }
}
