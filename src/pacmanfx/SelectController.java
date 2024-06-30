package pacmanfx;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SelectController implements Initializable {

    @FXML
    private Text title;
     
   
    private Model model; // Reference to the model

    public void select(Model model) {
        this.model = model;
    }
 
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create the floating animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), title);
        transition.setByY(-10);  // Move up by 10 pixels
        transition.setAutoReverse(true);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();
    }
     @FXML
  
    public void startGame() {
        Scene gameScene = model.getGameScene();
        model.setScene(gameScene);
        
    }

    
}

