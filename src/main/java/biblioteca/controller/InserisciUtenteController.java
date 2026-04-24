/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controller;

import biblioteca.model.GestoreBiblioteca;
import biblioteca.model.Utente;
import java.net.URL;
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
public class InserisciUtenteController implements Initializable {
    
    private GestoreBiblioteca gestore;
    private Utente utenteInModifica = null;
    
    @FXML
    private Label lblTitolo;
    @FXML
    private TextField txtNome;
    @FXML
    private TextField txtCognome;
    @FXML
    private TextField txtMatricola;
    @FXML
    private TextField txtEmail;
    @FXML
    private Button btnAnnulla;
    @FXML
    private Button btnConferma;
    
    
    public InserisciUtenteController(){
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
    private void annullaInserimento(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void confermaInserimento(ActionEvent event){
        String nome = txtNome.getText();
        String cognome = txtCognome.getText();
        String matricola = txtMatricola.getText();
        String email = txtEmail.getText();
        
        if(nome.isEmpty() || cognome.isEmpty() || matricola.isEmpty() || email.isEmpty()){
            mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "Tutti i campi devono essere riempiti correttamente.");
            return;
        }else if(!isValid(email)){
            mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "L'email deve essere in un formato valido.");
            return;
        }else if(!matricola.matches("[0-9]+")){
            mostraAlert(AlertType.ERROR , "INSERIMENTO FALLITO" , "La matricola deve essere in formato numerico.");
            return;
        }
        
        if(utenteInModifica != null) {
        if(!utenteInModifica.getMatricola().equals(matricola) && gestore.cercaUtentePerMatricola(matricola) != null) {
            mostraAlert(AlertType.ERROR, "ERRORE", "La nuova matricola è già assegnata ad un altro utente.");
            return;
        }

        utenteInModifica.setNome(nome);
        utenteInModifica.setCognome(cognome);
        utenteInModifica.setMatricola(matricola);
        utenteInModifica.setEmail(email);

        gestore.salvaStato();
        mostraAlert(AlertType.INFORMATION, "MODIFICA COMPLETATA", "Dati utente aggiornati con successo.");
        
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
        
        }else if(gestore.cercaUtentePerMatricola(matricola)!= null){
            mostraAlert(AlertType.INFORMATION , "UTENTE ESISTENTE" , "L'utente è già presente nel sistema.");
        }else{
            Utente u = new Utente(nome , cognome , matricola , email);
            gestore.registraUtente(u);
            gestore.salvaStato();
            mostraAlert(AlertType.CONFIRMATION , "INSERIMENTO AVVENUTO CON SUCCESSO" , "L'utente è stato registrato correttamente.");
        }
        
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
        
    }
    
    public void setUtenteDaModificare(Utente utente) {
        this.utenteInModifica = utente;
        
        txtNome.setText(utente.getNome());
        txtCognome.setText(utente.getCognome());
        txtMatricola.setText(utente.getMatricola());
        txtEmail.setText(utente.getEmail());
        txtMatricola.setEditable(false);
    }
    
    private void mostraAlert(Alert.AlertType tipo, String titolo, String contenuto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(contenuto);
        alert.showAndWait();
    }
    
    public boolean isValid(String email){
	String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
	java.util.regex.Matcher m = p.matcher(email);
	return m.matches();
    }
    
}
