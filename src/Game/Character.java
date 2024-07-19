package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import javafx.scene.canvas.GraphicsContext;

//interface for the character class to set up the common methods
public interface Character {
    int getScore();
    void setScore(int score);
    void movePlayer();
    void drawPlayer(GraphicsContext g2d);
    int getLives();
    boolean getSlowed();
    void setSlowed(boolean value);
    void setRunning(boolean value);
    boolean getRunning();
    int getStamina();
    int getPlayerX();
    int getPlayerY();
    void setPlayerX(int x);
    void setPlayerY(int y);
    void setPlayerDx(int dx);
    void setPlayerDy(int dy);
    boolean getPowerUp();
    void setPowerUp(boolean value);
    void setPlayerSpeed(int speed);
    int getPlayerSpeed();
    void updateStamina();
    void checkSlowed(GraphicsContext g2d);
}

//a parent class which implements the interface from the character class, it contains the general variables and methods used by all subclasses
abstract class GeneralCharacter implements Character {
    protected Controller controller;
    protected Maze1 maze1;
    protected Maze2 maze2;
    protected Maze3 maze3;
    protected Maze[] mazes;
    
    protected int score = 0;
    protected int lives;
    protected int stamina;
    protected boolean running = false;
    protected int playerX, playerY, playerDx, playerDy;
    protected int playerSpeed = 1;
    protected Timeline powerupTimer, slowedTimer;
    protected boolean powerUp = false;
    protected boolean slowed = false;
    
    public int getScore(){
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
   
    public int getStamina(){
        return stamina;
    }
    
    public boolean getSlowed(){
        return slowed;
    }
    
    public void setSlowed(boolean value){
        this.slowed = value;
    }
    
    public void setRunning(boolean value){
        this.running = value;
    }
    public boolean getRunning(){
        return running;
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
    
    //checks whether a powerup is active and starts or resets the powerup timer
    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startPowerUpTimer();
        } else {
            resetPowerUpTimer();
        }
    }
    
    //starts the powerup timer
    public void startPowerUpTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    //restarts the powerup timer
    public void resetPowerUpTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startPowerUpTimer();
    }

    //checks whether a debuff is active and starts or resets the slowed timer
    public void checkSlowed(GraphicsContext g2d){
        if (!slowed) {
            slowed = true;
            startSlowedTimer();
        } else {
            resetSlowedTimer();
        }    
    }
    
    //starts the slowed timer for when a player receives a debuff
    public void startSlowedTimer() {
        slowedTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                slowed = false;
            }
        }));
        slowedTimer.play();   
    }
        
    //restarts the slowed timer if a player gets debuffed again during a debuff
    public void resetSlowedTimer() {
        if (slowedTimer != null) {
            slowedTimer.stop();
        }
        startSlowedTimer();
    }
    
    //handles the stamina of the player by checking if the player is running or not running
    public void updateStamina() {
        if (running) {
            if (stamina > 0) {
                stamina--;
            }
            else if (stamina == 0){
                running = false;
            }
        } 
        else {
            running = false;
            int characterNo = controller.getCharacterNo();
            
            if (characterNo == 0 || characterNo == 2){
                if (stamina < 300) {
                    stamina++;
                }
            }
            else if (characterNo == 1){
                if (stamina < 500) {
                    stamina++;
                }
            }
        }
    }
}

class Knight extends GeneralCharacter{

    int lives = 5;
    int stamina = 300;
    
    public Knight(Controller controller) {
        this.controller = controller;
        this.maze1 = new Maze1(controller);
        this.maze2 = new Maze2(controller);
        this.maze3 = new Maze3(controller);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }

    public int getLives(){
        return lives;
    }

    //code for moving the player, and handles the collectibles such as coins and powerups and death to traps
    public void movePlayer() {
        int pos;
        short ch;
        int BLOCK_SIZE = controller.getBlockSize();
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        int level = controller.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = controller.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                controller.getScreenData()[pos] = (short) (ch & ~16); // Remove the dot
                score++;
                controller.playSound("gold.mp3",false);        

            }

            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                controller.playSound("powerup.mp3",false);
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
                powerUp = false;
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

    }

    //draws the characters when they are moving in the 4 directions in both normal state and powerup state, as well as drawing the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(controller.knightLeft, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(controller.knightRight, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(controller.knightUp, playerX, playerY);
            } else {
                g2d.drawImage(controller.knightDown, playerX, playerY);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(controller.powerKnightLeft, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(controller.powerKnightRight, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(controller.powerKnightUp, playerX, playerY);
            } else {
                g2d.drawImage(controller.powerKnightDown, playerX, playerY);
            }        
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) controller.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }
}

