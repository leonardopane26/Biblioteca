/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.model;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Archivio {
    
    private static Archivio instance = null;

    private List<Libro> listaLibri;
    private List<Utente> listaUtenti;
    private List<Prestito> listaPrestiti;

    private Archivio() {
        this.listaLibri = new ArrayList<>();
        this.listaUtenti = new ArrayList<>();
        this.listaPrestiti = new ArrayList<>();
    }

    public static Archivio getInstance() {
        if (instance == null) {
            instance = new Archivio();
        }
        return instance;
    }

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


    public void salvaArchivio() throws IOException {
        salvaLibriCSV();
        salvaUtentiCSV();
        salvaPrestitiCSV();
    }

    private void salvaLibriCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("libri.csv"))) {
            for (Libro l : listaLibri) {
                String autori = String.join(";", l.getAutori());
                pw.println(l.getTitolo() + "," + autori + "," + l.getIsbn() + "," + 
                           l.getAnnoPubblicazione() + "," + l.getNumeroCopieTotali() + "," + l.getNumeroCopieDisponibili());
            }
        }
    }

    private void salvaUtentiCSV() throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter("utenti.csv"))) {
            for (Utente u : listaUtenti) {
                pw.println(u.getNome() + "," + u.getCognome() + "," + u.getMatricola() + "," + u.getEmail());
            }
        }
    }

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

    public Libro cercaLibroPerISBN(String isbn) {
        for (Libro l : listaLibri) {
            if (l.getIsbn().equals(isbn)) return l;
        }
        return null;
    }

    public Utente cercaUtentePerMatricola(String matricola) {
        for (Utente u : listaUtenti) {
            if (u.getMatricola().equals(matricola)) return u;
        }
        return null;
    }

    public List<Prestito> filtraPrestitiPerUtente(Utente u) {
        List<Prestito> filtrati = new ArrayList<>();
        for (Prestito p : listaPrestiti) {
            if (p.getUtente().equals(u) && !p.isRestituito()) {
                filtrati.add(p);
            }
        }
        return filtrati;
    }

    public List<Libro> getListaLibri() { return listaLibri; }
    public List<Utente> getListaUtenti() { return listaUtenti; }
    public List<Prestito> getListaPrestiti() { return listaPrestiti; }
    
    public void aggiungiLibro(Libro l) {
        listaLibri.add(l); 
    }
    public void aggiungiUtente(Utente u) { 
        listaUtenti.add(u); 
    }
    public void aggiungiPrestito(Prestito p) {
        listaPrestiti.add(p); 
    }
    public void rimuoviPrestito(Prestito p) {
        this.listaPrestiti.remove(p);
    }
}