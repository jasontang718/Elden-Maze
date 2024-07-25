package Game;

import java.io.DataInputStream;
import java.io.FileInputStream;

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

public class Controller extends Application {
    private final Font smallFont = Font.font("STFangsong", FontWeight.BOLD,30);
    private boolean inGame = false;
    private boolean dying = false;
    private int lives;
    private boolean showScore = true; // Class-level variable
    private static final int BLOCK_SIZE = 40;
    private static final int MAX_ENEMY = 6;
    private static int validSpeed = 1;
    
    private Timeline timer;
    private MediaPlayer mediaPlayer;
    private double volume = 0.5;

    private int currentLevel = 0;
    
    public Image mazeFloor1, mazeFloor2, mazeFloor3, mazeWall1, mazeWall2, mazeWall3;
    public Image heart, coin, powerOrb, blinded, frozen;
    public Image spiderImage, skeletonImage, goblinImage, phantomImage, fire, spike;
    public Image knightUp, knightDown, knightLeft, knightRight, powerKnightUp, powerKnightDown, powerKnightLeft, powerKnightRight, background, assassinImage;
    public Image assassinUp, assassinDown, assassinLeft, assassinRight, powerAssassinUp, powerAssassinDown, powerAssassinLeft, powerAssassinRight;
    public Image mageUp, mageDown, mageLeft, mageRight, powerMage;

    private int reqDx = 0;
    private int reqDy = 0;
   
    private Stage stage;

    private short[] screenData;
    private Scene gameScene;
   
    private Scene introScene;
    
    private boolean finished = true;
    private boolean trap;
    private Map<String, KeyCode> keyMap = new HashMap<>();
    private int characterNo;
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
    private Skeleton skeleton = new Skeleton(this, characters);
    private Phantom phantom = new Phantom(this, characters);   
    private Enemy[] enemies = new Enemy[]{spider, goblin, skeleton};
    
    private int screenHSize = mazes[currentLevel].getHBlocks() * BLOCK_SIZE;
    private int screenVSize = mazes[currentLevel].getVBlocks() * BLOCK_SIZE;
    private int screenSize = screenHSize*screenVSize;
    
    
    //Create the game lobby and in game scene (pages, gameplay etc)
    //Call login scene
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        loadScene("login.fxml");
        // Set the stage
        primaryStage.setTitle("Elden Maze");

        //Create the intro scene
        Button startButton = new Button("Start Game");
        startButton.setFont(smallFont);
        startButton.setOnAction(event -> loadScene("select.fxml"));  
        
        Button settingsButton = new Button("Settings");
        settingsButton.setFont(smallFont);
        settingsButton.setOnAction(event -> loadScene("setting.fxml")); 
        
        Button exitButton = new Button("Exit");
        exitButton.setFont(smallFont);
        exitButton.setOnAction(e -> exitApplication());

        double buttonWidth = 250;
        double buttonHeight = 50;
        
        startButton.setPrefSize(buttonWidth, buttonHeight);
        settingsButton.setPrefSize(buttonWidth, buttonHeight);
        exitButton.setPrefSize(buttonWidth, buttonHeight);

        Label title = new Label("Elden Maze");
        title.setStyle("-fx-font: normal bold 50px 'STFangsong';" + "-fx-text-fill: maroon;");

        VBox introLayout = new VBox(20);
        introLayout.setPrefSize(728, 403);
        introLayout.setStyle("-fx-background-image: url('/images/maze/background.jpg');" + "-fx-background-size: cover;" 
                             + "-fx-background-repeat: stretch;" + "-fx-background-position: center center;");
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
        //KeyCode actions
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
    
    public Scene getGameScene() {
        return gameScene;
    }
    
    public Scene getintroScene() {
        return introScene;
    }
    
