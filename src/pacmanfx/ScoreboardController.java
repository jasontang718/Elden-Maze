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
import javafx.scene.text.Text;

/**
 * FXML Controller class
 *
 * @author jasontang
 */

public class ScoreboardController implements Initializable {

    private Model model;
    @FXML
    private Text score;
    @FXML
    private Text time;
        public void scoreBoard(Model model) {
        this.model = model;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
  
   

}
