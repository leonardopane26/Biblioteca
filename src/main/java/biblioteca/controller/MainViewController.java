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
import biblioteca.utils.LibroComparator;
import biblioteca.utils.PrestitoComparator;
import biblioteca.utils.UtenteComparator;
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
 * @class MainViewController
 * @brief Controller principale dell'applicazione.
 * @details Gestisce la finestra principale della biblioteca. Si occupa della visualizzazione 
 * dinamica delle tabelle (Libri, Utenti, Prestiti), della ricerca globale e dell'apertura 
 * delle finestre per l'inserimento o la visualizzazione dei dettagli.
 * @author lpane
 * @date 2026-04-18
 */
public class MainViewController implements Initializable {
    
    /** @brief Riferimento al gestore della biblioteca. */
    private GestoreBiblioteca gestore;
    /** @brief Stato attuale della tabella (può essere "LIBRI", "UTENTI" o "PRESTITI"). */
    private String vistaCorrente = "LIBRI";
    
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
    
    /**
     * @brief Costruttore del controller.
     * @details Istanzia il GestoreBiblioteca per interfacciarsi con i dati.
     */
    public MainViewController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * @brief Metodo di inizializzazione di JavaFX.
     * @details Carica i dati iniziali dall'archivio, configura la RowFactory per gestire 
     * il colore dei ritardi (rosso) e il doppio click, e imposta il listener per la ricerca.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        gestore.caricaDatiIniziali();
        
