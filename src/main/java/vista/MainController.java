package vista;

import common.IPartida;
import common.Lookups;
import common.PartidaException;
import common.PartidaJuego;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.naming.NamingException;

public class MainController implements Initializable {

    @FXML
    private Button salirButton;

    private String email = "";
    private String nickname = "";
    private int idpartida = 0;

//    @EJB
//    PartidaEJB partidaEJB;
    //Variable que se tiene que obtener de la pestaña dificutad HARDCODEADO (a la espera de AITOR)
    private int dificultad = 1;

    static IPartida partida;

    @FXML
    private TableColumn tc_fecha_n;

    @FXML
    private TableColumn tc_nick_n;

    @FXML
    private TableColumn tc_partida_n;

    @FXML
    private TableColumn tc_puntos_n;

    @FXML
    private TableView tvHall_n;

    @FXML
    private TableColumn tc_fecha_d;

    @FXML
    private TableColumn tc_nick_d;

    @FXML
    private TableColumn tc_partida_d;

    @FXML
    private TableColumn tc_puntos_d;

    @FXML
    private TableView tvHall_d;

    private void RecargaHallOfFame(int Dificultad) {
        
        try {
            List<PartidaJuego> hallOfFame = partida.ObtenerHallOfFame(Dificultad);
            
            switch (Dificultad) {
                case 0:
                    
                    // Limpiar la tabla
                    tvHall_n.getItems().clear();
                    // Configurar las columnas
                    tc_fecha_n.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
                    tc_nick_n.setCellValueFactory(new PropertyValueFactory<>("nick"));
                    tc_partida_n.setCellValueFactory(new PropertyValueFactory<>("idPartida"));
                    tc_puntos_n.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));

                    tvHall_n.getItems().addAll(hallOfFame);
                    break;
                case 1:
                    
                    // Limpiar la tabla
                    tvHall_d.getItems().clear();
                    // Configurar las columnas
                    tc_fecha_d.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
                    tc_nick_d.setCellValueFactory(new PropertyValueFactory<>("nick"));
                    tc_partida_d.setCellValueFactory(new PropertyValueFactory<>("idPartida"));
                    tc_puntos_d.setCellValueFactory(new PropertyValueFactory<>("puntuacion"));

                    tvHall_d.getItems().addAll(hallOfFame);
                    break;
            }

        } catch (PartidaException e) {
            generarAdvertencia("no se ha podido cargar la tabla de hall of fame");
        }
    }

    private void generarAdvertencia(String motivo) {

        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle("ALERTA");
        alert.setHeaderText(null); // Sin encabezado
        alert.setContentText(motivo);

        alert.showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            /**
             * Obtenemos la instancia remota de partidaEJBRemoteLookup
             */
            partida = Lookups.partidaEJBRemoteLookup();
            RecargaHallOfFame(0);
            RecargaHallOfFame(1);
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

        FXMLLoader loader = new FXMLLoader(getClass().getResource("partidaJuego.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();

        // Obtener el controlador de la escena partidaJuego
        PartidaJuegoController partidaJuegoController = loader.getController();
        // Pasar los valores a través del método setIdPartida en el controlador de la escena partidaJuego
        partidaJuegoController.setIdPartida(idpartida);
        partidaJuegoController.setEmailAndNickname(email, nickname);

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
