package pacmanfx;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.SHIFT;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Model extends Application {
    private final Font smallFont = Font.font("Times New Roman", FontWeight.BOLD,30);
    private boolean inGame = false;
    private boolean dying = false;
    private boolean showscore = true; // Class-level variable
    private static final int BLOCK_SIZE = 40;

    private static final int MAX_ENEMY = 12;
    private static int validSpeed = 1;

    private int currentLevel;
    private Timeline timer;
    
    public Image heart, spiderImage, floor3, floor2, coin, sword, blinded;
    public Image up, down, left, right, enhanced, background, assassinImage, skeletonImage, fire, spike;

    private int reqDx = 0;
    private int reqDy = 0;
   
    Stage stage;

    short[] screenData;
    Scene gameScene;
   
    private Scene introScene;
    
    private MediaPlayer mediaPlayer;
    private boolean finished = true;
    private boolean trap;
   
    private Map<String, KeyCode> keyMap = new HashMap<>();
    
    
    
    private int characterNo; //THIS FOR CHARACTER SELECTION
    
    
    
    

    
    
    private  KeyCode moveRight,moveLeft,moveUp,moveDown;
    private Knight knight = new Knight(this);
    private Assassin assassin = new Assassin(this);
    private Mage mage = new Mage(this);
    
    private Character[] characters = new Character[]{knight, assassin, mage};
            
    private Maze1 maze1 = new Maze1(this);
    private Maze2 maze2 = new Maze2(this);
    private Maze3 maze3 = new Maze3(this);
    
    private Maze[] mazes = new Maze[]{maze1,maze2,maze3};    
    
    private Spider spider = new Spider(this, characters);
    private Goblin goblin = new Goblin(this, characters);
    private Phantom phantom = new Phantom(this, characters);
    private Skeleton skeleton = new Skeleton(this, characters);
    
    private Enemy[] enemies = new Enemy[]{skeleton, goblin, spider};
    
    private int lives;

    private int screenHSize = mazes[currentLevel].getHBlocks() * BLOCK_SIZE;
    private int screenVSize = mazes[currentLevel].getVBlocks() * BLOCK_SIZE;
    private int screenSize = screenHSize*screenVSize;
    
  @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        loadScene("login.fxml");
        // Set the stage
        primaryStage.setTitle("Elden Maze");

        // Create the intro scene
        Button startButton = new Button("Start Game");
        startButton.setFont(smallFont);
        startButton.setOnAction(event -> loadScene("select.fxml"));  
        
        Button settingsButton = new Button("Settings");
        settingsButton.setFont(smallFont);
        settingsButton.setOnAction(event -> loadScene("setting.fxml")); 
        
        Button exitButton = new Button("Exit");
        exitButton.setFont(smallFont);
         exitButton.setOnAction(e -> exitApplication());
            

        double width = 250;
        double height = 50;
        
        startButton.setPrefSize(width, height);
        settingsButton.setPrefSize(width, height);
        exitButton.setPrefSize(width, height);

        
        Label title = new Label("Elden Maze");
        title.setStyle("-fx-font: normal bold 50px 'serif';" + "-fx-text-fill: maroon;");

        VBox introLayout = new VBox(20);
        introLayout.setPrefSize(728, 403);
        introLayout.setStyle("-fx-background-image: url('/images/background.jpg');" + "-fx-background-size: cover;" + "-fx-background-repeat: stretch;" + "-fx-background-position: center center;");
        introLayout.setAlignment(Pos.CENTER);
        introLayout.getChildren().add(title);
        introLayout.getChildren().add(startButton);
        introLayout.getChildren().add(settingsButton);
        introLayout.getChildren().add(exitButton);        

        introScene = new Scene(introLayout);

        // Create the game scene
        StackPane game = new StackPane();
        Canvas canvas = new Canvas(screenHSize, screenVSize);
        GraphicsContext g2d = canvas.getGraphicsContext2D();

        game.getChildren().add(canvas);
        gameScene = new Scene(game, screenHSize, screenVSize, Color.BLACK);
       
        gameScene.setOnKeyPressed((KeyEvent event) -> {
            KeyCode key = event.getCode();


            if (key == KeyCode.SPACE && !inGame) {
                    inGame = true;
                    
                    if (currentLevel == 0){
                        initGame();
                    } else{
                        initLevel();
                    }
                }


            if (inGame) {
                switch (key) {
                    case SHIFT:
                        characters[characterNo].setRunning(true);
                        System.out.println("Run: " + characters[characterNo].getRunning());
                        break;

                    case ESCAPE:
                        inGame = false;
                        loadScene("pauseScreen.fxml");
                        break;
                    default:
                        handleMovement(key);
                }
            }
        });

        gameScene.setOnKeyReleased((KeyEvent event) -> {
            KeyCode key = event.getCode();

            if (inGame && key == KeyCode.SHIFT) {
                characters[characterNo].setRunning(false);
                System.out.println("Run: " + characters[characterNo].getRunning());
            }
        });

        loadImages();
        initGame();
        startTrapTimer();
        
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(g2d);
            }
        }.start();

        loadScene("login.fxml");
    }
    
    public void setCurrentLevel(int currentLevel){
        this.currentLevel = currentLevel;
    }
    
    public void setInGame(boolean inGame){
        this.inGame = inGame;
    }
    
    public Scene getGameScene() {
        return gameScene;
    }
    public Scene getintroScene() {
        return introScene;
    }

    public int getCharacterNo(){
        return characterNo;
    }
     public void setCharacterNo(int characterNo){
        this.characterNo = characterNo;
    }
    public int getCurrentLevel(){
        return currentLevel;
    }
    
    public boolean getInGame(){
        return inGame;
    }
    
    public void setDying(boolean dying){
        this.dying = dying;
    }
    
    public void setshowScore(boolean showscore){
        this.showscore = showscore;
    }
    
    public void setFinished(boolean finished){
        this.finished = finished;
    }

    public int getBlockSize(){
        return BLOCK_SIZE;
    }
   
    public short[] getScreenData() {
        return screenData;
    }

    public int getScreenHSize() {
        return screenHSize;
    }
    
    public int getScreenVSize() {
        return screenVSize;
    }
    
    public int getScreenSize() {
        return screenSize;
    }
    public int getScore() {
        return characters[characterNo].getScore();
    }
    
    public int getReqDx() {
        return reqDx;
    }
    
    public int getReqDy() {
        return reqDy;
    }
    
    public boolean getActive(){
        return trap;
    }
    
    public void setActive(boolean value){
        this.trap = value;
    }
    
    public void loadImages() {
        blinded = new Image(getClass().getResourceAsStream("/images/blinded.png"),4000,2247,false,false);        
        down = new Image(getClass().getResourceAsStream("/images/knightleft.gif"));
        up = new Image(getClass().getResourceAsStream("/images/knightright.gif"));
        left = new Image(getClass().getResourceAsStream("/images/knightleft.gif"));
        right = new Image(getClass().getResourceAsStream("/images/knightright.gif"));
        spiderImage = new Image(getClass().getResourceAsStream("/images/spider.gif"));
        heart = new Image(getClass().getResourceAsStream("/images/heart.png"));
        floor3 = new Image(getClass().getResourceAsStream("/images/floor3.jpg"));
        floor2 = new Image(getClass().getResourceAsStream("/images/floor2.jpg"));
        coin = new Image(getClass().getResourceAsStream("/images/coin.gif"));
        sword = new Image(getClass().getResourceAsStream("/images/powerup.gif"));
        enhanced = new Image(getClass().getResourceAsStream("/images/powerupPlayer.gif"));
        background = new Image(getClass().getResourceAsStream("/images/background.jpg"));
        assassinImage = new Image(getClass().getResourceAsStream("/images/assassindown.gif"));
        skeletonImage = new Image(getClass().getResourceAsStream("/images/skeleton.gif"));
        fire = new Image(getClass().getResourceAsStream("/images/fire.gif"));
        spike = new Image(getClass().getResourceAsStream("/images/spike.gif"));
    }
    
    public void playSound(String soundFileName) {
        URL soundURL = getClass().getResource("/sound/" + soundFileName);
        if (soundURL != null) {
            Media sound = new Media(soundURL.toString());
           
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play(); // Play the specified sound
        } else {
            System.out.println("Sound file not found: " + soundFileName);
        }
    }

    public void startTrapTimer() {
        timer = new Timeline(
                new KeyFrame(Duration.seconds(3.84), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event){
                            trap = true;
                            System.out.println("Active: " + trap);
                        }
                    }
                ),
                
                new KeyFrame(Duration.seconds(4.8), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event){
                            trap = false;
                            System.out.println("Active: " + trap);                            
                        }
                    }
                )            
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();   
    }

    private void initVariables() {
        screenData = new short[mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks()];
        enemies[currentLevel].setEnemyX(new int[MAX_ENEMY]);
        enemies[currentLevel].setEnemyDx(new int[MAX_ENEMY]);
        enemies[currentLevel].setEnemyY(new int[MAX_ENEMY]);
        enemies[currentLevel].setEnemyDy(new int[MAX_ENEMY]);
        enemies[currentLevel].setEnemySpeed(new int[MAX_ENEMY]);
        enemies[currentLevel].setDx(new int[4]);
        enemies[currentLevel].setDy(new int[4]);
        phantom.setEnemyX(new int[MAX_ENEMY]);
        phantom.setEnemyDx(new int[MAX_ENEMY]);
        phantom.setEnemyY(new int[MAX_ENEMY]);
        phantom.setEnemyDy(new int[MAX_ENEMY]);
        phantom.setEnemySpeed(new int[MAX_ENEMY]);
        phantom.setDx(new int[4]);
        phantom.setDy(new int[4]);
    }

    private void playGame(GraphicsContext g2d) {
        if (dying) {
            death();
        } else {
            characters[characterNo].updateStamina();
            characters[characterNo].movePlayer();
            characters[characterNo].drawPlayer(g2d);
            enemies[currentLevel].moveEnemy(g2d);
            phantom.moveEnemy(g2d);
            checkMaze();
        }
    }

    private void showStartingText(GraphicsContext g2d) {
        String start = "Press SPACE to start";
        g2d.setFill(Color.WHITESMOKE);
        g2d.setFont(smallFont); 
        
        Text text = new Text(start);
        text.setFont(smallFont);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        // Calculate the coordinates to center the text
        double textX = (screenHSize - textWidth) / 2;
        double textY = (screenVSize + textHeight) / 2; // Adjust this to vertically center the text

        g2d.fillText(start, textX, textY);
    }

    private void drawScore(GraphicsContext g2d) {
        g2d.setFont(smallFont);
        g2d.setFill(Color.GREEN);
        String scoreText = "Score: " + characters[characterNo].getScore();
        double textWidth = smallFont.getSize() * scoreText.length() / 2;
        g2d.fillText(scoreText, screenHSize - textWidth - 10, screenVSize - 10);

        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heart, i * 28 + 8, screenVSize - 30);
        }
        
        g2d.setFill(Color.GREEN);
        g2d.fillRect(100, screenVSize - 30, characters[characterNo].getStamina()/5, 10);
        g2d.setStroke(Color.BLACK);
        g2d.strokeRect(100, screenVSize - 30, characters[characterNo].getStamina()/5, 10);
    }

   
    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
            initGame();
        }
        characters[characterNo].setPlayerX(8 * BLOCK_SIZE);
        characters[characterNo].setPlayerY(12 * BLOCK_SIZE);
        characters[characterNo].setPlayerDx(0);
        characters[characterNo].setPlayerDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;        
    }

    public void initGame() {
        currentLevel = 0;
        initLevel();
    }

    public void initLevel() {
        initVariables();
        characters[characterNo].setScore(0);
        lives = characters[characterNo].getLives();
        System.arraycopy(mazes[currentLevel].getLevelData(), 0, screenData, 0, mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks());
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;

        for (int i = 0; i < mazes[currentLevel].getEnemyCount(); i++) {
            enemies[currentLevel].setEnemyY(i, 4 * BLOCK_SIZE);
            enemies[currentLevel].setEnemyX(i, 4 * BLOCK_SIZE);
            enemies[currentLevel].setEnemyDy(i,0);
            enemies[currentLevel].setEnemyDx(i, dx);
            phantom.setEnemyY(i, 4 * BLOCK_SIZE);
            phantom.setEnemyX(i, 4 * BLOCK_SIZE);
            phantom.setEnemyDy(i,0);
            phantom.setEnemyDx(i, dx);

            dx = -dx;

            enemies[currentLevel].setEnemySpeed(i, validSpeed);
            phantom.setEnemySpeed(i, validSpeed);
        }

        characters[characterNo].setPlayerX(8 * BLOCK_SIZE);
        characters[characterNo].setPlayerY(12 * BLOCK_SIZE);
        characters[characterNo].setPlayerDx(0);
        characters[characterNo].setPlayerDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;
    }
    
    private void checkMaze() {
        // Iterate through screenData to check for remaining coins
        for (int i = 0; i < screenData.length; i++) {
            if ((screenData[i] & 16) != 0) {
                finished = false;
                break;
            }else{
                finished = true;
            }
        }


        // If no coins are left, the level is completed
        if (!finished && showscore) {

            loadScene("scoreboard.fxml");
            showscore = false;
        }
    }

    public void nextLevel(){
       
        currentLevel ++;
        
        if (currentLevel >= mazes.length) {
            currentLevel = 0; // Reset to first maze if there are no more levels
        }
        initLevel();
    }
    private void draw(GraphicsContext g2d) {
        g2d.drawImage(floor3, 0, 0, screenHSize, screenVSize);
        mazes[currentLevel].drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
            
        } else {
            showStartingText(g2d);
        }
    }@FXML
    public void loadScene(String file) {
        loadData();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(file));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            if (file.equals("select.fxml")) {
                SelectController controller = loader.getController();
                controller.select(this);
            } else if (file.equals("scoreboard.fxml")) {
                ScoreboardController controller = loader.getController();
                controller.scoreBoard(this);
            } else if (file.equals("pauseScreen.fxml")) {
                PauseScreenController controller = loader.getController();
                controller.pause(this);
            } else if (file.equals("setting.fxml")) {
                settingController controller = loader.getController();
                controller.setting(this);
            } else if (file.equals("login.fxml")) {
                LoginController controller = loader.getController();
                controller.login(this);
            }

    setScene(scene);


     
    
        } catch (IOException e) {
            e.printStackTrace();
        }
     }
     public void setScene(Scene scene){
         stage.setScene(scene);
         stage.show();
         stage.widthProperty().addListener((obs, oldVal, newVal) -> centerStage());
         stage.heightProperty().addListener((obs, oldVal, newVal) -> centerStage());
}

