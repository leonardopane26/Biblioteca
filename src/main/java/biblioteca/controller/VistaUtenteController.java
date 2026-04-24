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
 * FXML Controller class
 *
 * @author lpane
 */
public class VistaUtenteController implements Initializable {
    
    private GestoreBiblioteca gestore;
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
    
    public VistaUtenteController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
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
    
    public void inizializzaDati(Utente utente){
        this.utenteCorrente = utente;
        
        lblNomeUtente.setText(utente.getNome());
        lblCognomeUtente.setText(utente.getCognome());
        lblMatricolaUtente.setText(utente.getMatricola());
        lblEmailUtente.setText(utente.getEmail());
        
        List<Prestito> libri = gestore.filtraPrestitUtente(utente);
        tabellaPrestitiUtente.getItems().setAll(libri);
    }
    
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblMatricolaUtente.getScene().getWindow();
        stage.close();
    }
    
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

    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
    
}
