package pacmanfx;
//ok
import com.sun.javafx.sg.prism.NGCanvas;
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
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
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

    private final Font smallFont = Font.font("Times New Roman", FontWeight.BOLD,30);
    private boolean inGame = false;
    private boolean dying = false;
    private boolean powerup = false;

    private static final int BLOCK_SIZE = 40;
    private static final int N_BLOCKS = 17;
    private static final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE;
    private static final int MAX_GHOSTS = 12;
    private static final int PACMAN_SPEED = 2;
    private static final int[] VALID_SPEEDS = {1, 2, 3, 4, 6, 8};
    private static final int MAX_SPEED = 6;

    private int nGhosts = 6;
    private int lives, score;
    private int[] dx, dy;
    private int[] ghostX, ghostY, ghostDx, ghostDy, ghostSpeed;

    private Image heart, spider, floor3, floor2, coin, sword;
    private Image up, down, left, right, enhanced, background;
    private Timeline powerupTimer;

    private int pacmanX, pacmanY, pacmanDx, pacmanDy;
    private int reqDx, reqDy;

    private final short[] levelData = {
         0, 0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0, 
         0, 19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22, 0, 
         0, 17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20, 0, 
         0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,  0, 21, 0, 
         0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,  0, 21, 0, 
         0, 17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,  0, 21, 0, 
         0, 17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20, 0, 
         0, 17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20, 0, 
         0, 17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0, 
         0, 25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 44, 0, 
         0, 0,   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0
    };

    private int currentSpeed = 1;
    private short[] screenData;
    private Scene gameScene, introScene;
    
    private Media powerupSound;
    private MediaPlayer mediaPlayer;


    @Override
    public void start(Stage primaryStage) {
    
        playSound("powerup.mp3");
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

    private void loadImages() {
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
    private void playSound(String soundFileName) {
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
        screenData = new short[N_BLOCKS * N_BLOCKS];
        ghostX = new int[MAX_GHOSTS];
        ghostDx = new int[MAX_GHOSTS];
        ghostY = new int[MAX_GHOSTS];
        ghostDy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
    }

    private void playGame(GraphicsContext g2d) {
        if (dying) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
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

    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {
            score += 50;
            if (nGhosts < MAX_GHOSTS) {
                nGhosts++;
            }
            if (currentSpeed < MAX_SPEED) {
                currentSpeed++;
            }
            initLevel();
        }
    }

    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
        }
        continueLevel();
    }

    private void moveGhosts(GraphicsContext g2d) {
        int pos;
        int count;

        for (int i = 0; i < nGhosts; i++) {
            if (ghostX[i] % BLOCK_SIZE == 0 && ghostY[i] % BLOCK_SIZE == 0) {
                pos = ghostX[i] / BLOCK_SIZE + N_BLOCKS * (ghostY[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghostDx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghostDy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghostDx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghostDy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((screenData[pos] & 15) == 15) {
                        ghostDx[i] = 0;
                        ghostDy[i] = 0;
                    } else {
                        ghostDx[i] = -ghostDx[i];
                        ghostDy[i] = -ghostDy[i];
                    }
                } else {
                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghostDx[i] = dx[count];
                    ghostDy[i] = dy[count];
                }
            }

            ghostX[i] += ghostDx[i] * ghostSpeed[i];
            ghostY[i] += ghostDy[i] * ghostSpeed[i];
            drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);

            
            
            if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12) && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12) && inGame) {
                if (!powerup) {
                    dying = true;
                }
                else {
                    removeGhost(i);
                }
            }
        }
    }
    
    private void removeGhost(int index) {
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < nGhosts - 1; i++) {
            ghostX[i] = ghostX[i + 1];
            ghostY[i] = ghostY[i + 1];
            ghostDx[i] = ghostDx[i + 1];
            ghostDy[i] = ghostDy[i + 1];
            ghostSpeed[i] = ghostSpeed[i + 1];
        }
         playSound("kill.mp3");
        nGhosts--; // Decrease the count of ghosts
    }

    private void drawGhost(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(spider, x, y);
    }

   private void movePacman() {
    int pos;
    short ch;

    if (pacmanX % BLOCK_SIZE == 0 && pacmanY % BLOCK_SIZE == 0) {
        pos = pacmanX / BLOCK_SIZE + N_BLOCKS * (pacmanY / BLOCK_SIZE);
        ch = screenData[pos];

        if ((ch & 16) != 0) {
            // Pac-Man eats a dot
            screenData[pos] = (short) (ch & 15); // Remove the dot
        
            score++;
        }
        
        if ((ch & 32) != 0) {
            screenData[pos] = (short) (ch & 31); // Remove the dot
            score += 50;
            checkPowerUp();
        }

        // Check if the requested direction is valid
        if (reqDx != 0 || reqDy != 0) {
            if (!((reqDx == -1 && reqDy == 0 && (ch & 1) != 0)
                    || (reqDx == 1 && reqDy == 0 && (ch & 4) != 0)
                    || (reqDx == 0 && reqDy == -1 && (ch & 2) != 0)
                    || (reqDx == 0 && reqDy == 1 && (ch & 8) != 0))) {
                pacmanDx = reqDx;
                pacmanDy = reqDy;
            }
        }

        // Check for collisions with walls
        if ((pacmanDx == -1 && pacmanDy == 0 && (ch & 1) != 0)
                || (pacmanDx == 1 && pacmanDy == 0 && (ch & 4) != 0)
                || (pacmanDx == 0 && pacmanDy == -1 && (ch & 2) != 0)
                || (pacmanDx == 0 && pacmanDy == 1 && (ch & 8) != 0)) {
            pacmanDx = 0;
            pacmanDy = 0;
        }
    }
    pacmanX += PACMAN_SPEED * pacmanDx;
    pacmanY += PACMAN_SPEED * pacmanDy;
}

  private void checkPowerUp() {
    if (!powerup) {
        powerup = true;
        startTimer();
    } else {
        resetTimer();
    }
  }

    private void startTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerup = false;
            }
        }));
        powerupTimer.play();   
    }
    
    private void resetTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startTimer();
    }

    private void drawPacman(GraphicsContext g2d) {
        if (!powerup) {
            if (reqDx == -1) {
                g2d.drawImage(left, pacmanX + 1, pacmanY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(right, pacmanX + 1, pacmanY + 1);
            } else if (reqDy == -1) {
                g2d.drawImage(up, pacmanX + 1, pacmanY + 1);
            } else {
                g2d.drawImage(down, pacmanX + 1, pacmanY + 1);
            }
        }
        else {
            if (reqDx == -1) {
                g2d.drawImage(enhanced, pacmanX + 1, pacmanY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(enhanced, pacmanX + 1, pacmanY + 1);
            } else if (reqDy == -1) {
                g2d.drawImage(enhanced, pacmanX + 1, pacmanY + 1);
            } else {
                g2d.drawImage(enhanced, pacmanX + 1, pacmanY + 1);
            }        
        }
    }

    private void drawMaze(GraphicsContext g2d) {
    short i = 0;
    int x, y;

    for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
        for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

            g2d.setStroke(Color.GREY);
            g2d.setLineWidth(5);

            if (levelData[i] == 0) {
                g2d.drawImage(floor2, x, y);
            }
            
            if ((levelData[i] & 1) != 0) {
                g2d.strokeLine(x, y, x, y + BLOCK_SIZE - 1);
            }

            if ((levelData[i] & 2) != 0) {
                g2d.strokeLine(x, y, x + BLOCK_SIZE - 1, y);
            }

            if ((levelData[i] & 4) != 0) {
                g2d.strokeLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
            }

            if ((levelData[i] & 8) != 0) {
                g2d.strokeLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1, y + BLOCK_SIZE - 1);
            }

            if ((screenData[i] & 16) != 0) {
                double coinX = x + BLOCK_SIZE/2 - coin.getWidth()/2;
                double coinY = y + BLOCK_SIZE/2 - coin.getHeight()/2;
                g2d.drawImage(coin, coinX, coinY);
            
            }
            
            if ((screenData[i] & 32) != 0) {
                double powerupX = x + BLOCK_SIZE/2 - sword.getWidth()/2;
                double powerupY = y + BLOCK_SIZE/2 - sword.getHeight()/2;
                g2d.drawImage(sword, powerupX, powerupY);
                
            }

            i++;
        }
    }
}

    private void initGame() {
        lives = 3;
        score = 0;
        initLevel();
        nGhosts = 2;
        currentSpeed = 1;
    }

    private void initLevel() {
        System.arraycopy(levelData, 0, screenData, 0, N_BLOCKS * N_BLOCKS);
        continueLevel();
    }

    private void continueLevel() {
        int dx = 1;

        for (int i = 0; i < nGhosts; i++) {
            ghostY[i] = 4 * BLOCK_SIZE;
            ghostX[i] = 4 * BLOCK_SIZE;
            ghostDy[i] = 0;
            ghostDx[i] = dx;
            dx = -dx;
            int random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = VALID_SPEEDS[random];
        }

        pacmanX = 9 * BLOCK_SIZE;
        pacmanY = 12 * BLOCK_SIZE;
        pacmanDx = 0;
        pacmanDy = 0;
        reqDx = 0;
        reqDy = 0;
        dying = false;
    }

    private void draw(GraphicsContext g2d) {
        g2d.drawImage(floor3, 0, 0, SCREEN_SIZE, SCREEN_SIZE+50);

        drawMaze(g2d);
        drawScore(g2d);

        if (inGame) {
            playGame(g2d);
        } else {
            showStartingScreen(g2d);
        }
    }

}
