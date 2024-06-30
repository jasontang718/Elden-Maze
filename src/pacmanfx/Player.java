package pacmanfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class Player {
    private Model model;
    private Maze maze;
    private int score = 0;
    private int stamina = 500;
    private boolean running = false;
    public int playerX, playerY, playerDx, playerDy;
    private int playerSpeed = 2;
    private Timeline powerupTimer;
    private boolean powerUp = false;

    // Constructor to receive Model instance
    public Player(Model model) {
        this.model = model;
        this.maze = new Maze(model);
    }
    
    public void setRunning(boolean value){
        this.running = value;
    }
    public boolean getRunning(){
        return running;
    }
    public int getStamina(){
        return stamina;
    }
    
    public int getPlayerX(){
        return playerX;
    }
    
    public int getPlayerY(){
        return playerY;
    }

    public void setPlayerX(int x) {
        this.playerX = x;
    }
    
    public void setPlayerY(int y) {
        this.playerY = y;
    }
    
    public void setPlayerDx(int dx) {
        this.playerDx = dx;
    }

    public void setPlayerDy(int dy) {
        this.playerDy = dy;
    }
    
    public boolean getPowerUp() {
        return powerUp;
    }
    
    public void setPowerUp(boolean value){
        this.powerUp = value;
    }
    
    public void setPlayerSpeed(int speed){
        this.playerSpeed = speed;
    }
    
    public int getPlayerSpeed(){
        return playerSpeed;
    }
    
   public void movePlayer() {
        int pos;
        short ch;
        int BLOCK_SIZE = model.getBlockSize();
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();

        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + maze.getNBlocks() * (playerY / BLOCK_SIZE);
            ch = model.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the dot
                score++;
                model.setScore(score);
                model.playSound("gold.mp3");        

            }

            if ((ch & 32) != 0) {
                model.getScreenData()[pos] = (short) (ch & 31); // Remove the dot
                score += 50;
                model.setScore(score);
                model.playSound("powerup.mp3");
                checkPowerUp();
            }

            // Check if the requested direction is valid
            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx <= -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx >= 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy <= -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy >= 1 && (ch & 8) != 0))) {
                    playerDx = reqDx;
                    playerDy = reqDy;
                }
            }

            // Check for collisions with walls
        if ((playerDx <= -1 && playerDy == 0 && (ch & 1) != 0 && (ch & 0) == 0)
                || (playerDx >= 1 && playerDy == 0 && (ch & 4) != 0 && (ch & 0) == 0)
                || (playerDx == 0 && playerDy <= -1 && (ch & 2) != 0 && (ch & 0) == 0)
                || (playerDx == 0 && playerDy >= 1 && (ch & 8) != 0) && (ch & 0) == 0) {
            playerDx = 0;
            playerDy = 0;
        }
        }
        playerX += playerSpeed * playerDx;
        playerY += playerSpeed * playerDy;
        
        System.out.println("Before movement - PacmanX: " + playerX + ", PacmanY: " + playerY);
        System.out.println("Dx: " + playerDx + ", Dy: " + playerDy);


        // Debugging output after movement
        System.out.println("After movement - PacmanX: " + playerX + ", PacmanY: " + playerY);
    }


    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startTimer();
        } else {
            resetTimer();
        }
    }
    
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(model.left, playerX + 1, playerY + 1);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.right, playerX + 1, playerY + 1);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.up, playerX + 1, playerY + 1);
            } else {
                g2d.drawImage(model.down, playerX + 1, playerY + 1);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            } else {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            }        
        }
    }

    public void startTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    public void resetTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startTimer();
    }

    
    public void updateStamina() {
        if (running) {
            if (stamina > 0) {
                stamina--;
            }
            else if (stamina == 0){
                running = false;
            }
        } else {
            running = false;
            if (stamina < 500) {
                stamina++;
            }
        }
    }
}