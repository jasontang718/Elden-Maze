package pacmanfx;

import java.io.IOException;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class SelectController implements Initializable {

    @FXML
    private Text title;

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
    private void handleNextScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("next.fxml"));
            Parent nextRoot = loader.load();
            Scene nextScene = new Scene(nextRoot);
            Stage stage = (Stage) title.getScene().getWindow();
            stage.setScene(nextScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
