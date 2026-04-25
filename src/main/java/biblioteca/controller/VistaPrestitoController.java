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
 * FXML Controller class
 *
 * @author lpane
 */
public class VistaPrestitoController implements Initializable {
    
    private GestoreBiblioteca gestore;
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
    
    
    public VistaPrestitoController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void inizializzaDati(Prestito prestito){
        this.prestitoCorrente = prestito;
        
        lblTitoloLibroPrestato.setText(prestito.getLibro().getTitolo());
        lblAutoriLibroPrestato.setText(prestito.getLibro().getAutoriFormattati());
        lblUtenteLibroPrestato.setText(prestito.getUtente().getMatricola());
        lblDataLibroPrestato.setText(prestito.getDataFine().toString());
    }
    
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
    
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblTitoloLibroPrestato.getScene().getWindow();
        stage.close();
    }
    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }    
}