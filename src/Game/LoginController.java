/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Game;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


/**
 * FXML Controller class
 *
 * @author jasontang
 */
public class LoginController implements Initializable {

    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    private Controller controller;
  
     // Initializes the controller class.
     
     public void login(Controller controller) {
        this.controller = controller;
    }
     //Automatically check login credential
     private void handleLogin() {
        Scene introScene = controller.getintroScene();
        //Remove white space
        String user = username.getText().trim();
        String pass = password.getText().trim();
        
        // Example simple login check
        if ("admin".equals(user) && "password".equals(pass)) {
            System.out.println("Login successful!");
            controller.setScene(introScene);
            
        } 
    }
     //Check login credentials with KeyCode Enter
    private void handleManualLogin() {
         //Remove white space
        String user = username.getText().trim();
        String pass = password.getText().trim();
        if ("admin".equals(user) && "password".equals(pass)) {
            controller.setScene(controller.getintroScene());
            
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login failed");
            alert.setHeaderText(null); 
            alert.setContentText("Incorrect login credentials");

            alert.showAndWait();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
     username.textProperty().addListener((observable, oldValue, newValue) -> {
            handleLogin(); // Call login check whenever username changes
        });
        
        password.textProperty().addListener((observable, oldValue, newValue) -> {
            handleLogin(); // Call login check whenever password changes
        });
        // Call login check whenever Enter is clicked
          username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleManualLogin();
            }
        });

            // Call login check whenever Enter is clicked
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleManualLogin();
            }
        });
    }    
     
    
}
