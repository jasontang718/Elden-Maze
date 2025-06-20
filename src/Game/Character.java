package Game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

import javafx.scene.canvas.GraphicsContext;

//Interface for the Character class to set up the common methods
interface Character {
    int getScore();
    void setScore(int score);

    int getLives();
    
    boolean getDebuff();
    void setDebuff(boolean value);
    
    boolean getRunning();
    void setRunning(boolean value);
    
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
    
    void movePlayer();
    void drawPlayer(GraphicsContext g2d);
    void updateStamina();
    void checkDebuffed(GraphicsContext g2d);
}

//A parent class which implements the interface from the Character class, it contains the general variables and methods used by all subclasses
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
    protected Timeline powerupTimer, debuffedTimer;
    protected boolean powerUp = false;
    protected boolean debuff = false;
    
    public int getScore(){
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
   
    public int getStamina(){
        return stamina;
    }
    
    public boolean getDebuff(){
        return debuff;
    }
    
    public void setDebuff(boolean debuff){
        this.debuff = debuff;
    }
    
    public void setRunning(boolean debuff){
        this.running = debuff;
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
    
    public void setPowerUp(boolean powerUp){
        this.powerUp = powerUp;
    }
    
    public void setPlayerSpeed(int speed){
        this.playerSpeed = speed;
    }
    
    public int getPlayerSpeed(){
        return playerSpeed;
    }
    
    //Checks whether a powerup is active and starts or resets the powerup timer
    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startPowerUpTimer();
        } else {
            resetPowerUpTimer();
        }
    }
    
    //Starts the powerup timer
    public void startPowerUpTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    //Restarts the powerup timer
    public void resetPowerUpTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startPowerUpTimer();
    }

    //Checks whether a debuff is active and starts or resets the debuff timer
    public void checkDebuffed(GraphicsContext g2d){
        if (!debuff) {
            debuff = true;
            startDebuffedTimer();
        } else {
            resetDebuffedTimer();
        }    
    }
    
    //Starts the slowed timer for when a player receives a debuff
    public void startDebuffedTimer() {
        debuffedTimer = new Timeline(new KeyFrame(Duration.seconds(10), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                debuff = false;
            }
        }));
        debuffedTimer.play();   
    }
        
    //Restarts the slowed timer if a player gets debuffed again during a debuff
    public void resetDebuffedTimer() {
        if (debuffedTimer != null) {
            debuffedTimer.stop();
        }
        startDebuffedTimer();
    }
    
    //Handles the stamina of the player by checking if the player is running or not running
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
            
            //Amount of stamina for Knight and Mage
            if (characterNo == 0 || characterNo == 2){
                if (stamina < 300) {
                    stamina++;
                }
            }
            
            //Amount of stamina for Assassin
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
    
    //Constructor for Knight subclass
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

    //Code for moving the player, and handles interactions with coins and powerups, and death to traps
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
            
            //Removes the coin
            if ((ch & 16) != 0) {
                controller.getScreenData()[pos] = (short) (ch & ~16); 
                score++;
                controller.playSound("gold.mp3",false);        
            }
            
            //Removes the powerup orb
            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15); 
                score += 50;
                controller.playSound("powerup.mp3",false);
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
                powerUp = false;
            }
            
            //Checks if the requested direction is valid
            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx <= -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx >= 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy <= -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy >= 1 && (ch & 8) != 0))) {
                    playerDx = reqDx;
                    playerDy = reqDy;
                }
            }
            
            //Checks for collisions with walls
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

    //Draws the character when they are moving in different directions in both normal state and powerup state, and draws the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        //Normal state
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
        
        //PowerUp state
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
        
        //Debuffed state
        if (debuff && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();
            int imageHeight = (int) controller.blinded.getHeight();

            int drawX = playerX - (imageWidth / 2);
            int drawY = playerY - (imageHeight / 2);

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }
}

class Assassin extends GeneralCharacter{
    
    int lives = 3;
    int stamina = 500;

    //Constructor for Assassin subclass
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

    //Code for moving the player, and handles interactions with coins and powerups, and death to traps
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

            //Removes the coin
            if ((ch & 16) != 0) {
                controller.getScreenData()[pos] = (short) (ch & ~16);
                score++;
                controller.playSound("gold.mp3",false);        
            }

            //Removes the powerup orb
            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15);
                score += 50;
                controller.playSound("assassinvanish.mp3",false);
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
                debuff = false;
            }

            //Check if the requested direction is valid
            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx <= -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx >= 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy <= -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy >= 1 && (ch & 8) != 0))) {
                    playerDx = reqDx;
                    playerDy = reqDy;
                }
            }

            //Check for collisions with walls
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
    
    //Assasssin has its own implementation of checkPowerUp method as it changes the speed instead of allowing it to kill enemies
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
    
    //Draws the character when they are moving in different directions in both normal state and powerup state, and draws the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        //Normal state
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
        
        //Powerup state
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
        
        //Debuffed state
        if (debuff && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();
            int imageHeight = (int) controller.blinded.getHeight();

            int drawX = playerX - (imageWidth / 2);
            int drawY = playerY - (imageHeight / 2);

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }
}

class Mage extends GeneralCharacter{
    private Timeline powerupAnimationTimer;
    private boolean powerUpAnimation = false;

    int lives = 3;
    int stamina = 300;
    
    //Constructor for Mage subclass
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
 
    //Code for moving the player, and handles interactions with coins and powerups, and death to traps
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

            //Removes the coin
            if ((ch & 16) != 0) {
                controller.getScreenData()[pos] = (short) (ch & ~16);
                score++;
                controller.playSound("gold.mp3",false);        
            }

            //Removes the powerup orb
            if ((ch & 32) != 0) {
                controller.getScreenData()[pos] = (short) (ch & 15);
                score += 50;
                controller.playSound("freeze.mp3",false);
                debuff = false;
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && controller.getActive()) {
                controller.setDying(true);
            }

            //Check if the requested direction is valid
            if (reqDx != 0 || reqDy != 0) {
                if (!((reqDx <= -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx >= 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy <= -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy >= 1 && (ch & 8) != 0))) {
                    playerDx = reqDx;
                    playerDy = reqDy;
                }
            }

            //Check for collisions with walls
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
    
    //Draws the character when they are moving in different directions in both normal state and powerup state, and draws the debuff
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = controller.getReqDx();
        int reqDy = controller.getReqDy();
        
        //Normal state
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
        
        //Powerup state
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
        
        //Debuffed state
        if (debuff && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) controller.blinded.getWidth();
            int imageHeight = (int) controller.blinded.getHeight();

            int drawX = playerX - (imageWidth / 2);
            int drawY = playerY - (imageHeight / 2);

            g2d.drawImage(controller.blinded, drawX + 15, drawY + 20);
        }
    }

    //Mage has its unique checkPowerUp method as the tranformation only happens one time, therefore it needs a separate timer for the animation
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
    
    //Timer for the animation
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