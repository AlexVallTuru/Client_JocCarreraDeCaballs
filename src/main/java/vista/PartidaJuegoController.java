package vista;

import common.Lookups;
import common.ObjetoPartida;
import common.PartidaException;
import java.io.IOException;
import static java.lang.Integer.parseInt;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
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
    private Label timerLabel;

    private Timer timer;
    private int segundos = 2 * 60;

    @FXML
    private Button btn_UsarCarta;

    @FXML
    private Button btn_saltarCarta;

    @FXML
    private Label lbl_notificacion;

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
    private int skips = 15;

    private String email;
    private String nickname;

    /**
     * Recogemos la partida
     *
     * @param url
     * @param rb
     */
    @FXML
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {

            RunTimer();
            /**
             * Obtenemos la instancia remota de partidaEJBRemoteLookup
             */
            btn_UsarCarta.setDisable(true);
            btn_saltarCarta.setDisable(true);

            partida = Lookups.partidaEJBRemoteLookup();
            partida.limpiaDescartes();

        } catch (NamingException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error al obtener la instancia de partidaEJBRemoteLookup", ex);
        }
    }

    /**
     * Llamamos al servidor, le pedimos la carta y la mostramos por pantalla
     *
     * @throws PartidaException
     * @throws IOException
     */
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
            GoToMain();
        } else {
            Image cartaImage = new Image(getClass().getResourceAsStream("cartas/" + puntuacionNueva.getImageName()));
            cartaImageView.setImage(cartaImage);
        }

        lbl_notificacion.setText("");
        pedirCartaButton.setDisable(true);
        btn_UsarCarta.setDisable(false);

        if (skips != 0) {
            btn_saltarCarta.setDisable(false);
        }
    }

    /**
     * Metodo para omitir carta mostrada, buscamos una carta nueva
     *
     * @param event
     * @throws PartidaException
     * @throws IOException
     */
    @FXML
    void SaltarCarta(ActionEvent event) throws PartidaException, IOException {

        skips--;

        pedirCarta();

        lbl_notificacion.setText("Nueva carta pedida!, saltos restantes:" + skips);

        btn_UsarCarta.setDisable(false);

        if (skips != 0) {
            btn_saltarCarta.setDisable(false);
        } else {
            btn_saltarCarta.setDisable(true);
        }

        pedirCartaButton.setDisable(true);
    }

    /**
     * Usaremos la carta mostrada por pantalla sumando o restando la puntuacion,
     * mostramos el resultado de la accion realizada.
     *
     * @param event
     * @throws PartidaException
     * @throws IOException
     */
    @FXML
    void UsarCarta(ActionEvent event) throws PartidaException, IOException {

        if (puntuacionNueva != null) {
            // Si usamos la carta, sumamos la puntuación y la mostramos
            puntuacionActual = puntuacionNueva.getScore();
        }

        puntuacionLabel.setText("Puntuación actual: " + puntuacionActual + "/38");
        lbl_notificacion.setText(puntuacionNueva.getMovimiento());

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

            partida.aniadirPuntosPartida(idPartida, cartaCount);
            GoToMain();

        }

        btn_UsarCarta.setDisable(true);
        pedirCartaButton.setDisable(false);
        if (skips != 0) {
            btn_saltarCarta.setDisable(true);
        }

    }

    private void GoToMain() throws PartidaException, IOException {

        timer.cancel();

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

    private void mostrarResultadoPartida(String header, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partida");
        alert.setHeaderText(header);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Obtenemos el palo seleccionado por el usuario.
     * 
     * @param paloIn 
     */
    public void setPalo(String paloIn) {
        this.palo = paloIn;
        Image cartaImage = new Image(getClass().getResourceAsStream("cartas/" + palo + ".png"));
        imagePalo.setImage(cartaImage);
    }

    /**
     * Obtenemos la dificultad seleccionada por el usuario.
     * 
     * @param dificultad 
     */
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

    /**
     * Primero de todo instanciamos un objeto de timer y usamos el
     * timer.scheduleAtFixedRate para programar un ratio, en este caso
     * ejecutaremos la tarea de TimerTask cada segundo. La tarea TimerTask
     * ejecuta el método Run, que lo que hace és que hasta que el tiempo
     * resatnte no llegue a 0, mostraremos a traves del Platform.runLater en
     * otro hilo del procesador un contador del tiempo en minutos y segundos que
     * imprimira el label, para que el label se ejecute de forma paralela a la
     * partida, y iremos restando el tiempo. Finalmente, cuando el tiempo sea 0,
     * entraremos al else y cambiaremos el label a "Tiempo finalizado!"; La
     * tarea del timer la cancelaremos para que se deje de ejecutar en esta
     * ultima vuelta y finalmente en otro hilo mostraremos una ventana
     * informativa, y al aceptarla llamaremos al metodo GoToMain que nos llevara
     * a la pantalla de Main. Dentro del método GoToMain tenemos tambien la
     * cancelación de la tarea para que se cancele si llamamos al metodo mucho
     * antes.
     */
    public void RunTimer() {
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            int tiempoRestante = segundos;

            @Override
            public void run() {
                if (tiempoRestante > 0) {
                    int minutos = tiempoRestante / 60;
                    int segundos = tiempoRestante % 60;
                    String tiempoRestanteString = String.format("%02d:%02d", minutos, segundos);

                    Platform.runLater(() -> {
                        timerLabel.setText("Tiempo restante: " + tiempoRestanteString);
                    });

                    tiempoRestante--;
                } else {
                    Platform.runLater(() -> {
                        timerLabel.setText("Tiempo finalizado!");
                    });

                    timer.cancel();

                    Platform.runLater(() -> {
                        mostrarResultadoPartida("Te dormiste!", "Se ha terminado el tiempo de partida");
                        try {
                            GoToMain();
                        } catch (PartidaException | IOException ex) {
                            Logger.getLogger(PartidaJuegoController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        }, 0, 1000); // Ejecutar cada segundo
    }

}