    public void setInGame(boolean inGame){
        this.inGame = inGame;
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
    
    public void setCurrentLevel(int currentLevel){
        this.currentLevel = currentLevel;
    }
    
    public boolean getInGame(){
        return inGame;
    }
    
    public void setDying(boolean dying){
        this.dying = dying;
    }
    
    public void setShowScore(boolean showScore){
        this.showScore = showScore;
    }
    
    public void setFinished(boolean finished){
        this.finished = finished;
    }
    
    public MediaPlayer getmediaPlayer(){
        return mediaPlayer;
    }
    
    public void setMediaPlayer(MediaPlayer mediaplayer){
        this.mediaPlayer = mediaplayer;
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
    
    //Preloads the images to reduce buffering when the game runs and prevents lag
    public void loadImages() {
        blinded = new Image(getClass().getResourceAsStream("/images/maze/blinded.png"),4000,2247,false,false);    
        frozen = new Image(getClass().getResourceAsStream("/images/maze/frozen.png"),screenHSize,screenVSize,false,false);    
        
        knightDown = new Image(getClass().getResourceAsStream("/images/knight/knightDown.gif"));
        knightUp = new Image(getClass().getResourceAsStream("/images/knight/knightUp.gif"));
        knightLeft = new Image(getClass().getResourceAsStream("/images/knight/knightLeft.gif"));
        knightRight = new Image(getClass().getResourceAsStream("/images/knight/knightRight.gif"));
        powerKnightDown = new Image(getClass().getResourceAsStream("/images/knight/powerKnightDown.gif"));
        powerKnightUp = new Image(getClass().getResourceAsStream("/images/knight/powerKnightUp.gif"));
        powerKnightLeft = new Image(getClass().getResourceAsStream("/images/knight/powerKnightLeft.gif"));
        powerKnightRight = new Image(getClass().getResourceAsStream("/images/knight/powerKnightRight.gif"));
        
        assassinDown = new Image(getClass().getResourceAsStream("/images/assasin/assassinDown.gif"));
        assassinUp = new Image(getClass().getResourceAsStream("/images/assasin/assassinUp.gif"));
        assassinLeft = new Image(getClass().getResourceAsStream("/images/assasin/assassinLeft.gif"));
        assassinRight = new Image(getClass().getResourceAsStream("/images/assasin/assassinRight.gif"));
        powerAssassinDown = new Image(getClass().getResourceAsStream("/images/assasin/powerAssassinDown.gif"));
        powerAssassinUp = new Image(getClass().getResourceAsStream("/images/assasin/powerAssassinUp.gif"));
        powerAssassinLeft = new Image(getClass().getResourceAsStream("/images/assasin/powerAssassinLeft.gif"));
        powerAssassinRight = new Image(getClass().getResourceAsStream("/images/assasin/powerAssassinRight.gif"));
        
        mageDown = new Image(getClass().getResourceAsStream("/images/mage/mageDown.gif"));
        mageUp = new Image(getClass().getResourceAsStream("/images/mage/mageUp.gif"));
        mageLeft = new Image(getClass().getResourceAsStream("/images/mage/mageLeft.gif"));
        mageRight = new Image(getClass().getResourceAsStream("/images/mage/mageRight.gif"));
        powerMage = new Image(getClass().getResourceAsStream("/images/mage/powerMage.gif"));
        
        mazeFloor1 = new Image(getClass().getResourceAsStream("/images/maze/castlefloor.jpg"));        
        mazeFloor2 = new Image(getClass().getResourceAsStream("/images/maze/forestfloor.png"));
        mazeFloor3 = new Image(getClass().getResourceAsStream("/images/maze/hellfloor.png"));
        
        mazeWall1 = new Image(getClass().getResourceAsStream("/images/maze/castlewall.png"));
        mazeWall2 = new Image(getClass().getResourceAsStream("/images/maze/forestwall.png"));
        mazeWall3 = new Image(getClass().getResourceAsStream("/images/maze/hellwall.png"));
        
        heart = new Image(getClass().getResourceAsStream("/images/maze/heart.png"));
        coin = new Image(getClass().getResourceAsStream("/images/maze/coin.gif"));
        powerOrb = new Image(getClass().getResourceAsStream("/images/maze/powerup.gif"));
        background = new Image(getClass().getResourceAsStream("/images/maze/background.jpg"));
        spiderImage = new Image(getClass().getResourceAsStream("/images/maze/spider.gif"));
        assassinImage = new Image(getClass().getResourceAsStream("/images/assasin/assassinDown.gif"));
        skeletonImage = new Image(getClass().getResourceAsStream("/images/maze/skeleton.gif"));
        goblinImage = new Image(getClass().getResourceAsStream("/images/maze/goblin.gif"));
        phantomImage = new Image(getClass().getResourceAsStream("/images/maze/phantom.gif"));        
        fire = new Image(getClass().getResourceAsStream("/images/maze/fire.gif"));
        spike = new Image(getClass().getResourceAsStream("/images/maze/spike.gif"));
    }
    
    //Plays the music and sound effects
    public void playSound(String soundFileName, boolean stopAudio) {
       URL soundURL = getClass().getResource("/sound/" + soundFileName);
       if (soundURL == null) {
           System.out.println("Sound file not found: " + soundFileName);
           return;
       }

       Media sound = new Media(soundURL.toString());

       if (mediaPlayer != null && stopAudio) {
           mediaPlayer.stop();
           mediaPlayer.dispose(); // Dispose of the previous MediaPlayer
       }
       //Set volume
       mediaPlayer = new MediaPlayer(sound);
       mediaPlayer.setVolume(volume / 100.0);

       mediaPlayer.setOnError(() -> {
           System.out.println("Error occurred while playing sound: " + mediaPlayer.getError());
       });

       mediaPlayer.play();
   }

    //Initializes the game
    public void initGame() {
      
        currentLevel = 0;
        initLevel();
    }

    //Initializes the level
    public void initLevel() {
        initVariables();
        characters[characterNo].setPowerUp(false);
        characters[characterNo].setDebuff(false);
        characters[characterNo].setScore(0);
        lives = characters[characterNo].getLives();
        System.arraycopy(mazes[currentLevel].getLevelData(), 0, screenData, 0, mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks());
        continueLevel();
    }
    
    //Initialize the variables needed before starting the game
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
        showScore = true;
        finished = false;
    }

    //Can be a continuation of the initLevel method, or continues the level after the player dies, respawning the enemies and players back at their spawnpoints, as well as setting the speed for the enemies
    private void continueLevel() {
        int dx = 1;
        for (int i = 0; i < mazes[currentLevel].getEnemyCount(); i++) {
            //Sets the spawn point of enemies
            enemies[currentLevel].setEnemyY(i, 1 * BLOCK_SIZE);
            enemies[currentLevel].setEnemyX(i, 1 * BLOCK_SIZE);
            enemies[currentLevel].setEnemyDy(i,0);
            enemies[currentLevel].setEnemyDx(i, dx);
            phantom.setEnemyY(i, 22 * BLOCK_SIZE);
            phantom.setEnemyX(i, 23 * BLOCK_SIZE);
            phantom.setEnemyDy(i,0);
            phantom.setEnemyDx(i, dx);

            dx = -dx;

            enemies[currentLevel].setEnemySpeed(i, validSpeed);
            phantom.setEnemySpeed(i, validSpeed);
        }
        //Sets the spawn point of the player
        characters[characterNo].setPlayerX(12 * BLOCK_SIZE);
        characters[characterNo].setPlayerY(14 * BLOCK_SIZE);
        characters[characterNo].setPlayerDx(0);
        characters[characterNo].setPlayerDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;
    }
    
    //Handles the methods that are needed during the game
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
    
    //Iterate through each tile of the maze to check for remaining coins, if none are present, the level is finished and the scoreboard is shown
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
        if (!finished && showScore) {
            loadScene("scoreboard.fxml");
            showScore = false;
        }
    }

