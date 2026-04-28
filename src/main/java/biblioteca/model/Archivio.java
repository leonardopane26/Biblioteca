/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

/**
 * @class Archivio
 * @brief Gestisce la persistenza dei dati e l'archiviazione del sistema.
 * @details La classe utilizza il pattern **Singleton** per garantire un unico punto di accesso ai dati.
 * Si occupa della lettura e scrittura su file CSV per libri, utenti e prestiti, fungendo da 
 * database testuale per l'applicazione.
 * @author lpane
 * @date 2026-04-14
 */
public class Archivio {
    
    /** @brief Istanza univoca della classe (Singleton). */
    private static Archivio instance = null;
    
    /** @brief Elenco in memoria di tutti i libri. */
    private List<Libro> listaLibri;
    /** @brief Elenco in memoria di tutti gli utenti registrati. */
    private List<Utente> listaUtenti;
    /** @brief Elenco in memoria di tutti i prestiti registrati. */
    private List<Prestito> listaPrestiti;
    
    /**
     * @brief Costruttore privato per impedire l'istanziazione esterna.
     * @details Inizializza le liste vuote per libri, utenti e prestiti.
     */
    private Archivio() {
        this.listaLibri = new ArrayList<>();
        this.listaUtenti = new ArrayList<>();
        this.listaPrestiti = new ArrayList<>();
    }
    
    /**
     * @brief Restituisce l'istanza univoca dell'Archivio.
     * @return L'unica istanza esistente di Archivio.
     */ 
    public static Archivio getInstance() {
        if (instance == null) {
            instance = new Archivio();
        }
        return instance;
    }
    
