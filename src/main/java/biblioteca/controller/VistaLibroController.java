/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.Archivio;
import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Libro;
import biblioteca.model.Prestito;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
 * @class VistaLibroController
 * @brief Controller per la visualizzazione dettagliata di un singolo libro.
 * @details Questa classe gestisce una vista di approfondimento che mostra i dettagli 
 * completi di un libro (ISBN, autori, copie) e una tabella dei prestiti attivi 
 * relativi esclusivamente a quel volume. Permette inoltre l'avvio della modifica o 
 * l'eliminazione del libro previa verifica dei vincoli.
 * @author lpane
 * @date 2026-04-18
 */
public class VistaLibroController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    /** @brief Il libro attualmente visualizzato. */
    private Libro libroCorrente;
    
    @FXML
    private Button btnIndietro;
    @FXML
    private Label lblTitoloLibro;
    @FXML
    private Label lblAutoriLibro;
    @FXML
    private Label lblCodiceIsbn;
    @FXML
    private Label lblCopieDisponibili;
    @FXML
    private Label lblCopieTotali;
    @FXML
    private TableView<Prestito> tabellaLibroPrestato;
    @FXML
    private TableColumn<Prestito, String> colNomeUtente;
    @FXML
    private TableColumn<Prestito, String> colCognomeUtente;
    @FXML
    private TableColumn<Prestito, String> colMatricolaUtente;
    @FXML
    private TableColumn<Prestito, LocalDate> colDataInizio;
    @FXML
    private TableColumn<Prestito, LocalDate> colDataFine;
    @FXML
    private Button btnElimina;
    @FXML
    private Button btnModifica;
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public VistaLibroController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Inizializza la struttura della tabella dei prestiti.
     * @details Configura le colonne della tabella utilizzando sia PropertyValueFactory 
     * che SimpleStringProperty per accedere ai dati annidati dell'oggetto Utente 
     * associato al Prestito.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colNomeUtente.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUtente().getNome())
        );

        colCognomeUtente.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUtente().getCognome())
        );

        colMatricolaUtente.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUtente().getMatricola())
        );

        colDataInizio.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));
        colDataFine.setCellValueFactory(new PropertyValueFactory<>("dataFine"));
    }    
    
    /**
     * @brief Popola l'interfaccia con i dati del libro selezionato.
     * @param libro L'istanza del libro da visualizzare.
     * @details Il metodo aggiorna le label testuali e filtra la lista globale dei 
     * prestiti per mostrare nella tabella solo gli utenti che hanno attualmente 
     * in mano una copia di questo specifico libro (tramite confronto ISBN).
     */
    public void inizializzaDati(Libro libro){
        this.libroCorrente = libro;
        
        lblTitoloLibro.setText(libro.getTitolo());
        lblAutoriLibro.setText(libro.getAutoriFormattati());
        lblCodiceIsbn.setText(libro.getIsbn());
        lblCopieDisponibili.setText(String.valueOf(libro.getNumeroCopieDisponibili()));
        lblCopieTotali.setText(String.valueOf(libro.getNumeroCopieTotali()));
        
        List<Prestito> possessori = gestore.getListaPrestiti().stream()
                .filter(p -> p.getLibro().getIsbn().equals(libro.getIsbn()))
                .collect(Collectors.toList());
        System.out.println("Prestiti trovati per questo libro: " + possessori.size());
        tabellaLibroPrestato.getItems().setAll(possessori);
    }
    
    /**
     * @brief Chiude la vista di dettaglio e torna alla schermata precedente.
     */
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblTitoloLibro.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Gestisce l'eliminazione del libro dal sistema.
     * @param event Evento scatenato dal click del tasto elimina.
     * @details L'eliminazione è protetta da due livelli di controllo:
     * 1. **Conferma Utente**: Un alert di tipo CONFIRMATION richiede l'autorizzazione.
     * 2. **Integrità Dati**: Il libro non può essere eliminato se il numero di copie 
     * disponibili è inferiore al totale (ovvero se ci sono prestiti ancora aperti).
     */
    @FXML
    private void eliminaLibro(ActionEvent event){
 
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Conferma Eliminazione");
        alert.setHeaderText("Stai per eliminare: " + libroCorrente.getTitolo());
        alert.setContentText("Sei sicuro di voler procedere? L'azione è irreversibile.");

        Optional<ButtonType> risultato = alert.showAndWait();
        
        if(risultato.isPresent() && risultato.get() == ButtonType.OK){
            if(libroCorrente.getNumeroCopieDisponibili() < libroCorrente.getNumeroCopieTotali()){
                mostraAlert(AlertType.ERROR , "Impossibile eliminare", "Ci sono ancora copie in prestito. Attendi la restituzione di tutti i volumi.");
                return;
            }
            gestore.rimuoviLibro(libroCorrente.getIsbn());
            gestore.salvaStato();
            
            mostraAlert(AlertType.INFORMATION , "Libro eliminato" , "Eliminazione avvenuta con successo");
            Stage stage = (Stage) lblTitoloLibro.getScene().getWindow();
            stage.close();
        }else{
            Stage stage = (Stage) lblTitoloLibro.getScene().getWindow();
            stage.close();
        }
        
    }
    
    /**
     * @brief Apre la finestra di modifica per il libro corrente.
     * @param event Evento scatenato dal click del tasto modifica.
     * @details Carica il loader FXML di `InserisciLibroController` in modalità 
     * modale, passa il libro corrente per la pre-compilazione dei campi e 
     * al termine dell'operazione aggiorna la vista con i nuovi dati.
     */
    @FXML
    private void modificaLibro(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/inserisciLibro2.fxml"));
            Parent root = loader.load();

            InserisciLibroController controller = loader.getController();

            controller.setLibroDaModificare(libroCorrente);

            Stage stage = new Stage();
            stage.setTitle("Modifica Libro");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            inizializzaDati(libroCorrente); 

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
