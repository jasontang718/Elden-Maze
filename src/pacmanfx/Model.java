package pacmanfx;

import java.net.URL;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import static javafx.application.Platform.exit;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.D;
import static javafx.scene.input.KeyCode.ESCAPE;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.SHIFT;
import static javafx.scene.input.KeyCode.W;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Model extends Application {
    private Player player = new Player(this);
    private Enemy enemy = new Enemy(this,player);
    private Maze1 maze1 = new Maze1(this);
    private Maze2 maze2 = new Maze2(this);
    
    private Maze[] mazes = new Maze[]{maze1,maze2};
    
    private final Font smallFont = Font.font("Times New Roman", FontWeight.BOLD,30);
    private boolean inGame = false;
    private boolean dying = false;

    private static final int BLOCK_SIZE = 40;

    private static final int MAX_ENEMY = 12;
    private static int validSpeed = 1;

    private int lives;
    private int score;
    private int currentLevel;
    
    private int screenHSize = mazes[currentLevel].getHBlocks() * BLOCK_SIZE;
    private int screenVSize = mazes[currentLevel].getVBlocks() * BLOCK_SIZE;
    private int screenSize = screenHSize*screenVSize;
    
    public Image heart, spider, floor3, floor2, coin, sword;
    public Image up, down, left, right, enhanced, background;

    private int reqDx = 0;
    private int reqDy = 0;
   
    short[] screenData;
    private Scene gameScene, introScene;
    
    private MediaPlayer mediaPlayer;


    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("League of Legends");

        // Create the intro scene
        Button startButton = new Button("Start Game");
        startButton.setFont(smallFont);
        startButton.setOnAction(e -> primaryStage.setScene(gameScene));
        
        Button settingsButton = new Button("Settings");
        settingsButton.setFont(smallFont);
        
        Button exitButton = new Button("Exit");
        exitButton.setFont(smallFont);
        exitButton.setOnAction(e -> exit());        
        
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
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(screenHSize, screenVSize);
        GraphicsContext g2d = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        gameScene = new Scene(root, screenHSize, screenVSize, Color.BLACK);

        gameScene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            if (inGame) {
                switch (key) {
                    case A:
                        reqDx = -2;
                        reqDy = 0;
                        
                            if (!player.getRunning()){
                                reqDx=-1;
                                reqDy=0;
                            }
                        break;
                        
                    case D:
                        reqDx = 2;
                        reqDy = 0;
                        
                            if (!player.getRunning()){
                                reqDx=1;
                                reqDy=0;
                            }
                        break;
                        
                    case W:
                        reqDx = 0;
                        reqDy = -2;
                        
                            if (!player.getRunning()){
                                reqDx=0;
                                reqDy=-1;
                            }
                        break;
                        
                    case S:
                        reqDx = 0;
                        reqDy = 2;
                        
                            if (!player.getRunning()){
                                reqDx=0;
                                reqDy=1;
                            }
                        break;
                        
                    case ESCAPE:
                        inGame = false;
                        break;
                        
                    case SHIFT:
                        player.setRunning(true);
                        break;
                        
                    default:
                        break;
                }
            } else {
                if (key == KeyCode.SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        });

        gameScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SHIFT) {
                player.setRunning(false);
            }
        });

        loadImages();
        initVariables();
        initGame();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw(g2d);
            }
        }.start();

        primaryStage.setScene(introScene);
        primaryStage.show();
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
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getReqDx() {
        return reqDx;
    }
    
    public int getReqDy() {
        return reqDy;
    }
    
    
    public void loadImages() {
        down = new Image(getClass().getResourceAsStream("/images/knightleft.gif"));
        up = new Image(getClass().getResourceAsStream("/images/knightright.gif"));
        left = new Image(getClass().getResourceAsStream("/images/knightleft.gif"));
        right = new Image(getClass().getResourceAsStream("/images/knightright.gif"));
        spider = new Image(getClass().getResourceAsStream("/images/spider.gif"));
        heart = new Image(getClass().getResourceAsStream("/images/heart.png"));
        floor3 = new Image(getClass().getResourceAsStream("/images/floor3.jpg"));
        floor2 = new Image(getClass().getResourceAsStream("/images/floor2.jpg"));
        coin = new Image(getClass().getResourceAsStream("/images/coin.gif"));
        sword = new Image(getClass().getResourceAsStream("/images/powerup.gif"));
        enhanced = new Image(getClass().getResourceAsStream("/images/powerupPlayer.gif"));
        background = new Image(getClass().getResourceAsStream("/images/background.jpg"));
    }
    
    public void playSound(String soundFileName) {
        URL soundURL = getClass().getResource("/sound/" + soundFileName);
        if (soundURL != null) {
            Media sound = new Media(soundURL.toString());
            if (mediaPlayer != null) {
                mediaPlayer.stop(); // Stop any currently playing sound
            }
            mediaPlayer = new MediaPlayer(sound);
            mediaPlayer.play(); // Play the specified sound
        } else {
            System.out.println("Sound file not found: " + soundFileName);
        }
    }
    
    private void initVariables() {
        screenData = new short[mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks()];
        enemy.setEnemyX(new int[MAX_ENEMY]);
        enemy.setEnemyDx(new int[MAX_ENEMY]);
        enemy.setEnemyY(new int[MAX_ENEMY]);
        enemy.setEnemyDy(new int[MAX_ENEMY]);
        enemy.setEnemySpeed(new int[MAX_ENEMY]);
        enemy.setDx(new int[4]);
        enemy.setDy(new int[4]);
    }

    private void playGame(GraphicsContext g2d) {
        if (dying) {
            death();
        } else {
            player.updateStamina();
            player.movePlayer();
            player.drawPlayer(g2d);
            enemy.moveEnemy(g2d);
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
        String s = "Score: " + score;
        double textWidth = smallFont.getSize() * s.length() / 2;
        g2d.fillText(s, screenHSize - textWidth - 10, screenVSize - 10);

        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heart, i * 28 + 8, screenVSize - 30);
        }
        
        g2d.setFill(Color.GREEN);
        g2d.fillRect(100, screenVSize - 30, player.getStamina()/5, 10);
        g2d.setStroke(Color.BLACK);
        g2d.strokeRect(100, screenVSize - 30, 100, 10);
    }

   
    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
        }
        continueLevel();
    }

    private void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        mazes[currentLevel].setEnemyCount(4);
    }

    private void initLevel() {
        System.arraycopy(mazes[currentLevel].getLevelData(), 0, screenData, 0, mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks());
        currentLevel = 1;
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;

        for (int i = 0; i < mazes[currentLevel].getEnemyCount(); i++) {
            enemy.setEnemyY(i, 4 * BLOCK_SIZE);
            enemy.setEnemyX(i, 4 * BLOCK_SIZE);
            enemy.setEnemyDy(i,0);
            enemy.setEnemyDx(i, dx);

            dx = -dx;

            enemy.setEnemySpeed(i, validSpeed);
        }

        player.setPlayerX(8 * BLOCK_SIZE);
        player.setPlayerY(12 * BLOCK_SIZE);
        player.setPlayerDx(0);
        player.setPlayerDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;
    }
    
    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < mazes[currentLevel].getHBlocks() * mazes[currentLevel].getVBlocks() && finished) {
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {
            score += 50;
            int enemyCount = mazes[currentLevel].getEnemyCount();
            if (enemyCount < MAX_ENEMY) {
                enemyCount--;
                mazes[currentLevel].setEnemyCount(enemyCount);
            }

            initLevel();
        }
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
    }

}