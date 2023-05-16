package vista;


import common.Article;
import common.Compra;
import common.CompraException;
import common.ICarroCompra;
import common.ITenda;
import common.Lookups;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javax.naming.NamingException;

public class PrimaryController implements Initializable{

    @FXML
    private TextField txt_idcompra;

    @FXML
    private TextArea lv_Logger;

    @FXML
    private ListView<Article> lv_compraEnCurs;

    @FXML
    private ListView<Compra> lv_consultaCompra;

    @FXML
    private ListView<Article> lv_llistaArticles;

    @FXML
    private TextField txt_descripcio;

    @FXML
    private TextField txt_pvp;

    @FXML
    private TextField txt_login;
    
    @FXML
    private TextField txt_nom;
    
    @FXML
    private Label lbl_totalCompra;

    
    /***
     * identificador de compra
     */
    String idCompra;
    
     /***
    * ens proporciona mètodes que ens permeten gestionar un carro de la compra.
    * Des del moment que connectem, ens manté l'estat de les dades fins que ens desconnectem
    */
    static ICarroCompra carro;
   
   /***
    * Ens proporciona mètodes remots que realitzen diverses funcions
    * Similar a mètodes estàtics d'una classe
    */
   static ITenda tenda;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
        
        // ontenim una instància remota de la classe CarroCompraEJB
        carro = Lookups.carroCompraEJBRemoteLookup();
        
        // obntenim una instància remota de la classe TendaEJB
        tenda = Lookups.tendaEJBRemoteLookup();
        
         lv_Logger.appendText("Connexió correcta al servidor remot");
        
        } catch (NamingException ex) {
            
            lv_Logger.appendText("Error iniciant: " + ex + System.lineSeparator());
        }
    }

    @FXML
    void onAction_LlistarArticles(ActionEvent event) {
        
        try {
            
            ObservableList<Article> llistaArticles = FXCollections.observableArrayList();
            
            lv_llistaArticles.getItems().clear();
            
            llistaArticles.addAll(tenda.getArticles());
            
            lv_llistaArticles.getItems().addAll(llistaArticles);
            
        } catch (CompraException ex) {
            lv_Logger.appendText("Error connectant amb tenda remota: " + ex + System.lineSeparator());
        }
        
        
    }

    @FXML
    void onAction_AfegirLiniaCompra(ActionEvent event) {
        
        try {
            Article art = (Article)lv_llistaArticles.getSelectionModel().getSelectedItem();
            
            carro.addArticle(art);
            
            lv_compraEnCurs.getItems().add(art);
        } catch (CompraException ex) {
           lv_Logger.appendText("Error afegint article ");
        }

    }

    @FXML
    void onAction_FinalitzaCompra(ActionEvent event) {
        
        try {
            Long idCompra = carro.finalitzaCompra();
            
            lv_Logger.appendText("Compa finalitzada amb id: " +  idCompra);
            
            Compra c = tenda.getCompra(idCompra);
            
            lbl_totalCompra.setText(Float.toString(c.getTotal()));
            
            
        } catch (CompraException ex) {
            lv_Logger.appendText("Error finalitzant compra" );
        }

    }

    @FXML
    void onAction_AltaArticle(ActionEvent event) {
        
        try {
            tenda.addArticle(txt_descripcio.getText(), Float.parseFloat(txt_pvp.getText()));
        } catch (CompraException ex) {
            lv_Logger.appendText("error donant d'alta l'article: " + txt_descripcio.getText() );
        }

    }

    @FXML
    void onAction_ConsultaCompra(ActionEvent event) {
        
        try {
            Compra c = tenda.getCompra(Long.valueOf(txt_idcompra.getText()));
            
            lv_consultaCompra.getItems().add(c);
            
        } catch (CompraException ex) {
             lv_Logger.appendText("error recuperant compra id: " + txt_idcompra.getText());
        }
    }

    @FXML
    void onAction_Login(ActionEvent event) {
        try {
            
            idCompra = carro.getSessio(txt_login.getText());
            
            lv_Logger.appendText("Ok client reconegut amb idcompra: " + idCompra );
            
        } catch (CompraException ex) {
             lv_Logger.appendText("Error el client no existeix: " + ex + System.lineSeparator());
        }
    }

    @FXML
    void onAction_Alta(ActionEvent event) {
        
        try {
            tenda.addClient(txt_login.getText(), txt_nom.getText());
        } catch (CompraException ex) {
             lv_Logger.appendText("Error donant d'alta client: " + ex + System.lineSeparator());
        }
    }
    
    @FXML
    void onAction_Desconnectar(ActionEvent event) {
        
        carro.tancaSessio();
        
        lv_Logger.appendText("S'ha tancat la sessió: " + System.lineSeparator());

    }
}
