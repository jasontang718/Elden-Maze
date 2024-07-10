/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package pacmanfx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author jasontang
 */

public class ScoreboardController implements Initializable {

    private Model model;
    
  
    private int characterNo;

    @FXML
    private Text score;
    @FXML
    private Text duration;
        public void scoreBoard(Model model) {
            this.model = model;
       
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
        model.nextLevel();
        Scene gameScene = model.getGameScene();
        model.setScene(gameScene);
        model.setshowScore(true);
        model.setFinished(false);
    }

    @FXML
    private void quit(ActionEvent event) {
        model.setInGame(false);
        model.setCurrentLevel(0);
        Scene introScene = model.getintroScene();
        model.setScene(introScene);
    }
    

    @FXML 
    private void updateScore(){
        int readscore = model.getScore();
        score.setText(Integer.toString(readscore));
    }
}
