/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pacmanfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

/**
 * FXML Controller class
 *
 * @author jasontang
 */
public class LoginController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Button login;

    /**
     * Initializes the controller class.
     */
    public void userLogin(TextField textfield){
    textfield.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            // Get current text in the TextField
            String currentText = textfield.getText();
            // Check if adding the new character exceeds maxLength or is a duplicate
            
      
    });
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        TextField[] loginData = {username,password};
         for (TextField textField : loginData) {
            userLogin(textField);
        
    }    
    
}
}