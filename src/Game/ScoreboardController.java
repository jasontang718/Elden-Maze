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
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author jasontang
 */

public class ScoreboardController implements Initializable {

    private Controller controller;
    
  
     private int characterNo;

    @FXML
    private Text score;
    @FXML
    private Text title;
    private Button button;
    @FXML
    private Button next;
    @FXML
    private Button exit;
    public void scoreBoard(Controller controller) {
        this.controller = controller;
        updateText();
        updateScore();
    }

   

    //Initializes the controller class.
     
   public int getCharacterNo(){
        return characterNo;
    }
     public void setCharacterNo(int characterNo){
        this.characterNo = characterNo;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        addZoomEffect(next);
        addZoomEffect(exit);
    
    }    
    
    @FXML
    //Proceed to next level
    private void nextLevel(ActionEvent event) {
      controller.nextLevel();
      Scene gameScene = controller.getGameScene();
      controller.setScene(gameScene);
   
    }

    @FXML
    //Quit game
    private void Quit(ActionEvent event) {
        controller.setInGame(false);
        controller.setCurrentLevel(0);
        Scene introScene = controller.getintroScene();
        controller.setScene(introScene);
    }
    //Change title if final level completed
    private void updateText(){
     int gameCompleted = controller.getCurrentLevel();
     if (gameCompleted == 2){
      title.setText("Congratulation");
       button.setDisable(true);
     }
    }
    //Update score
    private void updateScore(){
        int readscore = controller.getScore();
        score.setText(Integer.toString(readscore));
    }
       private void addZoomEffect(Button button) {
        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(200), button);
        zoomIn.setToX(1.4);
        zoomIn.setToY(1.4);
        
        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(200), button);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);

        button.setOnMouseEntered(event -> {
            zoomIn.playFromStart();
        
         });
        
        button.setOnMouseExited(event -> {
            zoomOut.playFromStart(); 

        });
    }
}
