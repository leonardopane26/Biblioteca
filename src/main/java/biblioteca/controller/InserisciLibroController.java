/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Libro;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author lpane
 */
public class InserisciLibroController implements Initializable {
    
    private GestoreBiblioteca gestore;
    private Libro libroInModifica = null;
    
    @FXML
    private TextField txtTitolo;
    @FXML
    private TextField txtAutori;
    @FXML
    private TextField txtAnno;
    @FXML
    private TextField txtIsbn;
    @FXML 
    private TextField txtCopieTotali;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnConferma;
    @FXML
    private Label lblCopieTotali; 
    
    public InserisciLibroController(){
        this.gestore = new GestoreBiblioteca();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void annullaInserimento(ActionEvent event) {
        Stage stage = (Stage) txtTitolo.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void confermaInserimento(ActionEvent event) {
        String titolo = txtTitolo.getText();
        String autori = txtAutori.getText().trim();
        String annoStr = txtAnno.getText();
        String isbn = txtIsbn.getText();
        String copie = txtCopieTotali.getText(); 
        
        if (titolo.isEmpty() || autori.isEmpty() || annoStr.isEmpty() || isbn.isEmpty()) {
            mostraAlert(AlertType.ERROR, "ERRORE", "Compila tutti i campi obbligatori");
            return;
        }

        try {            
            int anno = Integer.parseInt(annoStr);
            int nuoveCopieTotali;

            if (libroInModifica != null) {
                nuoveCopieTotali = Integer.parseInt(copie);
            } else {
                nuoveCopieTotali = 1;
            }

            if (!isbn.matches("[0-9]+")) {
                mostraAlert(AlertType.ERROR, "ERRORE", "L'ISBN deve essere numerico");
                return;
            }

            if (libroInModifica != null) {
                int inPrestito = libroInModifica.getNumeroCopieTotali() - libroInModifica.getNumeroCopieDisponibili();

                if (nuoveCopieTotali < inPrestito) {
                    mostraAlert(AlertType.ERROR, "ERRORE COPIE", "Non puoi scendere sotto le " + inPrestito + " copie in prestito");
                    return;
                }

                libroInModifica.setTitolo(titolo);
                libroInModifica.setAnnoPubblicazione(anno);
                libroInModifica.setAutori(Arrays.asList(autori.split(";")));

                int diff = nuoveCopieTotali - libroInModifica.getNumeroCopieTotali();
                libroInModifica.setNumeroCopieTotali(nuoveCopieTotali);
                libroInModifica.setNumeroCopieDisponibili(libroInModifica.getNumeroCopieDisponibili() + diff);

                mostraAlert(AlertType.INFORMATION, "SUCCESSO", "Libro modificato con successo");

            } else {
                if (gestore.cercaLibroPerISBN(isbn) != null) {
                    mostraAlert(AlertType.ERROR, "ERRORE", "Esiste già un libro con questo ISBN");
                    return;
                }

                List<String> listaAutori = Arrays.asList(autori.split(";"));
                Libro nuovo = new Libro(titolo, listaAutori, anno, isbn, nuoveCopieTotali);
                nuovo.setNumeroCopieDisponibili(nuoveCopieTotali);

                gestore.aggiungiLibro(nuovo);
                mostraAlert(AlertType.INFORMATION, "SUCCESSO", "Libro inserito con successo");
            }
            gestore.salvaStato();
            Stage stage = (Stage) txtTitolo.getScene().getWindow();
            stage.close();

        } catch (NumberFormatException e) {
            mostraAlert(AlertType.ERROR, "ERRORE", "L'anno deve essere in formato numerico.");
        }
    }
    
    public void setLibroDaModificare(Libro libro) {
        this.libroInModifica = libro;
        lblCopieTotali.setVisible(true);
        txtCopieTotali.setVisible(true);
        
        txtTitolo.setText(libro.getTitolo());
        txtAutori.setText(String.join(", ", libro.getAutori()));
        txtAnno.setText(String.valueOf(libro.getAnnoPubblicazione()));
        txtIsbn.setText(libro.getIsbn());
        txtCopieTotali.setText(String.valueOf(libro.getNumeroCopieTotali()));
        txtIsbn.setEditable(false); 

        txtCopieTotali.setText(String.valueOf(libro.getNumeroCopieTotali()));
    }
    
    private void mostraAlert(AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
}
