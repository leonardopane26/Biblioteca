/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.util.List;

/**
 *
 * @author lpane
 */
public class Libro {
    
    private String titolo;
    private List<String> autori;
    private int annoPubblicazione;
    private String isbn;
    private int numeroCopieTotali;
    private int numeroCopieDisponibili;

    public Libro(String titolo, List<String> autori, int annoPubblicazione, String isbn, int numeroCopieTotali) {
        this.titolo = titolo;
        this.autori = autori;
        this.annoPubblicazione = annoPubblicazione;
        this.isbn = isbn;
        this.numeroCopieTotali = numeroCopieTotali;
    }
    
    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public List<String> getAutori() {
        return autori;
    }

    public void setAutori(List<String> autori) {
        this.autori = autori;
    }

    public int getAnnoPubblicazione() {
        return annoPubblicazione;
    }

    public void setAnnoPubblicazione(int annoPubblicazione) {
        this.annoPubblicazione = annoPubblicazione;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getNumeroCopieTotali() {
        return numeroCopieTotali;
    }

    public void setNumeroCopieTotali(int numeroCopieTotali) {
        this.numeroCopieTotali = numeroCopieTotali;
    }

    public int getNumeroCopieDisponibili() {
        return numeroCopieDisponibili;
    }

    public void setNumeroCopieDisponibili(int numeroCopieDisponibili) {
        this.numeroCopieDisponibili = numeroCopieDisponibili;
    }
   
    public void incrementaCopie(){
        this.numeroCopieTotali++;
    }
    
    public void decrementaCopie(){
        if (this.numeroCopieTotali > 0) {
        this.numeroCopieTotali--;
        }
    }
    
    public String getAutoriFormattati() {
        if (autori == null || autori.isEmpty()) return "Autore sconosciuto";
        return String.join(", ", autori);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Libro libro = (Libro) obj;
        return isbn.equals(libro.isbn); 
    }
    
    @Override
    public int hashCode() {
        return isbn.hashCode();
    }
    
    @Override
    public String toString() {
        return this.titolo + " (" + this.annoPubblicazione + ") - ISBN: " + this.isbn;
    }
    
}
