package vista;

import common.Lookups;
import common.PartidaException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javax.naming.NamingException;
import static vista.LoginController.user;
import static vista.MainController.partida;

public class PartidaJuegoController implements Initializable {

    int idPartida;

    @FXML
    private ImageView cartaImageView;

    @FXML
    private Button pedirCartaButton;

    @FXML
    private Label puntuacionLabel;

    private int puntuacionActual = 0;

    private String email;
    private String nickname;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Image cartaImage = new Image(getClass().getResourceAsStream("caballo.jpeg"));
        cartaImageView.setImage(cartaImage);

        try {
            /**
             * Obtenemos la instancia remota de partidaEJBRemoteLookup
             */
            partida = Lookups.partidaEJBRemoteLookup();
        } catch (NamingException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error al obtener la instancia de partidaEJBRemoteLookup", ex);
        }
    }

    @FXML
    private void pedirCarta() throws PartidaException, IOException {
        // Lógica para obtener una nueva carta y actualizar la puntuación
        // Por ahora, simplemente incrementaremos la puntuación actual en 1
        puntuacionActual++;
        puntuacionLabel.setText("Puntuación actual: " + puntuacionActual);

        if (puntuacionActual == 10) {

            System.out.print("\nIDPARTIDA: " + idPartida + " PUNTUACIONACTUAL: " + puntuacionActual + "\n");

            partida.añadirPuntosPartida(idPartida, puntuacionActual);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();

            //Se tiene pasar otra vez el mail y el usuario para que no se pierda en siguientes patidas.
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.show();

            // Obtener el controlador de la escena principal
            MainController mainController = loader.getController();
            // Pasar los valores a través del método set en el controlador de la escena principal
            mainController.setEmailAndNickname(email,nickname);

            // Obtener la ventana actual y cerrarla
            Stage currentStage = (Stage) pedirCartaButton.getScene().getWindow();
            currentStage.close();
        }
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public void setEmailAndNickname(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}