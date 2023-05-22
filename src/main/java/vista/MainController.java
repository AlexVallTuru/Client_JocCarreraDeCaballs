package vista;

import common.IPartida;
import common.Lookups;
import common.PartidaException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javax.naming.NamingException;

public class MainController implements Initializable {

    @FXML
    private Button salirButton;

    private String email = "";
    private String nickname = "";
    private int idpartida = 0;
    private String paloSeleccionado;

    //Variable que se tiene que obtener de la pestaña dificutad HARDCODEADO (a la espera de AITOR)
    private int dificultad = 1;

    @FXML
    private ComboBox paloSelect;

    static IPartida partida;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            /**
             * Obtenemos la instancia remota de partidaEJBRemoteLookup
             */
            partida = Lookups.partidaEJBRemoteLookup();
            
            ObservableList<String> palos = FXCollections.observableArrayList("Oros", "Copas", "Espadas", "Bastos");
            paloSelect.setItems(palos);
        } catch (NamingException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error al obtener la instancia de partidaEJBRemoteLookup", ex);
        }
    }

    @FXML
    private void jugarButtonAction(ActionEvent event) throws IOException {
        try {

            idpartida = partida.createPartida(dificultad, email, nickname);
            System.out.print("EL ID DE LA PARTIDA ES: ---> " + idpartida);

            Logger.getLogger(MainController.class.getName()).log(Level.INFO, "EL ID DE LA PARTIDA ES: ---> {0}", idpartida);

        } catch (PartidaException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, "Error al crear una partida:", ex);
        }
        paloSeleccionado = (String) paloSelect.getSelectionModel().getSelectedItem();
        this.paloSeleccionado = paloSeleccionado;
        if (paloSeleccionado == null) {
            paloSeleccionado = "Oros";
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("partidaJuego.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        // Obtener el controlador de la escena partidaJuego
        PartidaJuegoController partidaJuegoController = loader.getController();
        // Pasar los valores a través del método setIdPartida en el controlador de la escena partidaJuego
        partidaJuegoController.setIdPartida(idpartida);
        partidaJuegoController.setEmailAndNickname(email, nickname);
        partidaJuegoController.setPalo(paloSeleccionado);
        partidaJuegoController.setDificultad(dificultad);

        stage.setScene(scene);
        stage.show();

        // Obtener la ventana actual y cerrarla
        Stage currentStage = (Stage) salirButton.getScene().getWindow();
        currentStage.close();

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

        //System.out.println("Email: " + email);
        //System.out.println("Nickname: " + nickname);
    }

    @FXML
    private void dificultadNormalCheckBoxAction(ActionEvent event) {
        // Lógica para establecer la dificultad normal
    }

    @FXML
    private void dificultadDificilCheckBoxAction(ActionEvent event) {
        // Lógica para establecer la dificultad difícil
    }

    public void setEmailAndNickname(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }

}
