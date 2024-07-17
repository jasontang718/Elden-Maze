/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package Game;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

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
    @FXML
    private Button button;
        public void scoreBoard(Controller controller) {
        this.controller = controller;
       updateText();
        updateScore();
    }

   
    /**
     * Initializes the controller class.
     */
   public int getCharacterNo(){
        return characterNo;
    }
     public void setCharacterNo(int characterNo){
        this.characterNo = characterNo;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       
    
    }    
    
    @FXML
    private void nextLevel(ActionEvent event) {
      controller.nextLevel();
      Scene gameScene = controller.getGameScene();
      controller.setScene(gameScene);
   
    }

    @FXML
    private void quit(ActionEvent event) {
        controller.setInGame(false);
        controller.setCurrentLevel(0);
        Scene introScene = controller.getintroScene();
        controller.setScene(introScene);
    }
    
    private void updateText(){
     int gameCompleted = controller.getCurrentLevel();
     if (gameCompleted == 2){
      title.setText("Congratulation");
       button.setDisable(true);
     }
    }
    private void updateScore(){
        int readscore = controller.getScore();
        score.setText(Integer.toString(readscore));
    }
}
