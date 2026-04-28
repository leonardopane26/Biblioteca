package biblioteca.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * @class App
 * @brief Classe principale dell'applicazione (Entry Point).
 * @details Estende la classe `javafx.application.Application` e gestisce l'avvio 
 * del framework JavaFX, l'impostazione dello Stage principale e il caricamento 
 * della prima scena (Main View).
 * @author lpane
 * @date 2026-04-14
 */
public class App extends Application {
    
    /** @brief Scena principale dell'applicazione. */
    private static Scene scene;
    
    /**
     * @brief Metodo di avvio del ciclo di vita JavaFX.
     * @param stage Lo stage primario (finestra) fornito dal sistema.
     * @throws Exception se il file FXML della vista principale non viene trovato.
     * @details Il metodo carica il file `mainView.fxml`, inizializza la scena e 
     * rende visibile la finestra principale dell'applicazione.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML della schermata di inserimento
        Parent root = FXMLLoader.load(getClass().getResource("/biblioteca/mainView.fxml"));
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    /**
     * @brief Cambia dinamicamente la vista nella scena principale.
     * @param fxml Nome del file FXML da caricare.
     * @throws IOException in caso di errore nel caricamento del file.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    
    /**
     * @brief Utility per il caricamento dei file FXML.
     * @param fxml Nome del file.
     * @return Il nodo Parent caricato.
     * @throws IOException se il risorsa non è disponibile.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    
    /**
     * @brief Main method classico di Java.
     * @param args Argomenti da riga di comando.
     * @details Invoca il metodo `launch()` di JavaFX per far partire l'applicazione.
     */
    public static void main(String[] args) {
        launch();
    }

}