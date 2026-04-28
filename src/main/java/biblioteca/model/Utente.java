/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

/**
 * @class Utente
 * @brief Rappresenta un utente registrato presso la biblioteca.
 * @details Questa classe memorizza i dati anagrafici e di contatto dei lettori,
 * utilizzando la matricola come identificatore univoco per le operazioni di ricerca 
 * e confronto.
 * @author lpane
 * @date 2026-04-14
 */
public class Utente {
    /** @brief Nome dell'utente. */
    private String nome;
    /** @brief Cognome dell'utente. */
    private String cognome;
    /** @brief Codice identificativo univoco (matricola universitaria). */
    private String matricola;
    /** @brief Indirizzo email dell'utente. */
    private String email;
    
    /**
     * @brief Costruttore della classe Utente.
     * @param nome Nome del soggetto.
     * @param cognome Cognome del soggetto.
     * @param matricola Codice univoco assegnato all'utente.
     * @param email Indirizzo di posta elettronica.
     */
    public Utente(String nome, String cognome, String matricola, String email) {
        this.nome = nome;
        this.cognome = cognome;
        this.matricola = matricola;
        this.email = email;
    }
    
    /** @return Il nome dell'utente. */
    public String getNome() {
        return nome;
    }
    
    /** @param nome Il nuovo nome da impostare. */
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    /** @return Il cognome dell'utente. */
    public String getCognome() {
        return cognome;
    }
    
    /** @param cognome Il nuovo cognome da impostare. */
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
    
    /** @return La matricola identificativa. */
    public String getMatricola() {
        return matricola;
    }
    
    /** @param matricola La nuova matricola da assegnare. */
    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }
    
    /** @return L'indirizzo email dell'utente. */
    public String getEmail() {
        return email;
    }
    
    /** @param email La nuova email da impostare. */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /** * @brief Fornisce una rappresentazione testuale dell'utente.
     * @return Stringa nel formato "Cognome Nome (Matricola: X)".
     */
    @Override
    public String toString() {
        return cognome + " " + nome + " (Matricola: " + matricola + ")";
    }
    
    /**
     * @brief Confronta due utenti per verificarne l'uguaglianza.
     * @details L'uguaglianza è basata esclusivamente sulla matricola, 
     * garantendo l'univocità dell'utente nel sistema.
     * @param obj L'oggetto da confrontare.
     * @return true se le matricole coincidono, false altrimenti.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Utente utente = (Utente) obj;
        return matricola.equals(utente.matricola);
    }
    
}