private void centerStage() {
    // Get primary screen bounds
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
 
    // Compute the center position
    double centerXPosition = primaryScreenBounds.getMinX() + (primaryScreenBounds.getWidth() - stage.getWidth()) / 2;
    double centerYPosition = primaryScreenBounds.getMinY() + (primaryScreenBounds.getHeight() - stage.getHeight()) / 2;

    // Set the position
    stage.setX(centerXPosition);
    stage.setY(centerYPosition);
}

     private void loadData() {
    String filePath = "./src/pacmanfx/data.bin";
    System.out.println("Loading data from: " + filePath);

    try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
        while (dis.available() > 0) {
            String keyType = dis.readUTF();
            if (!keyType.equals("volume")) {
                String keyValue = dis.readUTF();
                switch (keyType) {
                    case "rightKey":
                        moveRight = KeyCode.valueOf(keyValue);
                        break;
                    case "leftKey":
                        moveLeft = KeyCode.valueOf(keyValue);
                        break;
                    case "upKey":
                        moveUp = KeyCode.valueOf(keyValue);
                        break;
                    case "downKey":
                        moveDown = KeyCode.valueOf(keyValue);
                        break;
                    default:
                        System.err.println("Unknown key type: " + keyType);
                }
            } else {
                double savedVolume = dis.readDouble();
                System.out.println("Volume found but not used in model: " + savedVolume);
            }
        }

        // Initialize default keys if not found
        if (moveRight == null) moveRight = KeyCode.D;
        if (moveLeft == null) moveLeft = KeyCode.A;
        if (moveUp == null) moveUp = KeyCode.W;
        if (moveDown == null) moveDown = KeyCode.S;

        System.out.println("Loaded keys: Right=" + moveRight + ", Left=" + moveLeft + ", Up=" + moveUp + ", Down=" + moveDown);

    } catch (IOException | IllegalArgumentException e) {
        System.err.println("Error loading data: " + e.getMessage());
        e.printStackTrace();
        // Set default keys on error
        moveRight = KeyCode.D;
        moveLeft = KeyCode.A;
        moveUp = KeyCode.W;
        moveDown = KeyCode.S;
    }
}

    
        //volume.valueProperty().addListener((observable, oldValue, newValue) -> {
            //mediaPlayer.setVolume(newValue.doubleValue() / 100.0);
        //});
    private void handleMovement(KeyCode key) {
        int speed = 2; // Default walking speed

        if (characters[characterNo].getRunning()) {
            speed = 4; // Running speed
        }
        if (characters[characterNo].getSlowed()) {
            speed = 1; // Slowed speed
        }

        if (key.equals(moveRight)) {
            reqDx = speed;
            reqDy = 0;
        } else if (key.equals(moveLeft)) {
            reqDx = -speed;
            reqDy = 0;
        } else if (key.equals(moveUp)) {
            reqDx = 0;
            reqDy = -speed;
        } else if (key.equals(moveDown)) {
            reqDx = 0;
            reqDy = speed;
        }
    }
    public void exitApplication(){
        Platform.exit();
    }
}
    
