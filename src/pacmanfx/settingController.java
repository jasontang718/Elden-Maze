/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package pacmanfx;

import com.sun.java.accessibility.util.EventID;
import java.awt.RenderingHints;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import jdk.nashorn.internal.runtime.PropertyDescriptor;



public class settingController implements Initializable {
    
    @FXML
    private Label label;
    @FXML
    private TextField upKey,downKey,leftKey,rightKey;

    private Button button;
    private Model model;
    
  public void setting(Model model) {
        this.model = model;
    }

    
  @FXML
private void applyChanges(ActionEvent event) {
    
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


 @FXML
    private void checkLength(TextField textField, int maxLength) {
        textField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            // Get current text in the TextField
            String currentText = textField.getText();
            // Check if adding the new character exceeds maxLength or is a duplicate
            String newText = currentText + event.getCharacter();
             System.out.println(newText);
        if (newText.length() > maxLength || isDuplicateKey(newText, -1)) {
                System.out.println(currentText.length());
                textField.clear();
                event.consume();
            }
        });
    }


 
    private boolean isDuplicateKey(String newKey, int currentIndex) {
        TextField[] key = {rightKey,leftKey,upKey,downKey};
        for (int i = 0; i < key.length; i++) {
            if (i != currentIndex && newKey.equals(key[i].getText())) {
                System.out.println("true");
                System.out.println(key[i].getText());
                 
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
      TextField[] key = {rightKey,leftKey,upKey,downKey};
    
       try (BufferedReader reader = new BufferedReader(new FileReader("/Users/jasontang/NetBeansProjects/pacman/src/pacmanfx/data.txt"))) {
            String savedText = reader.readLine();
            
            
            if (savedText != null) {
                String[] parts = savedText.split(",");
                for (int i = 0 ;i < parts.length ;i++){
                    System.out.println(parts[i]);
                    key[i].setText(parts[i]);
                }
            for (TextField textField : key) {
            checkLength(textField, 1);
        }
 

  
         
        } }catch (IOException e) {
            e.printStackTrace();
           
        }
     
    
    }    
    
}
