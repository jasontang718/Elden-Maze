/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Game;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.util.Duration;

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
        addZoomEffect(restart);
        addZoomEffect(quit);
    }    
    
    @FXML
    //Restart current level
    private void restartLevel(ActionEvent event) {
     //Set to game scene
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
    @FXML
       private void addZoomEffect(Button button) {
           
        //Zoom In effect
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(200), button);
        zoomIn.setToX(1.4);
        zoomIn.setToY(1.4);
        //Zoom Out effect
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(200), button);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);
        
        //Mouse hover event listener
        button.setOnMouseEntered(event -> {
            zoomIn.playFromStart();
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: #ff0000;");
         });
        
        button.setOnMouseExited(event -> {
            zoomOut.playFromStart(); 
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");  
        });
    }
}
