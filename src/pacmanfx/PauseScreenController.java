/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pacmanfx;

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
     private Model model;
     
    public void pause(Model model) {
        this.model = model;
       
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void restart(ActionEvent event) {
     
     Scene gameScene = model.getGameScene();
      model.setScene(gameScene);
      model.initLevel();
      
    }
   @FXML
    private void quit(ActionEvent event) {
     model.setInGame(false);
     model.setCurrentLevel(0);
     Scene introScene = model.getintroScene();
     model.setScene(introScene);
    }
}
