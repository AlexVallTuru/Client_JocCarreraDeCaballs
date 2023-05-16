/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
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
import main.SingletonUsuari;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;

/**
 * FXML Controller class
 *
 * @author alex
 */
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

    static ILoginResiter user;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        try {

            user = Lookups.LoginRegisterEJBRemoteLookup();

        } catch (NamingException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void loginButtonAction(ActionEvent event) throws IOException {

        user.getSessio(emailField.getText());
        
        System.out.print(user.getClass());
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();

        // Obtener la ventana actual y cerrarla
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
    }

    @FXML
    private void registerButtonAction(ActionEvent event) throws IOException {

            System.out.print("\n\nDATOS -------->"+registroEmailField.getText() +" "+ registroNicknameField.getText() +"\n\n");
            
            user.addUsuari(registroEmailField.getText(), registroNicknameField.getText());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

            // Obtener la ventana actual y cerrarla
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.close();
        
    }

}
