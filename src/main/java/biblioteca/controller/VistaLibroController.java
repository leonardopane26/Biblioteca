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
 * FXML Controller class
 *
 * @author lpane
 */
public class VistaLibroController implements Initializable {
    
    private GestoreBiblioteca gestore;
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
    
    public VistaLibroController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
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
    
    @FXML
    private void goIndietro(){
        Stage stage = (Stage) lblTitoloLibro.getScene().getWindow();
        stage.close();
    }
    
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

    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}