        tabellaPrincipale.setRowFactory(tv -> {
            TableRow<Object> row = new TableRow<Object>(){
                @Override
                protected void updateItem(Object item, boolean empty){
                    super.updateItem(item, empty);
                    
                    setStyle("");
                    
                    if(item instanceof Prestito && !empty){
                        Prestito p = (Prestito) item;
                        
                        if(!p.isRestituito() && p.getDataFine().isBefore(java.time.LocalDate.now())){
                            setStyle("-fx-background-color: #f28d8d;");
                        }
                    }
                }
            };
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Object selezionato = row.getItem();

                    if (selezionato instanceof Libro) {
                        apriDettaglioLibro((Libro) selezionato);
                    }else if(selezionato instanceof Utente){
                        apriDettaglioUtente((Utente) selezionato);
                    }else if(selezionato instanceof Prestito){
                        apriDettaglioPrestito((Prestito) selezionato);
                    }
                }
            });
            return row;
        });
        
        txtRicerca.textProperty().addListener((obs, vecchioValore, nuovoValore) -> {
            filtraTabella(nuovoValore.toLowerCase());
        });
        
        javafx.application.Platform.runLater(() -> {
            mostraLibri(null);
        });
        
       
    }    
    
    /**
     * @brief Configura la tabella per mostrare la lista degli utenti.
     * @param event Evento scatenato dal click sul pulsante Utenti.
     */
    @FXML
    private void mostraUtenti(ActionEvent event) {
        vistaCorrente = "UTENTI";
        txtRicerca.clear();
        
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
        tabellaPrincipale.getItems().setAll(gestore.getUtentiOrdinati(new UtenteComparator()));
        
        btnNuovoUtente.setText("Inserisci nuovo Utente");
        btnNuovoUtente.setVisible(true);
        btnNuovoLibro.setVisible(false);
    }
    
    /**
     * @brief Configura la tabella per mostrare il catalogo libri.
     * @param event Evento scatenato dal click sul pulsante Libri.
     */
    @FXML
    private void mostraLibri(ActionEvent event) {
        vistaCorrente = "LIBRI";
        txtRicerca.clear();
        
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

        tabellaPrincipale.getItems().setAll(gestore.getCatalogoOrdinato(new LibroComparator()));
        
        btnNuovoLibro.setText("Inserisci nuovo libro");
        btnNuovoUtente.setVisible(false);
        btnNuovoLibro.setVisible(true);
        
    }
    
    /**
     * @brief Configura la tabella per mostrare i prestiti correnti.
     * @param event Evento click sul pulsante Prestiti Attivi.
     */
    @FXML
    private void mostraPrestitiAttivi(ActionEvent event) {
        vistaCorrente = "PRESTITI";
        txtRicerca.clear();
        
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

        tabellaPrincipale.getItems().setAll(gestore.getPrestitiOrdinati(new PrestitoComparator()));
        
        btnNuovoUtente.setVisible(false);
        btnNuovoLibro.setVisible(false);
    }
    
    /** @brief Apre il popup per l'inserimento di un utente. */
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
    
    /** @brief Apre il popup per l'inserimento di un libro. */
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
    
    /** @brief Apre il popup per registrare un prestito. */
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
    
    /** @brief Apre il popup per registrare una restituzione. */
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
    
    /**
     * @brief Gestisce il filtraggio in tempo reale dei dati nelle tabelle.
     * @param query Stringa inserita dall'utente nella barra di ricerca.
     */
    private void filtraTabella(String query) {
        if (query == null || query.isEmpty()) {
            if (vistaCorrente.equals("LIBRI")) tabellaPrincipale.getItems().setAll(gestore.getListaLibri());
            else if (vistaCorrente.equals("UTENTI")) tabellaPrincipale.getItems().setAll(gestore.getListaUtenti());
            else tabellaPrincipale.getItems().setAll(gestore.getListaPrestiti());
            return;
        }

        switch (vistaCorrente) {
            case "LIBRI":
                List<Libro> libriFiltrati = gestore.getListaLibri().stream()
                    .filter(l -> l.getTitolo().toLowerCase().contains(query) || 
                                 l.getIsbn().contains(query) ||
                                 l.getAutoriFormattati().toLowerCase().contains(query))
                    .collect(java.util.stream.Collectors.toList());
                tabellaPrincipale.getItems().setAll(libriFiltrati);
                break;

            case "UTENTI":
                List<Utente> utentiFiltrati = gestore.getListaUtenti().stream()
                    .filter(u -> u.getNome().toLowerCase().contains(query) || 
                                 u.getCognome().toLowerCase().contains(query) || 
                                 u.getMatricola().contains(query))
                    .collect(java.util.stream.Collectors.toList());
                tabellaPrincipale.getItems().setAll(utentiFiltrati);
                break;

            case "PRESTITI":
                List<Prestito> prestitiFiltrati = gestore.getListaPrestiti().stream()
                    .filter(p -> p.getUtente().getCognome().toLowerCase().contains(query) || 
                                 p.getLibro().getTitolo().toLowerCase().contains(query) ||
                                 p.getUtente().getMatricola().contains(query))
                    .collect(java.util.stream.Collectors.toList());
                tabellaPrincipale.getItems().setAll(prestitiFiltrati);
                break;
        }
    }
    
    /** @brief Apre la vista dettagliata di un libro. */
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
    
    /** @brief Apre la vista dettagliata di un utente. */
    private void apriDettaglioUtente(Utente utente){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/vistaUtente.fxml"));
            Parent root = loader.load();
            
            VistaUtenteController controller = loader.getController();
            controller.inizializzaDati(utente);
            
            Stage stage = new Stage();
            stage.setTitle("Dettaglio Utente - " + utente.getNome() + " " + utente.getCognome());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            tabellaPrincipale.getItems().setAll(gestore.getListaUtenti());

            
        }catch(IOException e){
            e.printStackTrace();
            mostraAlert(AlertType.ERROR , "Errore Caricamento" , "Impossibile aprire la vista dettagliata.");
        }
    }
    
    /** @brief Apre la vista dettagliata di un prestito. */
    private void apriDettaglioPrestito(Prestito prestito){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/biblioteca/vistaPrestito.fxml"));
            Parent root = loader.load();
            
            VistaPrestitoController controller = loader.getController();
            controller.inizializzaDati(prestito);
            
            Stage stage = new Stage();
            stage.setTitle("Dettaglio Prestito - " + prestito.getLibro().getTitolo() + " " + prestito.getLibro().getIsbn());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            tabellaPrincipale.getItems().setAll(gestore.getListaPrestiti());
            
        }catch(IOException e){
            e.printStackTrace();
            mostraAlert(AlertType.ERROR , "Errore Caricamento" , "Impossibile aprire la vista dettagliata.");
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
