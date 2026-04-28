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
 * @class InserisciUtenteController
 * @brief Controller per la gestione dell'anagrafica utenti.
 * @details Gestisce l'interfaccia FXML per la registrazione di nuovi utenti o la modifica 
 * di quelli esistenti. Include una validazione rigorosa dei campi, in particolare per 
 * la matricola (solo numerica) e l'indirizzo email (tramite Regex).
 * @author lpane
 * @date 2026-04-18
 */
public class InserisciUtenteController implements Initializable {
    
    /** @brief Riferimento al gestore della logica di business. */
    private GestoreBiblioteca gestore;
    /** @brief Riferimento all'utente se la finestra è aperta in modalità "Modifica". */
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
    
    /**
     * @brief Costruttore del controller.
     * @details Inizializza l'istanza del GestoreBiblioteca.
     */
    public InserisciUtenteController(){
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
     * @brief Chiude il popup senza salvare i dati.
     * @param event Click sul pulsante annulla.
     */
    @FXML
    private void annullaInserimento(ActionEvent event){
        Stage stage = (Stage) lblTitolo.getScene().getWindow();
        stage.close();
    }
    
    /**
     * @brief Valida i dati inseriti e procede al salvataggio o all'aggiornamento.
     * @param event Click sul pulsante conferma.
     * @details Il metodo esegue diverse fasi di controllo:
     * 1. **Completezza**: verifica che nessun campo sia vuoto.
     * 2. **Sintassi**: controlla la validità dell'email e che la matricola sia numerica.
     * 3. **Unicità**: in modalità inserimento, controlla che la matricola non sia già presente.
     * 4. **Persistenza**: salva lo stato sul file CSV tramite il gestore.
     */
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
    
    /**
     * @brief Pre-compila i campi per la modifica di un utente esistente.
     * @param utente L'oggetto Utente da modificare.
     * @details Imposta il campo matricola come non editabile per preservare l'integrità dei riferimenti.
     */
    public void setUtenteDaModificare(Utente utente) {
        this.utenteInModifica = utente;
        
        txtNome.setText(utente.getNome());
        txtCognome.setText(utente.getCognome());
        txtMatricola.setText(utente.getMatricola());
        txtEmail.setText(utente.getEmail());
        txtMatricola.setEditable(false);
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
    
    /**
     * @brief Verifica la validità sintattica di un indirizzo email.
     * @param email La stringa da controllare.
     * @return true se l'email rispetta lo standard, false altrimenti.
     * @details Utilizza una Regular Expression (Pattern e Matcher) per il controllo.
     */
    public boolean isValid(String email){
	String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
	java.util.regex.Matcher m = p.matcher(email);
	return m.matches();
    }
    
}