    /**
     * @brief Carica i libri dal file "libri.csv".
     * @throws IOException In caso di errori durante la lettura del file.
     * @details Legge ogni riga del CSV, separa i campi e crea oggetti di tipo Libro. 
     * Gli autori sono attesi separati da punto e virgola (;).
     */
    public void caricaLibri() throws IOException {
        listaLibri.clear();
        File file = new File("libri.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(",");
                List<String> autori = Arrays.asList(dati[1].split(";"));
                int copieDisponibili = Integer.parseInt(dati[5]);
                Libro l = new Libro(dati[0], autori, Integer.parseInt(dati[3]), dati[2], Integer.parseInt(dati[4]));
                l.setNumeroCopieDisponibili(copieDisponibili);
                listaLibri.add(l);
            }
        }
    }
    
    /**
     * @brief Carica gli utenti dal file "utenti.csv".
     * @throws IOException In caso di errori di lettura.
     */
    public void caricaUtenti() throws IOException {
        listaUtenti.clear();
        File file = new File("utenti.csv");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String riga;
            while ((riga = br.readLine()) != null) {
                String[] dati = riga.split(",");
                Utente u = new Utente(dati[0], dati[1], dati[2], dati[3]);
                listaUtenti.add(u);
            }
        }
    }
    
    /**
     * @brief Carica i prestiti dal file "prestiti.csv".
     * @throws IOException In caso di errori di lettura.
     * @details Ricostruisce le associazioni tra Libri e Utenti esistenti 
     * cercandoli rispettivamente per ISBN e Matricola.
     */
   public void caricaPrestiti() throws IOException {
    listaPrestiti.clear();
    File file = new File("prestiti.csv");
    if (!file.exists()) return;

    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String riga;
        while ((riga = br.readLine()) != null) {
            String[] dati = riga.split(",");
            
            Libro lib = cercaLibroPerISBN(dati[0]);
            Utente ut = cercaUtentePerMatricola(dati[1]);

            if (lib != null && ut != null) {
                LocalDate inizio = LocalDate.parse(dati[2]);
                LocalDate fine = LocalDate.parse(dati[3]);

                Prestito p = new Prestito(lib, ut, inizio, fine);
                
                listaPrestiti.add(p);
            }
        }
    }
}

    /**
     * @brief Salva tutti i dati correnti dell'archivio sui rispettivi file CSV.
     * @throws IOException In caso di errori durante la scrittura.
     */
    public void salvaArchivio() throws IOException {
        salvaLibriCSV();
        salvaUtentiCSV();
        salvaPrestitiCSV();
    }
    
    /** @brief Scrive i dati dei libri su "libri.csv". */
    private void salvaLibriCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("libri.csv"))) {
            for (Libro l : listaLibri) {
                String autori = String.join(";", l.getAutori());
                pw.println(l.getTitolo() + "," + autori + "," + l.getIsbn() + "," + 
                           l.getAnnoPubblicazione() + "," + l.getNumeroCopieTotali() + "," + l.getNumeroCopieDisponibili());
            }
        }
    }
    
    /** @brief Scrive i dati degli utenti su "utenti.csv". */
    private void salvaUtentiCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("utenti.csv"))) {
            for (Utente u : listaUtenti) {
                pw.println(u.getNome() + "," + u.getCognome() + "," + u.getMatricola() + "," + u.getEmail());
            }
        }
    }
    
    /** @brief Scrive i dati dei prestiti su "prestiti.csv". */
    private void salvaPrestitiCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("prestiti.csv"))) {
            for (Prestito p : listaPrestiti) {
                String dataRest = (p.getDataRestituzioneEffettiva() != null) 
                                  ? p.getDataRestituzioneEffettiva().toString() : "null";
                pw.println(p.getLibro().getIsbn() + "," + p.getUtente().getMatricola() + "," + 
                           p.getDataInizio() + "," + p.getDataFine() + "," + dataRest);
            }
        }
    }
    
    /**
     * @brief Ricerca un libro nella lista interna tramite ISBN.
     * @param isbn Codice ISBN da cercare.
     * @return L'oggetto Libro corrispondente o null.
     */
    public Libro cercaLibroPerISBN(String isbn) {
        for (Libro l : listaLibri) {
            if (l.getIsbn().equals(isbn)) return l;
        }
        return null;
    }
    
    /**
     * @brief Ricerca un utente nella lista interna tramite matricola.
     * @param matricola Matricola da cercare.
     * @return L'oggetto Utente corrispondente o null.
     */
    public Utente cercaUtentePerMatricola(String matricola) {
        for (Utente u : listaUtenti) {
            if (u.getMatricola().equals(matricola)) return u;
        }
        return null;
    }
    
    /**
     * @brief Filtra i prestiti non ancora restituiti per un utente specifico.
     * @param u L'utente di riferimento.
     * @return Lista dei prestiti attivi dell'utente.
     */
    public List<Prestito> filtraPrestitiPerUtente(Utente u) {
        List<Prestito> filtrati = new ArrayList<>();
        for (Prestito p : listaPrestiti) {
            if (p.getUtente().equals(u) && !p.isRestituito()) {
                filtrati.add(p);
            }
        }
        return filtrati;
    }
    
    /** @return La lista dei libri caricati in memoria. */
    public List<Libro> getListaLibri() { 
        return listaLibri; 
    }
    /** @return La lista degli utenti caricati in memoria. */
    public List<Utente> getListaUtenti() { 
        return listaUtenti; 
    }
    /** @return La lista dei prestiti caricati in memoria. */
    public List<Prestito> getListaPrestiti() {
        return listaPrestiti; 
    }
    
    /** @param l Libro da aggiungere alla lista interna. */
    public void aggiungiLibro(Libro l) {
        listaLibri.add(l); 
    }
    /** @param u Utente da aggiungere alla lista interna. */
    public void aggiungiUtente(Utente u) { 
        listaUtenti.add(u); 
    }
    /** @param p Prestito da aggiungere alla lista interna. */
    public void aggiungiPrestito(Prestito p) {
        listaPrestiti.add(p); 
    }
    /** @param p Prestito da rimuovere dalla lista interna. */
    public void rimuoviPrestito(Prestito p) {
        this.listaPrestiti.remove(p);
    }
}