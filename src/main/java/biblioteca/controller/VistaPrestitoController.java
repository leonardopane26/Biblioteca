/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Prestito;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @class VistaPrestitoController
 * @brief Controller per la visualizzazione dei dettagli di un prestito specifico.
 * @details Questa classe gestisce la finestra di riepilogo di un prestito selezionato. 
 * Mostra le informazioni incrociate tra Libro e Utente e permette di avviare il 
 * processo di restituzione caricando il relativo controller secondario.
 * @author lpane
 * @date 2026-04-18
 */
public class VistaPrestitoController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    /** @brief Il prestito correntemente visualizzato. */
    private Prestito prestitoCorrente = null;
    
    @FXML
    private Label lblTitoloLibroPrestato;
    @FXML
    private Label lblAutoriLibroPrestato;
    @FXML
    private Label lblUtenteLibroPrestato;
    @FXML
    private Label lblDataLibroPrestato;
    @FXML
    private Button btnIndietro;
    @FXML
    private Button btnRestituisci;
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public VistaPrestitoController(){
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
     * @brief Inserisce i dati del prestito nei componenti grafici.
     * @param prestito L'oggetto Prestito da cui estrarre le informazioni.
     * @details Popola le label della vista con il titolo del libro, gli autori, 
     * la matricola dell'utente e la data di scadenza prevista.
     */
    public void inizializzaDati(Prestito prestito){
        this.prestitoCorrente = prestito;
        
        lblTitoloLibroPrestato.setText(prestito.getLibro().getTitolo());
        lblAutoriLibroPrestato.setText(prestito.getLibro().getAutoriFormattati());
        lblUtenteLibroPrestato.setText(prestito.getUtente().getMatricola());
        lblDataLibroPrestato.setText(prestito.getDataFine().toString());
    }
    
    /**
     * @brief Apre il modulo per registrare la restituzione del libro.
     * @param event Evento scatenato dal click sul tasto "Restituisci".
     * @details Carica il file FXML `registraRestituzione2.fxml` e passa l'oggetto 
     * `prestitoCorrente` al controller `RegistraRestituzioneController`. 
     * La finestra viene aperta in modalità `APPLICATION_MODAL` per bloccare 
     * l'interazione con le finestre sottostanti fino alla chiusura del modulo.
     */
    @FXML
    private void apriFinestraRestituzione(ActionEvent event) {
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/registraRestituzione2.fxml"));
            Parent root = loader.load();

            RegistraRestituzioneController controller = loader.getController();
            controller.setDatiPrestito(prestitoCorrente); 

            Stage stage = new Stage();
            stage.setTitle("Registra Restituzione");

            stage.initModality(Modality.APPLICATION_MODAL); 

            stage.setScene(new Scene(root));
            stage.showAndWait(); 

        } catch (IOException e) {
            e.printStackTrace();
            mostraAlert(AlertType.ERROR, "Errore", "Impossibile caricare la finestra di restituzione.");
        }
    }
    
    /**
     * @brief Chiude la finestra di dettaglio.
     */
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblTitoloLibroPrestato.getScene().getWindow();
        stage.close();
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