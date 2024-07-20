package Game;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javax.swing.text.html.parser.DTDConstants;

public class settingController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private TextField upKey, downKey, leftKey, rightKey;
    @FXML
    private Button button;
    private Controller controller;
    @FXML
    private Slider volume;

    private Map<String, KeyCode> keyMap = new HashMap<>();
    private MediaPlayer mediaPlayer;

    public void setting(Controller controller) {
        this.controller = controller;
        this.mediaPlayer = controller.getmediaPlayer();
        loadData();
    }

    @FXML
    private void applyChanges(ActionEvent event) {
        saveData();
    }

    private void loadData() {
       
       
        TextField[] keys = {rightKey, leftKey, upKey, downKey};
        // Get current working 
        String filePath = "./src/Game/data.bin";
        System.out.println("Loading data from: " + filePath);

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            while (dis.available() > 0) {
                String keyType = dis.readUTF();
                if (!keyType.equals("volume")) {
                    String keyValue = dis.readUTF();
                    keyMap.put(keyType, KeyCode.valueOf(keyValue));
                    switch (keyType) {
                        case "rightKey":
                            rightKey.setText(keyValue);
                            break;
                        case "leftKey":
                            leftKey.setText(keyValue);
                            break;
                        case "upKey":
                            upKey.setText(keyValue);
                            break;
                        case "downKey":
                            downKey.setText(keyValue);
                            break;
                    }
                } else {
                    double savedVolume = dis.readDouble();
                    volume.setValue(savedVolume);
                    mediaPlayer.setVolume(savedVolume / 100);
                    System.out.println(mediaPlayer);
                }
            }
             volume.valueProperty().addListener((observable, oldValue, newValue) -> {
                   mediaPlayer.setVolume(newValue.doubleValue() / 100.0); // Normalize to [0.0, 1.0]
                 
        });
            // If any key is empty, set default keys and save data
            boolean anyKeyEmpty = false;
            for (TextField textField : keys) {
                if (textField.getText().isEmpty()) {
                    anyKeyEmpty = true;
                    break;
                }
            }

            if (anyKeyEmpty) {
                rightKey.setText("D");
                leftKey.setText("A");
                upKey.setText("W");
                downKey.setText("S");
                saveData(); // Save default keys
            }

        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error loading data: " + e.getMessage());
            e.printStackTrace();


            // Set default keys and volume on error
            rightKey.setText("D");
            leftKey.setText("A");
            upKey.setText("W");
            downKey.setText("S");
            volume.setValue(50); // Default volume
      
            saveData(); // Save default keys and volume
        }

        // Limit key length and update button state
        for (TextField textField : keys) {
            checkInput(textField, 1);
        }

        // Update media player volume based on slider
       

        // Initial button state update
        updateButtonState();
}

    private void saveData() {
        double value = volume.getValue();
        TextField[] keys = {rightKey, leftKey, upKey, downKey};

        // Construct the file path relative to the current working directory
        String filePath = "./src/Game/data.bin";
       

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(filePath))) {
            for (TextField key : keys) {
                String keyType = key.getId();
                String keyValue = key.getText();
                dos.writeUTF(keyType); // Write the key type
                dos.writeUTF(keyValue); // Write the key value
                System.out.println("Writing " + keyType + ": " + keyValue);
            }
            dos.writeUTF("volume");
            dos.writeDouble(value); // Write the volume value
            System.out.println("Writing volume: " + value);
         Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Data Saved");
            alert.setHeaderText(null);
            alert.setContentText("Setting has been saved successfully!");
              alert.showAndWait();
        } catch (IOException e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void Back(ActionEvent event) {
        Scene scene = controller.getintroScene();
        controller.setScene(scene);
    }

    private void checkInput(TextField textField, int maxLength) {
       textField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
           String character = event.getCharacter().toUpperCase(); // Convert to uppercase
           boolean isAlphabetic = character.matches("[a-zA-Z]");

           // Disallow non-alphabetic characters
           if (!isAlphabetic) {
               event.consume();
               return;
           }

           // Get current text and construct new text
           String currentText = textField.getText();
           if (currentText.length() >= maxLength) {
               event.consume();
           }
       });

       textField.textProperty().addListener((observable, oldValue, newValue) -> {
           // Convert to uppercase and truncate if necessary
           if (newValue.length() > maxLength) {
               newValue = newValue.substring(0, maxLength);
           }

           // If newValue is not already uppercase, convert it and update text
           if (!newValue.equals(newValue.toUpperCase())) {
               textField.setText(newValue.toUpperCase());
           }

           // Check for duplicate keys or other validation
           if (isDuplicateKey(newValue, textField)) {
               textField.setText(oldValue); // Revert to old value if invalid
           }

           // Move caret to the end
           textField.positionCaret(newValue.length());

           updateButtonState();
       });
   }



    private boolean isDuplicateKey(String newKey, TextField currentTextField) {
        TextField[] keys = {rightKey, leftKey, upKey, downKey};
        for (TextField key : keys) {
            if (key != currentTextField && newKey.equals(key.getText())) {
                return true;
            }
        }
        return false;
    }

    private void updateButtonState() {
        TextField[] keys = {rightKey, leftKey, upKey, downKey};
        for (TextField key : keys) {
            if (key.getText().isEmpty()) {
                button.setDisable(true);
                return;
            }
        }
        button.setDisable(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
}
