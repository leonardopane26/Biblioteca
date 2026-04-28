/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Libro;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * @class InserisciLibroController
 * @brief Controller per la gestione della finestra di inserimento e modifica libri.
 * @details Questa classe gestisce l'interfaccia FXML per aggiungere nuovi libri al catalogo
 * o aggiornare i dati di un libro già esistente. Include controlli di validazione per ISBN,
 * formato dell'anno e coerenza del numero di copie.
 * @author lpane
 * @date 2026-04-18
 */
public class InserisciLibroController implements Initializable {
    
    /** @brief Riferimento al gestore logico. */
    private GestoreBiblioteca gestore;
    /** @brief Riferimento al libro in fase di modifica. Se null, la modalità è "Inserimento". */
    private Libro libroInModifica = null;
    
    @FXML
    private TextField txtTitolo;
    @FXML
    private TextField txtAutori;
    @FXML
    private TextField txtAnno;
    @FXML
    private TextField txtIsbn;
    @FXML 
    private TextField txtCopieTotali;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnConferma;
    @FXML
    private Label lblCopieTotali; 
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public InserisciLibroController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Inizializzatore JavaFX.
     * @details Metodo richiamato automaticamente dopo il caricamento del file FXML.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    /**
     * @brief Chiude la finestra senza salvare le modifiche.
     * @param event L'evento scatenato dal click sul tasto annulla.
     */
    @FXML
    private void annullaInserimento(ActionEvent event) {
        Stage stage = (Stage) txtTitolo.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Valida i dati e conferma l'inserimento o la modifica del libro.
     * @param event L'evento scatenato dal click sul tasto conferma.
     * @details Il metodo esegue i seguenti controlli:
     * - Campi obbligatori non vuoti.
     * - ISBN numerico e non duplicato (in caso di nuovo inserimento).
     * - Formato dell'anno valido.
     * - In caso di modifica, il numero di copie totali non può essere inferiore ai libri attualmente in prestito.
     */
    @FXML
    private void confermaInserimento(ActionEvent event) {
        String titolo = txtTitolo.getText();
        String autori = txtAutori.getText().trim();
        String annoStr = txtAnno.getText();
        String isbn = txtIsbn.getText();
        String copie = txtCopieTotali.getText(); 
        
        if (titolo.isEmpty() || autori.isEmpty() || annoStr.isEmpty() || isbn.isEmpty()) {
            mostraAlert(AlertType.ERROR, "ERRORE", "Compila tutti i campi obbligatori");
            return;
        }

        try {            
            int anno = Integer.parseInt(annoStr);
            int nuoveCopieTotali;

            if (libroInModifica != null) {
                nuoveCopieTotali = Integer.parseInt(copie);
            } else {
                nuoveCopieTotali = 1;
            }

            if (!isbn.matches("[0-9]+")) {
                mostraAlert(AlertType.ERROR, "ERRORE", "L'ISBN deve essere numerico");
                return;
            }

            if (libroInModifica != null) {
                int inPrestito = libroInModifica.getNumeroCopieTotali() - libroInModifica.getNumeroCopieDisponibili();

                if (nuoveCopieTotali < inPrestito) {
                    mostraAlert(AlertType.ERROR, "ERRORE COPIE", "Non puoi scendere sotto le " + inPrestito + " copie in prestito");
                    return;
                }

                libroInModifica.setTitolo(titolo);
                libroInModifica.setAnnoPubblicazione(anno);
                libroInModifica.setAutori(Arrays.asList(autori.split(";")));

                int diff = nuoveCopieTotali - libroInModifica.getNumeroCopieTotali();
                libroInModifica.setNumeroCopieTotali(nuoveCopieTotali);
                libroInModifica.setNumeroCopieDisponibili(libroInModifica.getNumeroCopieDisponibili() + diff);

                mostraAlert(AlertType.INFORMATION, "SUCCESSO", "Libro modificato con successo");

            } else {
                if (gestore.cercaLibroPerISBN(isbn) != null) {
                    mostraAlert(AlertType.ERROR, "ERRORE", "Esiste già un libro con questo ISBN");
                    return;
                }

                List<String> listaAutori = Arrays.asList(autori.split(";"));
                Libro nuovo = new Libro(titolo, listaAutori, anno, isbn, nuoveCopieTotali);
                nuovo.setNumeroCopieDisponibili(nuoveCopieTotali);

                gestore.aggiungiLibro(nuovo);
                mostraAlert(AlertType.INFORMATION, "SUCCESSO", "Libro inserito con successo");
            }
            gestore.salvaStato();
            Stage stage = (Stage) txtTitolo.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            mostraAlert(AlertType.ERROR, "ERRORE", "L'anno deve essere in formato numerico.");
        }
    }
    
    /**
     * @brief Configura il controller per la modalità modifica.
     * @param libro Il libro i cui dati devono essere pre-caricati nei campi di testo.
     * @details Rende visibili i campi per la gestione delle copie e disabilita la modifica 
     * dell'ISBN (chiave primaria).
     */
    public void setLibroDaModificare(Libro libro) {
        this.libroInModifica = libro;
        lblCopieTotali.setVisible(true);
        txtCopieTotali.setVisible(true);
        
        txtTitolo.setText(libro.getTitolo());
        txtAutori.setText(String.join(", ", libro.getAutori()));
        txtAnno.setText(String.valueOf(libro.getAnnoPubblicazione()));
        txtIsbn.setText(libro.getIsbn());
        txtCopieTotali.setText(String.valueOf(libro.getNumeroCopieTotali()));
        txtIsbn.setEditable(false); 

        txtCopieTotali.setText(String.valueOf(libro.getNumeroCopieTotali()));
    }
    
    /**
     * @brief Visualizza un messaggio di avviso all'utente.
     * @param tipo Il tipo di alert (ERROR, INFO, etc).
     * @param titolo Titolo della finestra.
     * @param contenuto Testo del messaggio.
     */
    private void mostraAlert(AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}
