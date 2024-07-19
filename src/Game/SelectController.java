package Game;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class SelectController implements Initializable {

    @FXML
    private Text title;

    private Controller controller; // Reference to the model

    @FXML
    private ImageView assasin;
    @FXML
    private ImageView mage;
    @FXML
    private Rectangle knighShape;
    @FXML
    private Rectangle mageShape;
    @FXML
    private Rectangle assasinShape;
    @FXML
    private ImageView knight;

    private boolean isZoomedIn = false;
    private ImageView currentlyZoomedImageView = null;
    private Rectangle currentlyZoomedShape = null;
    
    //Initialize controller class
    public void select(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create the floating animation
        TranslateTransition transition = new TranslateTransition(Duration.seconds(2), title);
        transition.setByY(-10);  // Move up by 10 pixels
        transition.setAutoReverse(true);
        transition.setCycleCount(TranslateTransition.INDEFINITE);
        transition.play();
        imageTransition(knight, knighShape);
        imageTransition(assasin, assasinShape);
        imageTransition(mage, mageShape);
    }

    @FXML
    //Start game
    public void startGame() {
        controller.setCurrentLevel(0);
        Scene gameScene = controller.getGameScene();
        controller.setScene(gameScene);
    }
    
    //Image animation
    private void imageTransition(ImageView imageView, Rectangle shape) {
        double newYPosition = -10;

        imageView.setOnMouseEntered(event -> {
            imageView.setTranslateY(newYPosition);
            shape.setTranslateY(newYPosition);
        });

        imageView.setOnMouseExited(event -> {
            imageView.setTranslateY(0);
            shape.setTranslateY(0);
        });

        ScaleTransition zoomIn = new ScaleTransition(Duration.millis(200), imageView);
        zoomIn.setToX(1.5);
        zoomIn.setToY(1.5);

        ScaleTransition zoomOut = new ScaleTransition(Duration.millis(200), imageView);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);

        ScaleTransition zoomInShape = new ScaleTransition(Duration.millis(200), shape);
        zoomInShape.setToX(1.5);
        zoomInShape.setToY(1.5);

        ScaleTransition zoomOutShape = new ScaleTransition(Duration.millis(200), shape);
        zoomOutShape.setToX(1.0);
        zoomOutShape.setToY(1.0);

        imageView.setOnMouseClicked(event -> {
            if (currentlyZoomedImageView != null && currentlyZoomedImageView != imageView) {
                // Zoom out the currently zoomed in ImageView and Shape
                ScaleTransition zoomOutCurrent = new ScaleTransition(Duration.millis(200), currentlyZoomedImageView);
                zoomOutCurrent.setToX(1.0);
                zoomOutCurrent.setToY(1.0);
                zoomOutCurrent.playFromStart();
                
                ScaleTransition zoomOutCurrentShape = new ScaleTransition(Duration.millis(200), currentlyZoomedShape);
                zoomOutCurrentShape.setToX(1.0);
                zoomOutCurrentShape.setToY(1.0);
                zoomOutCurrentShape.playFromStart();
            }

            if (!isZoomedIn || currentlyZoomedImageView != imageView) {
                // Zoom in the clicked ImageView and Shape
                zoomIn.playFromStart();
                zoomInShape.playFromStart();
                currentlyZoomedImageView = imageView;
                currentlyZoomedShape = shape;
                isZoomedIn = true;
            } else {
                // Zoom out the clicked ImageView and Shape if it's already zoomed in
                zoomOut.playFromStart();
                zoomOutShape.playFromStart();
                currentlyZoomedImageView = null;
                currentlyZoomedShape = null;
                isZoomedIn = false;
            }
            String selectCharacter = currentlyZoomedImageView.getId();
            if (selectCharacter.equals("knight")){
             controller.setCharacterNo(0);
           
            }else if (selectCharacter.equals("assasin")){
             controller.setCharacterNo(1);
               
            }else{
             controller.setCharacterNo(2);
             
            }
        });
    }
}
