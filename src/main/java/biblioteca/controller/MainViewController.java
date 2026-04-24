/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Utente;
import biblioteca.model.Libro;
import biblioteca.model.Prestito;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author lpane
 */
public class MainViewController implements Initializable {
    
    private GestoreBiblioteca gestore;
    
    @FXML
    private Button btnLibri;
    @FXML
    private Button btnUtenti;
    @FXML
    private Button btnPrestitiAttivi;
    @FXML
    private TextField txtRicerca;
    @FXML
    private Button btnEffettuaPrestito;
    @FXML
    private Button btnEffettuaRestituzione;
    @FXML
    private TableView<Object> tabellaPrincipale;
    @FXML
    private Button btnNuovoUtente;
    @FXML
    private Button btnNuovoLibro;
    
    public MainViewController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestore.caricaDatiIniziali();
        
        tabellaPrincipale.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Object selezionato = row.getItem();

                    if (selezionato instanceof Libro) {
                        apriDettaglioLibro((Libro) selezionato);
                    }else if(selezionato instanceof Utente){
                        apriDettaglioUtente((Utente) selezionato);
                    }
                }
            });
            return row;
        });
        
        javafx.application.Platform.runLater(() -> {
            mostraLibri(null);
        });
    }    

    @FXML
    private void mostraUtenti(ActionEvent event) {
        tabellaPrincipale.getColumns().clear();
        
        TableColumn<Object , String> colMatricola = new TableColumn<>("Matricola");
        colMatricola.setCellValueFactory(new PropertyValueFactory<>("matricola"));
        colMatricola.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        TableColumn<Object , String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colNome.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        
        TableColumn<Object , String> colCognome = new TableColumn<>("Cognome");
        colCognome.setCellValueFactory(new PropertyValueFactory<>("cognome"));
        colCognome.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        
        TableColumn<Object , String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        tabellaPrincipale.getColumns().addAll(colMatricola , colNome , colCognome , colEmail);
        tabellaPrincipale.getItems().setAll(gestore.getListaUtenti());
        
        btnNuovoUtente.setText("Inserisci nuovo Utente");
        btnNuovoUtente.setVisible(true);
        btnNuovoLibro.setVisible(false);
    }

    @FXML
    private void mostraLibri(ActionEvent event) {
        tabellaPrincipale.getColumns().clear();
        
        TableColumn<Object , String> colTitolo = new TableColumn<>("Titolo");
        colTitolo.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        colTitolo.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        TableColumn<Object , String> colAutori = new TableColumn<>("Autori");
        colAutori.setCellValueFactory(new PropertyValueFactory<>("autoriFormattati"));
        colAutori.setMaxWidth(1f * Integer.MAX_VALUE * 30); 
        
        TableColumn<Object , String> colAnno = new TableColumn<>("Anno");
        colAnno.setCellValueFactory(new PropertyValueFactory<>("annoPubblicazione"));
        colAnno.setMaxWidth(1f * Integer.MAX_VALUE * 10);
        
        TableColumn<Object , String> colIsbn = new TableColumn<>("Isbn");
        colIsbn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        colIsbn.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        
        TableColumn<Object , String> colCopieTotali = new TableColumn<>("Copie Totali");
        colCopieTotali.setCellValueFactory(new PropertyValueFactory<>("numeroCopieTotali"));
        colCopieTotali.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        
        TableColumn<Object , String> colCopieDisponibili = new TableColumn<>("Copie Disponibili");
        colCopieDisponibili.setCellValueFactory(new PropertyValueFactory<>("numeroCopieDisponibili"));
        colCopieDisponibili.setMaxWidth(1f * Integer.MAX_VALUE * 5);
        
        tabellaPrincipale.getColumns().addAll(colTitolo , colAutori , colAnno , colIsbn , colCopieTotali, colCopieDisponibili);

        tabellaPrincipale.getItems().setAll(gestore.getListaLibri());
        
        btnNuovoLibro.setText("Inserisci nuovo libro");
        btnNuovoUtente.setVisible(false);
        btnNuovoLibro.setVisible(true);
        
    }

    @FXML
    private void mostraPrestitiAttivi(ActionEvent event) {
        tabellaPrincipale.getColumns().clear();
        
        TableColumn<Object , String> colMatricola = new TableColumn<>("Matricola");
        colMatricola.setCellValueFactory(cellData -> 
            new SimpleStringProperty(((Prestito)cellData.getValue()).getUtente().getMatricola())
        );
        colMatricola.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        TableColumn<Object , String> colIsbn = new TableColumn<>("Codice isbn");
        colIsbn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(((Prestito)cellData.getValue()).getLibro().getIsbn())
        );
        colIsbn.setMaxWidth(1f * Integer.MAX_VALUE * 20); 
        
        TableColumn<Object , String> colDataInizio = new TableColumn<>("Inizio prestito");
        colDataInizio.setCellValueFactory(new PropertyValueFactory<>("dataInizio"));
        colDataInizio.setMaxWidth(1f * Integer.MAX_VALUE * 20);
        
        TableColumn<Object , String> colDataFine = new TableColumn<>("Fine prestito");
        colDataFine.setCellValueFactory(new PropertyValueFactory<>("dataFine"));
        colDataFine.setMaxWidth(1f * Integer.MAX_VALUE * 30);
        
        tabellaPrincipale.getColumns().addAll(colMatricola , colIsbn , colDataInizio , colDataFine);

        tabellaPrincipale.getItems().setAll(gestore.getPrestitiOrdinati(
                Comparator.comparing(Prestito::getDataFine)
        ));
        
    }
    
    @FXML 
    private void goToInserisciUtente(ActionEvent event){
         try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/inserisciUtente3.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Inserisci Nuovo Utente");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            mostraUtenti(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    @FXML
    private void goToInserisciLibro(ActionEvent event){
            try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/inserisciLibro2.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Inserisci Nuovo Libro");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            mostraLibri(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void goToRegistraPrestito(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/registraPrestito.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Registra nuovo prestito");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

            mostraLibri(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void goToRegistraRestituzione(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/registraRestituzione2.fxml"));
            Parent root = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Registra restituzione");

            popupStage.initModality(Modality.APPLICATION_MODAL);

            popupStage.setScene(new Scene(root));
            popupStage.showAndWait(); 

            mostraLibri(null);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void apriDettaglioLibro(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/vistaLibro.fxml"));
            Parent root = loader.load();

            VistaLibroController controller = loader.getController();

            controller.inizializzaDati(libro);

            Stage stage = new Stage();
            stage.setTitle("Dettaglio Libro - " + libro.getTitolo());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            mostraLibri(null);

        } catch (IOException e) {
            e.printStackTrace();
            mostraAlert(AlertType.ERROR, "Errore Caricamento", "Impossibile aprire la vista dettagliata.");
        }
    }
    
    private void apriDettaglioUtente(Utente utente){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/vistaUtente.fxml"));
            Parent root = loader.load();
            
            VistaUtenteController controller = loader.getController();
            
            controller.inizializzaDati(utente);
            
            Stage stage = new Stage();
            stage.setTitle("Dettaglio Utente - " + utente.getNome() + utente.getCognome());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            tabellaPrincipale.getItems().setAll(gestore.getListaUtenti());

            
        }catch(IOException e){
            e.printStackTrace();
            mostraAlert(AlertType.ERROR , "Errore Caricamento" , "Impossibile aprire la vista dettagliata.");
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
