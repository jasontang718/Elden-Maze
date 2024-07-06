package pacmanfx;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class settingController implements Initializable {

    @FXML
    private Label label;
    @FXML
    private TextField upKey, downKey, leftKey, rightKey;
    @FXML
    private Button button;
    private Model model;
    @FXML
    private Slider volume;

    private Map<String, KeyCode> keyMap = new HashMap<>();
    private MediaPlayer mediaPlayer;

    public void setting(Model model) {
        this.model = model;
    }

    @FXML
    private void applyChanges(ActionEvent event) {
        saveData();
    }

    private void loadData() {
        String mediaUrl = getClass().getResource("/sound/countdown.mp3").toString();
        // Initialize mediaPlayer
        Media media = new Media(mediaUrl);
        mediaPlayer = new MediaPlayer(media);

        TextField[] keys = {rightKey, leftKey, upKey, downKey};

        try (BufferedReader reader = new BufferedReader(new FileReader("/Users/jasontang/Downloads/pacman/src/pacmanfx/data.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("hi");
                String[] parts = line.split(",");
                

                if (parts.length == 2) {
                    String keyType = parts[0];
                 String keyValue = parts[1];
                    if (!keyType.equals("volume")) {
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
                        double savedVolume = Double.parseDouble(keyValue);
                        volume.setValue(savedVolume);
                        mediaPlayer.setVolume(savedVolume / 100.0);
                    }
                }
            }

            if (rightKey.getText().isEmpty() && leftKey.getText().isEmpty() && upKey.getText().isEmpty() && downKey.getText().isEmpty()) {
                rightKey.setText("D");
                leftKey.setText("A");
                upKey.setText("W");
                downKey.setText("S");
                saveData();
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        for (TextField textField : keys) {
            checkLength(textField, 1);
        }

        volume.valueProperty().addListener((observable, oldValue, newValue) -> {
            mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        });
    }

    private void saveData() {
        double value = volume.getValue();
        TextField[] keys = {rightKey, leftKey, upKey, downKey};
        String filePath = "/Users/jasontang/Downloads/pacman/src/pacmanfx/data.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (TextField key : keys) {
                String newKey = key.getText();
                String keyType = key.getId();
                writer.write(keyType + "," + newKey);
                writer.newLine();
            }

            writer.write("volume," + value);
            writer.newLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void back(ActionEvent event) {
        Scene scene = model.getintroScene();
        model.setScene(scene);
    }

    private void checkLength(TextField textField, int maxLength) {
        textField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String currentText = textField.getText();
            String newText = currentText + event.getCharacter();
            if (newText.length() > maxLength || isDuplicateKey(newText, -1)) {
                textField.clear();
                event.consume();
            }
        });
    }

    private boolean isDuplicateKey(String newKey, int currentIndex) {
        TextField[] keys = {rightKey, leftKey, upKey, downKey};
        for (int i = 0; i < keys.length; i++) {
            if (i != currentIndex && newKey.equals(keys[i].getText())) {
                return true;
            }
        }
        return false;
    }

    public KeyCode getKey(String key) {
        return keyMap.get(key);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadData();
    }
}
