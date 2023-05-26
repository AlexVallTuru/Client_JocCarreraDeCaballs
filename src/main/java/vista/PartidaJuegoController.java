package vista;

import common.Lookups;
import common.ObjetoPartida;
import common.PartidaException;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.naming.NamingException;
import static vista.LoginController.user;
import static vista.MainController.partida;

public class PartidaJuegoController implements Initializable {

    int idPartida;

    @FXML
    private ImageView imagePalo;

    @FXML
    private ImageView cartaImageView;

    @FXML
    private Button pedirCartaButton;

    @FXML
    private Label puntuacionLabel;

    private int puntuacionActual = 0;
    private ObjetoPartida puntuacionNueva;
    private String palo;
    private int dificultad;
    private int cartaCount = 0;

    private String email;
    private String nickname;

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            /**
             * Obtenemos la instancia remota de partidaEJBRemoteLookup
             */
            partida = Lookups.partidaEJBRemoteLookup();
            partida.limpiaDescartes();
        } catch (NamingException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error al obtener la instancia de partidaEJBRemoteLookup", ex);
        }
    }

    @FXML
    private void pedirCarta() throws PartidaException, IOException {
        cartaCount++;
        // Llamada a la lógica del juego para obtener una nueva carta
        puntuacionNueva = partida.partidaLogica(puntuacionActual, palo, dificultad);
        if (puntuacionNueva.isIsFinished()) {
            // Si nos hemos quedado sin cartas, termina la partida y no se guarda una puntuación
            puntuacionActual = 0;
            mostrarResultadoPartida("Perdiste la partida.", "Te has "
                    + "quedado sin cartas.");
        } else if (puntuacionNueva != null) {
            // Si obtenemos una nueva carta, sumamos la puntuación y la mostramos
            puntuacionActual = puntuacionNueva.getScore();
            Image cartaImage = new Image(getClass().getResourceAsStream("cartas/" + puntuacionNueva.getImageName()));
            cartaImageView.setImage(cartaImage);
        }

        puntuacionLabel.setText("Puntuación actual: " + puntuacionActual + "/38");

        if (puntuacionActual >= 38 || puntuacionNueva.isIsFinished()) {
            if (puntuacionActual == 38) {
                mostrarResultadoPartida("¡Ganaste la partida!", "¡Has"
                        + " obtenido una puntuación perfecta en " + cartaCount
                        + " cartas!");
            } else {
                mostrarResultadoPartida("¡Ganaste la partida!", "¡Has "
                        + "llegado a " + puntuacionActual + " de 38 puntos, "
                        + (puntuacionActual - 38) + " por encima de la "
                        + "puntuación perfecta en " + cartaCount + " cartas!");
            }

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
            mainController.setEmailAndNickname(email, nickname);

            // Obtener la ventana actual y cerrarla
            Stage currentStage = (Stage) pedirCartaButton.getScene().getWindow();
            currentStage.close();
        }
    }

    private void mostrarResultadoPartida(String header, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partida");
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    public void setPalo(String paloIn) {
        this.palo = paloIn;
        Image cartaImage = new Image(getClass().getResourceAsStream("cartas/" + palo + ".png"));
        imagePalo.setImage(cartaImage);
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public void setEmailAndNickname(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
}
