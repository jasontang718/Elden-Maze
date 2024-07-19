/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Game;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author jasontang
 */
public class PauseScreenController implements Initializable {

    @FXML
    private Button restart;
    @FXML
    private Button resume;
    @FXML
    private Button quit;
    private Controller controller;
     
    public void pause(Controller controller) {
        this.controller = controller;
       
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    //Restart current level
    private void restartLevel(ActionEvent event) {
     
     Scene gameScene = controller.getGameScene();
      controller.setScene(gameScene);
      controller.initLevel();
      
    }
   @FXML
   //Quit current level
    private void Quit(ActionEvent event) {
        controller.setInGame(false);
        controller.setCurrentLevel(0);
        Scene introScene = controller.getintroScene();
        controller.setScene(introScene);
    }
}
