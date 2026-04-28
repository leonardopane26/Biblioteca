/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Prestito;
import biblioteca.model.Utente;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * @class VistaUtenteController
 * @brief Controller per la visualizzazione dettagliata del profilo di un utente.
 * @details Gestisce la schermata di riepilogo che mostra i dati anagrafici di un utente 
 * e la tabella dei libri attualmente in suo possesso. Include la logica per la 
 * cancellazione sicura (inibita se ci sono prestiti attivi) e l'accesso alla modifica.
 * @author lpane
 * @date 2026-04-18
 */
public class VistaUtenteController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    /** @brief L'utente di cui si stanno visualizzando i dettagli. */
    private Utente utenteCorrente;
    
    @FXML
    private Button btnIndietro;
    @FXML
    private Label lblNomeUtente;
    @FXML
    private Label lblCognomeUtente;
    @FXML
    private Label lblMatricolaUtente;
    @FXML
    private Label lblEmailUtente;
    @FXML
    private TableView<Prestito> tabellaPrestitiUtente;
    @FXML
    private TableColumn<Prestito, String> colTitoloLibro;
    @FXML
    private TableColumn<Prestito, String> colCodiceIsbn;
    @FXML
    private TableColumn<Prestito, String> colDataInizio;
    @FXML
    private TableColumn<Prestito, String> colDataScadenza;
    @FXML
    private Button btnElimina;
    @FXML
    private Button btnModifica;
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public VistaUtenteController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Inizializza la tabella dei prestiti utente.
     * @details Configura le colonne per estrarre i dati dal modello `Libro` 
     * annidato nell'oggetto `Prestito` tramite `SimpleStringProperty`.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colTitoloLibro.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getLibro().getTitolo())
        );
        
        colCodiceIsbn.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getLibro().getIsbn())
        );
        
        colDataInizio.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));
        colDataScadenza.setCellValueFactory(new PropertyValueFactory<>("dataFine"));
    }    
    
    /**
     * @brief Carica i dati dell'utente nell'interfaccia.
     * @param utente L'istanza dell'utente da visualizzare.
     * @details Popola le label informative e interroga il gestore per ottenere 
     * solo i prestiti attivi legati alla matricola dell'utente.
     */
    public void inizializzaDati(Utente utente){
        this.utenteCorrente = utente;
        
        lblNomeUtente.setText(utente.getNome());
        lblCognomeUtente.setText(utente.getCognome());
        lblMatricolaUtente.setText(utente.getMatricola());
        lblEmailUtente.setText(utente.getEmail());
        
        List<Prestito> libri = gestore.filtraPrestitUtente(utente);
        tabellaPrestitiUtente.getItems().setAll(libri);
    }
    
    /**
     * @brief Chiude lo stage corrente.
     */
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblMatricolaUtente.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Gestisce il processo di rimozione di un utente.
     * @param event Pressione del tasto elimina.
     * @details Viene richiesta una conferma esplicita. Se l'utente ha ancora 
     * dei libri in prestito (lista prestiti non vuota), l'operazione viene 
     * bloccata con un messaggio di errore per garantire l'integrità del sistema.
     */
    @FXML
    private void eliminaUtente(ActionEvent event){
 
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare: " + utenteCorrente.getNome() + " " + utenteCorrente.getCognome() + " " + utenteCorrente.getMatricola());
        alert.setContentText("Sei sicuro di voler procedere? L'azione è irreversibile.");

        Optional<ButtonType> risultato = alert.showAndWait();
        
        if(risultato.isPresent() && risultato.get() == ButtonType.OK){
            if(!gestore.filtraPrestitUtente(utenteCorrente).isEmpty()){
                mostraAlert(AlertType.ERROR , "Impossibile eliminare", "L'utente selezionato deve ancora restituire dei volumi.");
                return;
            }
            gestore.rimuoviUtente(utenteCorrente.getMatricola());
            gestore.salvaStato();
            
            mostraAlert(AlertType.INFORMATION , "Utente rimosso" , "Eliminazione avvenuta con successo");
            Stage stage = (Stage) lblMatricolaUtente.getScene().getWindow();
            stage.close();
        }else{
            Stage stage = (Stage) lblMatricolaUtente.getScene().getWindow();
            stage.close();
        }
        
    }
    
    /**
     * @brief Apre la finestra di modifica dell'anagrafica utente.
     * @param event Pressione del tasto modifica.
     * @details Carica il controller `InserisciUtenteController` e passa 
     * l'oggetto utente corrente per popolare i campi. Al ritorno, 
     * aggiorna i dati visualizzati nella vista dettaglio.
     */
    @FXML
    private void modificaUtente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/inserisciUtente3.fxml"));
            Parent root = loader.load();

            InserisciUtenteController controller = loader.getController();

            controller.setUtenteDaModificare(utenteCorrente);

            Stage stage = new Stage();
            stage.setTitle("Modifica Utente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            inizializzaDati(utenteCorrente); 
        } catch (IOException e) {
            e.printStackTrace();
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
