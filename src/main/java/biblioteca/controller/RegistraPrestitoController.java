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
 * FXML Controller class
 *
 * @author lpane
 */
public class RegistraPrestitoController implements Initializable {
    
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
    
    public RegistraPrestitoController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
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
    


    @FXML
    private void annullaInserimento(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
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
    
    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
    
}
