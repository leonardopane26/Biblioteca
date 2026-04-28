/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Libro;
import biblioteca.model.Utente;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * @class RegistraPrestitoController
 * @brief Controller per la registrazione di un nuovo prestito.
 * @details Gestisce l'interfaccia FXML che permette di associare un libro a un utente. 
 * Include la logica per popolare le ComboBox con i dati esistenti e convalida la 
 * coerenza delle date e la disponibilità effettiva del libro.
 * @author lpane
 * @date 2026-04-18
 */
public class RegistraPrestitoController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    
    @FXML
    private Label lblTitolo;
    @FXML
    private ComboBox<String> cmbLibro;
    @FXML
    private ComboBox<String> cmbUtente;
    @FXML
    private DatePicker dpInizio;
    @FXML
    private DatePicker dpFine;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnConferma;
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public RegistraPrestitoController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Inizializza la finestra popolando i menu a tendina.
     * @details Recupera tramite Stream API le matricole di tutti gli utenti e 
     * gli ISBN di tutti i libri presenti nel sistema per permetterne la selezione.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> matricole = gestore.getListaUtenti().stream()
                                .map(Utente::getMatricola)
                                .collect(Collectors.toList());
        cmbUtente.getItems().setAll(matricole);

        List<String> codiciIsbn = gestore.getListaLibri().stream()
                                .map(Libro::getIsbn)
                                .collect(Collectors.toList());
        cmbLibro.getItems().setAll(codiciIsbn);
    }
    
    /**
     * @brief Chiude lo stage senza salvare.
     */
    @FXML
    private void annullaInserimento(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Registra il prestito dopo aver verificato i vincoli.
     * @param event Click sul pulsante di conferma.
     * @details Il metodo esegue i seguenti controlli di sicurezza:
     * 1. Verifica che tutti i campi siano stati selezionati.
     * 2. Controlla che la data di fine non sia precedente a quella di inizio.
     * 3. Interroga il `GestoreBiblioteca` per verificare se ci sono copie disponibili 
     * e se l'utente non ha superato il limite massimo di prestiti.
     */
    @FXML
    private void confermaInserimento(ActionEvent event){
       String matricola = cmbUtente.getValue();
       String isbn = cmbLibro.getValue();
       LocalDate di = dpInizio.getValue();
       LocalDate df = dpFine.getValue();
       
       if(matricola.isEmpty() || isbn.isEmpty() || di == null || df == null){
           mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "Tutti i campi devono essere selezionati.");
       }
       
       Utente utente = gestore.cercaUtentePerMatricola(matricola);
       Libro libro = gestore.cercaLibroPerISBN(isbn);
       
       if(utente == null){
           mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "L'utente selezionato non è presente nel sistema.");
       }else if(libro == null){
           mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "Il libro selezionato non è presente nel sistema.");
       }
       
       if(df.isBefore(di)){
           mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "La data di fine prestito deve essere successiva alla data di inizio.");
           return;
       }
       
       if(!gestore.effettuaPrestito(utente, libro, di, df)){
           if(libro.getNumeroCopieDisponibili() < 1){
               mostraAlert(AlertType.INFORMATION , "PRESTITO FALLITO" , "Non ci sono copie disponibili per il prestito");
           }else{
               mostraAlert(AlertType.INFORMATION, "PRESTITO FALLITO", "L'utente ha raggiunto il numero massimo di prestiti");
           }
       }else{
            mostraAlert(AlertType.CONFIRMATION , "PRESTITO REGISTRATO CORRETTAMENTE", "Il prestito è stato inserito correttamente nel sistema");
            gestore.salvaStato();
            Stage stage = (Stage) lblTitolo.getScene().getWindow();
            stage.close();
       }
    }
    
    /**
     * @brief Visualizza un messaggio di avviso all'utente.
     * @param tipo Il tipo di alert (ERROR, INFO, etc).
     * @param titolo Titolo della finestra.
     * @param contenuto Testo del messaggio.
     */
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
    
}
