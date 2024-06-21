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
import static javafx.scene.input.KeyCode.W;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Model extends Application {
    private Player player = new Player(this);
    private Maze maze = new Maze(this);
    
    private final Font smallFont = Font.font("Times New Roman", FontWeight.BOLD,30);
    private boolean inGame = false;
    private boolean dying = false;

    private static final int BLOCK_SIZE = 40;
    private final int SCREEN_SIZE = maze.getNBlocks() * BLOCK_SIZE;
    private static final int MAX_GHOSTS = 12;
    private static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8};
    private static final int MAX_SPEED = 6;

    private int nGhosts = 6;
    int lives;
    private int score;

    public Image heart, spider, floor3, floor2, coin, sword;
    public Image up, down, left, right, enhanced, background;

    private int reqDx = 0;
    private int reqDy = 0;
   

    private int currentSpeed = 1;
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
        
        Button exitButton = new Button("Exit");
        exitButton.setFont(smallFont);
        exitButton.setOnAction(e -> exit());        
        
        Label title = new Label("LOL");
        title.setStyle("-fx-font: normal bold 50px 'serif';" + "-fx-text-fill: maroon;");

        VBox introLayout = new VBox(20);
        introLayout.setPrefSize(728, 403);
        introLayout.setStyle("-fx-background-image: url('/images/background.jpg');" + "-fx-background-size: cover;" + "-fx-background-repeat: stretch;" + "-fx-background-position: center center;");
        introLayout.setAlignment(Pos.CENTER);
        introLayout.getChildren().add(title);
        introLayout.getChildren().add(startButton);
        introLayout.getChildren().add(exitButton);        

        introScene = new Scene(introLayout);

        // Create the game scene
        StackPane root = new StackPane();
        Canvas canvas = new Canvas(SCREEN_SIZE, SCREEN_SIZE + 50);
        GraphicsContext g2d = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        gameScene = new Scene(root, SCREEN_SIZE, SCREEN_SIZE + 50, Color.BLACK);

        gameScene.setOnKeyPressed(event -> {
            KeyCode key = event.getCode();

            if (inGame) {
                switch (key) {
                    case A:
                        reqDx = -1;
                        reqDy = 0;
                        break;
                    case D:
                        reqDx = 1;
                        reqDy = 0;
                        break;
                    case W:
                        reqDx = 0;
                        reqDy = -1;
                        break;
                    case S:
                        reqDx = 0;
                        reqDy = 1;
                        break;
                    case ESCAPE:
                        inGame = false;
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

    public boolean getInGame(){
        return inGame;
    }
    
    public void setDying(boolean dying){
        this.dying = dying;
    }
    
    public int getNGhosts(){
        return nGhosts;
    }
    
    public void setNGhosts(int nGhosts){
        this.nGhosts = nGhosts;
    }
    
    public int getBlockSize(){
        return BLOCK_SIZE;
    }
    
    public short[] getScreenData() {
        return screenData;
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
        screenData = new short[maze.getNBlocks() * maze.getNBlocks()];
        player.ghostX = new int[MAX_GHOSTS];
        player.ghostDx = new int[MAX_GHOSTS];
        player.ghostY = new int[MAX_GHOSTS];
        player.ghostDy = new int[MAX_GHOSTS];
        player.ghostSpeed = new int[MAX_GHOSTS];
        player.dx = new int[4];
        player.dy = new int[4];
    }

    private void playGame(GraphicsContext g2d) {
        if (dying) {
            death();
        } else {
            player.movePacman();
            player.drawPacman(g2d);
            player.moveGhosts(g2d);
            maze.checkMaze();
        }
    }

    private void showStartingScreen(GraphicsContext g2d) {
        String start = "Press SPACE to start";
        g2d.setFill(Color.YELLOW);
        g2d.setFont(smallFont);
        g2d.fillText(start, SCREEN_SIZE / 2, 150);
    }

    private void drawScore(GraphicsContext g2d) {
        g2d.setFont(smallFont);
        g2d.setFill(new Color(5 / 255.0, 181 / 255.0, 79 / 255.0, 1));
        String s = "Score: " + score;
        g2d.fillText(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);

        for (int i = 0; i < lives; i++) {
            g2d.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1);
        }
    }

   
    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
        }
        continueLevel();
    }

    
    

    public void drawGhost(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(spider, x, y);
    }



    private void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        nGhosts = 2;
        currentSpeed = 1;
    }

    void initLevel() {
        System.arraycopy(maze.levelData, 0, screenData, 0, maze.getNBlocks() * maze.getNBlocks());
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;

        for (int i = 0; i < nGhosts; i++) {
            player.ghostY[i] = 4 * BLOCK_SIZE;
            player.ghostX[i] = 4 * BLOCK_SIZE;
            player.ghostDy[i] = 0;
            player.ghostDx[i] = dx;
            dx = -dx;
            int random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            player.ghostSpeed[i] = VALID_SPEEDS[random];
        }

        player.setPacmanX(9 * BLOCK_SIZE);
        player.setPacmanY(12 * BLOCK_SIZE);
        player.setPacmanDx(0);
        player.setPacmanDy(0);
        reqDx = 0;
        reqDy = 0;
        dying = false;
    }

    private void draw(GraphicsContext g2d) {
        g2d.drawImage(floor3, 0, 0, SCREEN_SIZE, SCREEN_SIZE+50);

        maze.drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showStartingScreen(g2d);
        }
    }
}