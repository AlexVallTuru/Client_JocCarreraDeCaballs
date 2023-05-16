package vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {

    private Stage stage; // Referencia al Stage actual

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void jugarButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("juego.fxml"));
            Parent root = loader.load();
            
            //JuegoController juegoController = loader.getController();
            //juegoController.setStage(stage); // Establecer el Stage en el JuegoController
            
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void salirButtonAction(ActionEvent event) {
        // Lógica para cerrar la aplicación
    }

    @FXML
    private void dificultadNormalCheckBoxAction(ActionEvent event) {
        // Lógica para establecer la dificultad normal
    }

    @FXML
    private void dificultadDificilCheckBoxAction(ActionEvent event) {
        // Lógica para establecer la dificultad difícil
    }
}