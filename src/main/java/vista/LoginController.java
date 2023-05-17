package vista;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import common.ILoginResiter;
import common.Lookups;
import common.UsuariException;
import main.SingletonUsuari;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.TextArea;
import javax.naming.NamingException;

public class LoginController implements Initializable {

    @FXML
    private TextField emailField;
    @FXML
    private TextField nicknameField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private TextField registroEmailField;
    @FXML
    private TextField registroNicknameField;
    @FXML
    private TextArea errorField;
    @FXML
    private TextArea registroErrorField;

    static ILoginResiter user;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            user = Lookups.LoginRegisterEJBRemoteLookup();
        } catch (NamingException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loginButtonAction(ActionEvent event) throws IOException, UsuariException {
        try {
            if (user.validaUsuariExistent(emailField.getText(), nicknameField.getText())) {

                user.getSessio(emailField.getText());

                FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();

            } else {
                errorField.setText("Mail o nick, no existent");
            }
        } catch (UsuariException ex) {
            errorField.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void registerButtonAction(ActionEvent event) throws IOException {
        try {
            user.addUsuari(registroEmailField.getText(), registroNicknameField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.close();
        } catch (UsuariException ex) {
            errorField.setText("Error: " + ex.getMessage());
        }
    }

}
