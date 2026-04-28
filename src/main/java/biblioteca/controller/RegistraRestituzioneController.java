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
 * @class RegistraRestituzioneController
 * @brief Controller per la gestione del rientro dei libri in biblioteca.
 * @details Gestisce l'interfaccia FXML che permette di chiudere un prestito attivo. 
 * La classe implementa una logica di filtraggio dinamico tra le ComboBox "Utente" e "Libro" 
 * per facilitare l'individuazione del prestito corretto e gestisce la notifica di eventuali ritardi.
 * @author lpane
 * @date 2026-04-18
 */
public class RegistraRestituzioneController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    /** @brief Riferimento al prestito specifico selezionato per la restituzione. */
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
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public RegistraRestituzioneController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Inizializza i componenti e imposta i listener per il filtraggio incrociato.
     * @details All'avvio, le ComboBox vengono popolate con i dati dei soli prestiti attivi.
     * Vengono aggiunti dei listener sulle proprietà di selezione:
     * - Se si seleziona un utente, la lista dei libri viene filtrata mostrando solo quelli in suo possesso.
     * - Se si seleziona un libro, viene mostrato solo l'utente (o gli utenti) che lo hanno in prestito.
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
    
    /**
     * @brief Chiude la finestra senza registrare la restituzione.
     * @param event Click sul pulsante annulla.
     */
    @FXML
    private void annullaRestituzione(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Valida i dati e processa la restituzione del libro.
     * @param event Click sul pulsante conferma.
     * @details Il metodo recupera il prestito corrispondente alla coppia Utente-Libro selezionata.
     * Effettua i seguenti controlli:
     * 1. La data di restituzione non può essere antecedente alla data di inizio prestito.
     * 2. Se la data di restituzione è successiva alla scadenza prevista, avvisa l'amministratore del ritardo.
     * 3. Aggiorna lo stato del libro (incrementa copie disponibili) e salva l'archivio.
     */
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
            Stage stage = (Stage) lblTitolo.getScene().getWindow();
            stage.close();
        }else{
            gestore.restituisciLibro(prestito, dataRestituzione);
            gestore.salvaStato();
            mostraAlert(AlertType.INFORMATION , "RESTITUZIONE AVVENUTA CON SUCCESSO" , "Il libro è stato restituito correttamente.");
            Stage stage = (Stage) lblTitolo.getScene().getWindow();
            stage.close();
        } 
    }
    
    /**
     * @brief Pre-imposta i dati se la finestra viene aperta da una selezione specifica.
     * @param prestito Il prestito da chiudere.
     */
    public void setDatiPrestito(Prestito prestito){
        LocalDate df = LocalDate.now();
        cmbUtente.setValue(prestito.getUtente().getMatricola());
        cmbLibro.setValue(prestito.getLibro().getIsbn());
        dpFine.setValue(df);
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