class Assassin extends GeneralCharacter{
    
    int lives = 3;
    int stamina = 500;

    // Constructor to receive Controller instance
    public Assassin(Controller controller) {
        this.controller = controller;
        this.maze1 = new Maze1(controller);
        this.maze2 = new Maze2(controller);
        this.maze3 = new Maze3(controller);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }
  
    public int getLives(){
        return lives;
    }

    //code for moving the player, and handles the collectibles such as coins and powerups and death to traps
    public void movePlayer() {
        int pos;
        short ch;
        int BLOCK_SIZE = controller.getBlockSize();
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        int level = controller.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = controller.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                controller.getScreenData()[pos] = (short) (ch & ~16); // Remove the dot
                score++;
                controller.playSound("gold.mp3",false);        

            }

            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                controller.playSound("powerup.mp3",false);
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
                slowed = false;
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

    }
    
    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            playerSpeed = 2;
            startPowerUpTimer();
        } else {
            resetPowerUpTimer();
        }
    }
    
    public void startPowerUpTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                playerSpeed = 1;
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    //draws the characters when they are moving in the 4 directions in both normal state and powerup state, as well as drawing the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(controller.assassinLeft, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(controller.assassinRight, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(controller.assassinUp, playerX, playerY);
            } else {
                g2d.drawImage(controller.assassinDown, playerX, playerY);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(controller.powerAssassinLeft, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(controller.powerAssassinRight, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(controller.powerAssassinUp, playerX, playerY);
            } else {
                g2d.drawImage(controller.powerAssassinDown, playerX, playerY);
            }        
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) controller.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }
}

class Mage extends GeneralCharacter{
    private Timeline powerupAnimationTimer;
    private boolean powerUpAnimation = false;

    int lives = 3;
    int stamina = 300;
    
    public Mage(Controller controller) {
        this.controller = controller;
        this.maze1 = new Maze1(controller);
        this.maze2 = new Maze2(controller);
        this.maze3 = new Maze3(controller);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }
    
    public int getLives(){
        return lives;
    }
 
    //code for moving the player, and handles the collectibles such as coins and powerups and death to traps
    public void movePlayer() {
        int pos;
        short ch;
        int BLOCK_SIZE = controller.getBlockSize();
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        int level = controller.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = controller.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                controller.getScreenData()[pos] = (short) (ch & ~16); // Remove the dot
                score++;
                controller.playSound("gold.mp3",false);        

            }

            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                controller.playSound("freeze.mp3",false);
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
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

    }
    
    //draws the characters when they are moving in the 4 directions in both normal state and powerup state, as well as drawing the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(controller.mageLeft, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(controller.mageRight, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(controller.mageUp, playerX, playerY);
            } else {
                g2d.drawImage(controller.mageDown, playerX, playerY);
            }
        }
        else {
            g2d.drawImage(controller.frozen, 0, 0);
            
            if (powerUpAnimation) {
                if (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1) {
                    g2d.drawImage(controller.powerMage, playerX, playerY);
                }
            }
            else {
                if (reqDx <= -1) {
                    g2d.drawImage(controller.mageLeft, playerX, playerY);
                } else if (reqDx >= 1) {
                    g2d.drawImage(controller.mageRight, playerX, playerY);
                } else if (reqDy <= -1) {
                    g2d.drawImage(controller.mageUp, playerX, playerY);
                } else {
                    g2d.drawImage(controller.mageDown, playerX, playerY);
                }
            }
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) controller.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }

    //mage has its unique checkPowerUp method as the tranformation only happens one time, therefore it needs a separate timer for the animation
    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startPowerUpTimer();
        } 
        else {
            resetPowerUpTimer();
        }
        
        if (!powerUpAnimation) {
            powerUpAnimation = true;
            startAnimationTimer();
        }
    }
    
    public void startAnimationTimer() {
        powerupAnimationTimer = new Timeline(new KeyFrame(Duration.seconds(1.4), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUpAnimation = false;
            }
        }));
        powerupAnimationTimer.play();   
    }
}