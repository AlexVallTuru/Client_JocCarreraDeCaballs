package vista;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.control.Button;

public class MainController {

    @FXML
    private Button salirButton;

    @FXML
    private void jugarButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("juego.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            //JuegoController juegoController = loader.getController();
            //juegoController.setStage(stage); // Establecer el Stage en el JuegoController
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void salirButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // Obtener la ventana actual y cerrarla
        Stage currentStage = (Stage) salirButton.getScene().getWindow();
        currentStage.close();
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
