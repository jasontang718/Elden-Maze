package pacmanfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.util.Duration;

import javafx.scene.canvas.GraphicsContext;

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


class Knight implements Character{
    private Model model;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;
    
    private int lives = 5;
    private int score = 0;
    private int stamina = 300;
    private boolean running = false;
    public int playerX, playerY, playerDx, playerDy;
    private int playerSpeed = 1;
    private Timeline powerupTimer, slowedTimer;
    private boolean powerUp = false;
    private boolean slowed = false;

    // Constructor to receive Model instance
    public Knight(Model model) {
        this.model = model;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }
    
    public int getScore(){
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getLives(){
        return lives;
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
        int level = model.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = model.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the dot
                score++;
                model.playSound("gold.mp3");        

            }

            if ((ch & 32) != 0) {
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                model.playSound("powerup.mp3");
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && model.getActive()) {
                model.setDying(true);
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
    
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(model.left, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.right, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.up, playerX, playerY);
            } else {
                g2d.drawImage(model.down, playerX, playerY);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else {
                g2d.drawImage(model.enhanced, playerX, playerY);
            }        
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) model.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) model.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(model.blinded, drawX + 15, drawY + 20);
        }
    }

    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startPowerUpTimer();
        } else {
            resetPowerUpTimer();
        }
    }
    
    public void startPowerUpTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    public void resetPowerUpTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startPowerUpTimer();
    }

    public void startSlowedTimer() {
        slowedTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                slowed = false;
            }
        }));
        slowedTimer.play();   
    }
        
    public void resetSlowedTimer() {
        if (slowedTimer != null) {
            slowedTimer.stop();
        }
        startSlowedTimer();
    }
    
    public void checkSlowed(GraphicsContext g2d){
        if (!slowed) {
            slowed = true;
            startSlowedTimer();
        } else {
            resetSlowedTimer();
        }    
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

class Assassin implements Character{
    private Model model;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;
    
    private int lives = 3;
    private int score = 0;
    private int stamina = 500;
    private boolean running = false;
    public int playerX, playerY, playerDx, playerDy;
    private int playerSpeed = 1;
    private Timeline powerupTimer, slowedTimer;
    private boolean powerUp = false;
    private boolean slowed = false;

    // Constructor to receive Model instance
    public Assassin(Model model) {
        this.model = model;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }
    
    public int getScore(){
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getLives(){
        return lives;
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
        int level = model.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = model.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the dot
                score++;
                model.playSound("gold.mp3");        

            }

            if ((ch & 32) != 0) {
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                model.playSound("powerup.mp3");
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && model.getActive()) {
                model.setDying(true);
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
    
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(model.assassinImage, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.assassinImage, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.assassinImage, playerX, playerY);
            } else {
                g2d.drawImage(model.assassinImage, playerX, playerY);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else {
                g2d.drawImage(model.enhanced, playerX, playerY);
            }        
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) model.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) model.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(model.blinded, drawX + 15, drawY + 20);
        }
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
    
    public void resetPowerUpTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startPowerUpTimer();
    }

    public void startSlowedTimer() {
        slowedTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                slowed = false;
            }
        }));
        slowedTimer.play();   
    }
        
    public void resetSlowedTimer() {
        if (slowedTimer != null) {
            slowedTimer.stop();
        }
        startSlowedTimer();
    }
    
    public void checkSlowed(GraphicsContext g2d){
        if (!slowed) {
            slowed = true;
            startSlowedTimer();
        } else {
            resetSlowedTimer();
        }    
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

class Mage implements Character{
    private Model model;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;
    
    private int lives = 3;
    private int score = 0;
    private int stamina = 300;
    private boolean running = false;
    public int playerX, playerY, playerDx, playerDy;
    private int playerSpeed = 1;
    private Timeline powerupTimer, slowedTimer;
    private boolean powerUp = false;
    private boolean slowed = false;

    // Constructor to receive Model instance
    public Mage(Model model) {
        this.model = model;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);
        
        mazes = new Maze[]{maze1, maze2, maze3};
    }
    
    public int getScore(){
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getLives(){
        return lives;
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
        int level = model.getCurrentLevel();
        
        if (playerX % BLOCK_SIZE == 0 && playerY % BLOCK_SIZE == 0) {
            pos = playerX / BLOCK_SIZE + mazes[level].getHBlocks() * (playerY / BLOCK_SIZE);
            ch = model.getScreenData()[pos];

            if ((ch & 16) != 0) {
                // Pac-Man eats a dot
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the dot
                score++;
                model.playSound("gold.mp3");        

            }

            if ((ch & 32) != 0) {
                model.getScreenData()[pos] = (short) (ch & 15); // Remove the powerup orb
                score += 50;
                model.playSound("powerup.mp3");
                checkPowerUp();
            }
            
            if ((ch & 64) != 0 && model.getActive()) {
                model.setDying(true);
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
    
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        
        if (!powerUp) {
            if (reqDx <= -1) {
                g2d.drawImage(model.spiderImage, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.spiderImage, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.spiderImage, playerX, playerY);
            } else {
                g2d.drawImage(model.spiderImage, playerX, playerY);
            }
        }
        else {
            if (reqDx <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDx >= 1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else if (reqDy <= -1) {
                g2d.drawImage(model.enhanced, playerX, playerY);
            } else {
                g2d.drawImage(model.enhanced, playerX, playerY);
            }        
        }
        if (slowed && (reqDx <= -1 || reqDx >= 1 || reqDy <= -1 || reqDy >= 1)) {
            int imageWidth = (int) model.blinded.getWidth();  // Assuming getWidth() gives the width of the image
            int imageHeight = (int) model.blinded.getHeight(); // Assuming getHeight() gives the height of the image

            int drawX = playerX - (imageWidth / 2);  // Center horizontally
            int drawY = playerY - (imageHeight / 2); // Center vertically

            g2d.drawImage(model.blinded, drawX + 15, drawY + 20);
        }
    }

    public void checkPowerUp() {
        if (!powerUp) {
            powerUp = true;
            startPowerUpTimer();
        } else {
            resetPowerUpTimer();
        }
    }
    
    public void startPowerUpTimer() {
        powerupTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                powerUp = false;
            }
        }));
        powerupTimer.play();   
    }
    
    public void resetPowerUpTimer() {
        if (powerupTimer != null) {
            powerupTimer.stop();
        }
        startPowerUpTimer();
    }

    public void startSlowedTimer() {
        slowedTimer = new Timeline(new KeyFrame(Duration.millis(10000), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event){
                slowed = false;
            }
        }));
        slowedTimer.play();   
    }
        
    public void resetSlowedTimer() {
        if (slowedTimer != null) {
            slowedTimer.stop();
        }
        startSlowedTimer();
    }
    
    public void checkSlowed(GraphicsContext g2d){
        if (!slowed) {
            slowed = true;
            startSlowedTimer();
        } else {
            resetSlowedTimer();
        }    
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
