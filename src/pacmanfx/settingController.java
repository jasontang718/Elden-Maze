/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package pacmanfx;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static javax.management.Query.value;




public class settingController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private TextField upKey,downKey,leftKey,rightKey;

    @FXML
    private Button button;
    private Model model;
    @FXML
    private Slider volume;
    
     private MediaPlayer mediaPlayer;
  public void setting(Model model) {
        this.model = model;
    }

    
  @FXML
private void applyChanges(ActionEvent event) {
    double value = volume.getValue();
    TextField[] key = {rightKey,leftKey,upKey,downKey};
    String filePath = "/Users/jasontang/NetBeansProjects/pacman/src/pacmanfx/data.txt";

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
        for (int i = 0; i < key.length; i++) {
            
           
            String newKey = key[i].getText();
           
         
            writer.write(newKey);
            
                                    
            // Add comma after each value, except the last one
            if (i < key.length - 1) {
                writer.write(",");
            }
        }
        writer.newLine();
        writer.write(String.valueOf(value));
    } catch (IOException e) {
        e.printStackTrace();
        // Handle IOException (file writing error) appropriately
    }
}

    
    @FXML
    private void back(ActionEvent event) {
          Scene scene = model.getintroScene();
        model.setScene(scene);
        
    }


    private void checkLength(TextField textField, int maxLength) {
        textField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            // Get current text in the TextField
            String currentText = textField.getText();
            // Check if adding the new character exceeds maxLength or is a duplicate
            String newText = currentText + event.getCharacter();
             System.out.println(newText);
        if (newText.length() > maxLength ) {
                System.out.println(currentText.length());
                event.consume();
            }
        else if(isDuplicateKey(newText, -1)){
                 textField.clear();
                 event.consume();
        }
        
        });
    }


 
    private boolean isDuplicateKey(String newKey, int currentIndex) {
        TextField[] key = {rightKey,leftKey,upKey,downKey};
        for (int i = 0; i < key.length; i++) {
            if (i != currentIndex && newKey.equals(key[i].getText())) {
              
                 
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
         String mediaUrl = getClass().getResource("/sound/countdown.mp3").toString();
        
        // Initialize mediaPlayer
        Media media = new Media(mediaUrl);
        mediaPlayer = new MediaPlayer(media);
      TextField[] key = {rightKey,leftKey,upKey,downKey};
    
       try (BufferedReader reader = new BufferedReader(new FileReader("/Users/jasontang/NetBeansProjects/pacman/src/pacmanfx/data.txt"))) {
            String savedText = reader.readLine();
            
            
            if (savedText != null) {
                String[] parts = savedText.split(",");
                for (int i = 0 ;i < parts.length ;i++){
                    System.out.println(parts[i]);
                    key[i].setText(parts[i]);
                }
                
            String volumeLine = reader.readLine();
            if (volumeLine != null) {
                double savedVolume = Double.parseDouble(volumeLine);
                volume.setValue(savedVolume);
                mediaPlayer.setVolume(savedVolume / 100.0);
            }

            for (TextField textField : key) {
            checkLength(textField, 1);
     
        }
         
        } }catch (IOException e) {
            e.printStackTrace();
           
        }
       
        
       
      volume.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
               
                mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
                 }
        });
    }}
            
               

