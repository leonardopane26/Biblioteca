/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Prestito;
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
public class RegistraRestituzioneController implements Initializable {
    
    private GestoreBiblioteca gestore;
    private Prestito prestitoScelto;
    
    @FXML
    private Label lblTitolo;
    @FXML
    private ComboBox<String> cmbUtente;
    @FXML
    private ComboBox<String> cmbLibro;
    @FXML
    private DatePicker dpFine;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnConferma;
    
    public RegistraRestituzioneController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> matricole = gestore.getListaPrestiti().stream()
                .map(p -> p.getUtente().getMatricola())
                .distinct()
                .collect(Collectors.toList());
        
        List<String> isbn = gestore.getListaPrestiti().stream()
                .map(p -> p.getLibro().getIsbn())
                .distinct()
                .collect(Collectors.toList());
        
        cmbUtente.getItems().setAll(matricole);
        cmbLibro.getItems().setAll(isbn);
        
        cmbUtente.valueProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            List<String> libriPerUtente = gestore.getListaPrestiti().stream()
                .filter(p -> p.getUtente().getMatricola().equals(newVal))
                .map(p -> p.getLibro().getIsbn())
                .collect(Collectors.toList());
            
            if (!cmbLibro.getItems().equals(libriPerUtente)) {
                cmbLibro.getItems().setAll(libriPerUtente);
            }
        }    
        });
        
        cmbLibro.valueProperty().addListener((obs, oldVal, newVal) -> {
        if (newVal != null) {
            List<String> utentiConLibro = gestore.getListaPrestiti().stream()
                .filter(p -> p.getLibro().getIsbn().equals(newVal))
                .map(p -> p.getUtente().getMatricola())
                .collect(Collectors.toList());

            if (!cmbUtente.getItems().equals(utentiConLibro)) {
                cmbUtente.getItems().setAll(utentiConLibro);
            }
        }
        });
    }
    
    @FXML
    private void annullaRestituzione(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void confermaRestituzione(ActionEvent event){
        String matricolaScelta = cmbUtente.getValue();
        String isbnScelto = cmbLibro.getValue();
        LocalDate dataRestituzione = dpFine.getValue();
        
        Prestito prestito = gestore.getListaPrestiti().stream()
            .filter(p -> p.getUtente().getMatricola().equals(matricolaScelta) && 
                         p.getLibro().getIsbn().equals(isbnScelto))
            .findFirst()
            .orElse(null);
        
        if(matricolaScelta.isEmpty() || isbnScelto.isEmpty() || dataRestituzione == null){
            mostraAlert(AlertType.ERROR , "RESTITUZIONE FALLITA" , "Tutti i campi devono essere riempiti correttamente.");
        }else if(dataRestituzione.isBefore(prestito.getDataInizio())){
            mostraAlert(AlertType.ERROR, "RESTITUZIONE FALLITA", "La data di fine prestito non è valida.");
        }else if(dataRestituzione.isAfter(prestito.getDataFine())){
            gestore.restituisciLibro(prestito, dataRestituzione);
            gestore.salvaStato();
            mostraAlert(AlertType.INFORMATION , "RESTITUZIONE IN RITARDO" , "Il libro è stato restituito in ritardo.");
        }else{
            gestore.restituisciLibro(prestito, dataRestituzione);
            gestore.salvaStato();
            mostraAlert(AlertType.INFORMATION , "RESTITUZIONE AVVENUTA CON SUCCESSO" , "Il libro è stato restituito correttamente.");
            Stage stage = (Stage) lblTitolo.getScene().getWindow();
            stage.close();
        } 
    }
    
    public void setDatiPrestito(Prestito prestito){
        LocalDate df = LocalDate.now();
        cmbUtente.setValue(prestito.getUtente().getMatricola());
        cmbLibro.setValue(prestito.getLibro().getIsbn());
        dpFine.setValue(df);
    }
    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}
