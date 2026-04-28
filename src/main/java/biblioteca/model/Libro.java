/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.util.List;

/**
 * @class Libro
 * @brief Rappresenta un libro all'interno del sistema biblioteca.
 * @details Questa classe gestisce i dati anagrafici di un libro e tiene traccia 
 * della disponibilità delle copie fisiche per il prestito.
 * * @author lpane
 * @date 2026-04-14
 */
public class Libro {
    /** @brief Il titolo del libro. */
    private String titolo;
    /** @brief Lista degli autori del libro. */
    private List<String> autori;
    /** @brief L'anno in cui il libro è stato pubblicato. */
    private int annoPubblicazione;
    /** @brief Codice ISBN (identificativo univoco del libro). */
    private String isbn;
    /** @brief Numero totale di copie fisiche possedute dalla biblioteca. */
    private int numeroCopieTotali;
    /** @brief Numero di copie attualmente prestabili. */
    private int numeroCopieDisponibili;
    
    /**
     * @brief Costruttore della classe Libro.
     * @param titolo Titolo dell'opera.
     * @param autori Lista di stringhe contenente i nomi degli autori.
     * @param annoPubblicazione Anno di edizione.
     * @param isbn Codice ISBN univoco.
     * @param numeroCopieTotali Quantità iniziale di copie registrate.
     */
    public Libro(String titolo, List<String> autori, int annoPubblicazione, String isbn, int numeroCopieTotali) {
        this.titolo = titolo;
        this.autori = autori;
        this.annoPubblicazione = annoPubblicazione;
        this.isbn = isbn;
        this.numeroCopieDisponibili = numeroCopieTotali;
        this.numeroCopieTotali = numeroCopieTotali;
    }
    
    /** @return Il titolo del libro. */
    public String getTitolo() {
        return titolo;
    }
    
    /** @param titolo Il nuovo titolo da assegnare al libro. */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }
    
    /** @return La lista degli autori. */
    public List<String> getAutori() {
        return autori;
    }
    
    /** @param autori La nuova lista di autori da impostare. */
    public void setAutori(List<String> autori) {
        this.autori = autori;
    }
    
    /** @return L'anno di pubblicazione. */
    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }
    
    /** @param annoPubblicazione L'anno da impostare. */
    public void setAnnoPubblicazione(int annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }
    
    /** @return Il codice ISBN. */
    public String getIsbn() {
        return isbn;
    }
    
    /** @param isbn Il nuovo codice ISBN. */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    /** @return Il totale delle copie nell'inventario. */
    public int getNumeroCopieTotali() {
        return numeroCopieTotali;
    }
    
    /** @param numeroCopieTotali Imposta il nuovo numero totale di copie. */
    public void setNumeroCopieTotali(int numeroCopieTotali) {
        this.numeroCopieTotali = numeroCopieTotali;
    }
    
    /** @return Il numero di copie attualmente in biblioteca. */
    public int getNumeroCopieDisponibili() {
        return numeroCopieDisponibili;
    }
    
    /** @param numeroCopieDisponibili Imposta il numero di copie disponibili. */
    public void setNumeroCopieDisponibili(int numeroCopieDisponibili) {
        this.numeroCopieDisponibili = numeroCopieDisponibili;
    }
   
    /**
     * @brief Incrementa l'inventario del libro.
     * @details Aggiunge una unità sia alle copie totali che a quelle disponibili.
     */
    public void incrementaCopie(){
        this.numeroCopieTotali++;
        this.numeroCopieDisponibili++;
    }
    
    /**
     * @brief Decrementa l'inventario del libro.
     * @details Rimuove una unità dalle copie totali e disponibili, 
     * impedendo di scendere sotto lo zero.
     */
    public void decrementaCopie(){
        if (this.numeroCopieTotali > 0) {
            this.numeroCopieTotali--;
        if(this.numeroCopieDisponibili > 0){
            this.numeroCopieDisponibili--;
        }
        }
    }
    
    /**
     * @brief Restituisce una stringa con tutti gli autori separati da virgola.
     * @return Stringa formattata degli autori o "Autore sconosciuto" se la lista è vuota.
     */
    public String getAutoriFormattati() {
        if (autori == null || autori.isEmpty()) return "Autore sconosciuto";
        return String.join(", ", autori);
    }
    
    /**
     * @brief Verifica l'uguaglianza tra due libri.
     * @details Due libri sono considerati uguali se hanno lo stesso codice ISBN.
     * @param obj L'oggetto da confrontare.
     * @return true se gli ISBN coincidono, false altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro libro = (Libro) obj;
        return isbn.equals(libro.isbn); 
    }
    
    /** @return L'hashcode basato sul codice ISBN. */
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
    
    /** @return Una rappresentazione testuale del libro (Titolo, Anno e ISBN). */
    @Override
    public String toString() {
        return this.titolo + " (" + this.annoPubblicazione + ") - ISBN: " + this.isbn;
    }
    
}
