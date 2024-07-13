/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pacmanfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
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
    private TextField password;
    @FXML
    private Button login;
    private Model model;
    /**
     * Initializes the controller class.
     */
     public void login(Model model) {
        this.model = model;
    }
     @FXML
     private void handleLogin() {
        Scene introScene = model.getintroScene();
        String user = username.getText().trim();
        String pass = password.getText().trim();
        
        // Example simple login check
        if ("admin".equals(user) && "password".equals(pass)) {
            System.out.println("Login successful!");
            model.setScene(introScene);
        } 
    }
      @FXML
    private void handleManualLogin() {
        String user = username.getText().trim();
        String pass = password.getText().trim();
        if ("admin".equals(user) && "password".equals(pass)) {
            model.setScene(model.getintroScene());
            
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
          username.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleManualLogin();
            }
        });

        // Handle Enter key pressed in password field
        password.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleManualLogin();
            }
        });
    }    
     
    
}