    //Changes the current level after a level is cleared
    public void nextLevel(){
        currentLevel ++;
        
        if (currentLevel >= mazes.length) {
            currentLevel = 0; // Reset to first maze if there are no more levels
        }
        initLevel();
    }
    
    //Sets the timer for each interval of trap damage
    public void startTrapTimer() {
        timer = new Timeline(
                //Sets trap to active
                new KeyFrame(Duration.seconds(3.84), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event){
                            trap = true;
                            System.out.println("Active: " + trap); //code for debugging
                        }
                    }
                ),
                //Resets trap to inactive
                new KeyFrame(Duration.seconds(4.8), new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event){
                            trap = false;
                            System.out.println("Active: " + trap); //code for debugging          
                        }
                    }
                )            
        );
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();   
    }
    
    //Handles the deaths of the player and respawn the player back at the spawn point
    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
            initGame();
        }
        //Respawns the player at spawn point and resets all statuses
        characters[characterNo].setPlayerX(12 * BLOCK_SIZE);
        characters[characterNo].setPlayerY(14 * BLOCK_SIZE);
        characters[characterNo].setPlayerDx(0);
        characters[characterNo].setPlayerDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;       
        characters[characterNo].setPowerUp(false);
        characters[characterNo].setDebuff(false);
    }
    
    //Shows a starting text before a game starts to let the player to prepare
    private void showStartingText(GraphicsContext g2d) {
        String start = "Press SPACE to start";
        g2d.setFill(Color.WHITESMOKE);
        g2d.setFont(smallFont); 
        
        Text text = new Text(start);
        text.setFont(smallFont);
        double textWidth = text.getLayoutBounds().getWidth();
        double textHeight = text.getLayoutBounds().getHeight();

        // Calculate the screen size to center the text
        double textX = (screenHSize - textWidth) / 2;
        double textY = (screenVSize + textHeight) / 2;

        g2d.fillText(start, textX, textY);
    }

    //Draws the score, lives, and stamina bar at the bottom of the maze
    private void drawScore(GraphicsContext g2d) {
        //Draws the score
        g2d.setFont(smallFont);
        g2d.setFill(Color.GREEN);
        String scoreText = "Score: " + characters[characterNo].getScore();
        double textWidth = smallFont.getSize() * scoreText.length() / 2;
        g2d.fillText(scoreText, screenHSize - textWidth - 10, screenVSize - 10);

        //Draws the lives
        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heart, i * 28 + 8, screenVSize - 30);
        }
        
        //Draws the stamina bar
        g2d.setFill(Color.GREEN);
        g2d.fillRect(170, screenVSize - 25, characters[characterNo].getStamina()/2, 10);
        g2d.setStroke(Color.BLACK);
        if (characters[characterNo] == characters[0] || characters[characterNo] == characters[2]){
            g2d.strokeRect(170, screenVSize - 25, 300/2, 10);
        }
        else if (characters[characterNo] == characters[1]){
            g2d.strokeRect(170, screenVSize - 25, 500/2, 10);
        }
    }
    
    //Draws the texture of the mazes, and other related graphical content
    private void draw(GraphicsContext g2d) {
        //draws the backgrounds of different levels
        if (currentLevel == 0){
            g2d.drawImage(mazeFloor1, 0, 0, screenHSize, screenVSize);            
        }
        else if (currentLevel == 1){
            g2d.drawImage(mazeFloor2, 0, 0, screenHSize, screenVSize);            
        }
        else if (currentLevel == 2){
            g2d.drawImage(mazeFloor3, 0, 0, screenHSize, screenVSize);                    
        }
        
        //draws the maze layout
        mazes[currentLevel].drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        }
        else {
            showStartingText(g2d);
        }
    }
    
    @FXML
    //Pass instance to different scene file
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
                playSound("login.mp3", true);
            }

        setScene(scene);
     
    
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Load the scene
    public void setScene(Scene scene){
        loadData();
        stage.setScene(scene);
        //Dispose previous audio
        if (scene == introScene) {
           mediaPlayer.dispose();
           playSound("lobby.mp3", true); // Play sound
        }else if(scene == gameScene){
           mediaPlayer.dispose();
            
        }
          stage.show();
          stage.widthProperty().addListener((obs, oldVal, newVal) -> centerStage());
          stage.heightProperty().addListener((obs, oldVal, newVal) -> centerStage());
          centerStage();
        }
     //Centre the stage 
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
    //Read data from file
    private void loadData() {
      
        String filePath = "./src/Game/data.bin";
        System.out.println("Loading data from: " + filePath);
          //Load file from directory
        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))){
            //read binary file
            while (dis.available() > 0) {
                String keyType = dis.readUTF();
                if (keyType.equals("volume")) {
                volume = dis.readDouble(); // Read and set the volume
            } else {
                //Read and set keybind
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
                }
            }}

        // Initialize default keys if not found
        if (moveRight == null) moveRight = KeyCode.D;
        if (moveLeft == null) moveLeft = KeyCode.A;
        if (moveUp == null) moveUp = KeyCode.W;
        if (moveDown == null) moveDown = KeyCode.S;
        
        System.out.println("Loaded keys: Right=" + moveRight + ", Left=" + moveLeft + ", "
                + "Up=" + moveUp + ", Down=" + moveDown);

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
    //Decide how fast the character move
    private void handleMovement(KeyCode key) {
        int speed = 2; // Default walking speed

        if (characters[characterNo].getRunning()) {
            speed = 4; // Running speed
        }
        if (characters[characterNo].getDebuff()) {
            speed = 1; // Slowed speed
        }
        //Set movement direction
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
    //Quit game
    public void exitApplication(){
        Platform.exit();
    }
}
    
