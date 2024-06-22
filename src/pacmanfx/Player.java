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
        
    public int playerX, playerY, playerDx, playerDy;
    public int playerSpeed = 1;
    private Timeline powerupTimer;
    private boolean powerup = false;
    public int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    public int[] dx, dy;

    // Constructor to receive Model instance
    public Player(Model model) {
        this.model = model;
        this.maze = new Maze(model);
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
        return powerup;
    }
    
    public void setPowerUp(boolean value){
        this.powerup = value;
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
                if (!((reqDx == -1 && reqDy == 0 && (ch & 1) != 0)
                        || (reqDx == 1 && reqDy == 0 && (ch & 4) != 0)
                        || (reqDx == 0 && reqDy == -1 && (ch & 2) != 0)
                        || (reqDx == 0 && reqDy == 1 && (ch & 8) != 0))) {
                    playerDx = reqDx;
                    playerDy = reqDy;
                }
            }

            // Check for collisions with walls
            if ((playerDx == -1 && playerDy == 0 && (ch & 1) != 0)
                    || (playerDx == 1 && playerDy == 0 && (ch & 4) != 0)
                    || (playerDx == 0 && playerDy == -1 && (ch & 2) != 0)
                    || (playerDx == 0 && playerDy == 1 && (ch & 8) != 0)) {
                playerDx = 0;
                playerDy = 0;
            }
        }
        playerX += playerSpeed * playerDx;
        playerY += playerSpeed * playerDy;
        
        System.out.println("Before movement - PacmanX: " + playerX + ", PacmanY: " + playerY);
        System.out.println("Dx: " + playerDx + ", Dy: " + playerDy);

        // Movement logic
        playerX += playerSpeed * playerDx;
        playerY += playerSpeed * playerDy;

        // Debugging output after movement
        System.out.println("After movement - PacmanX: " + playerX + ", PacmanY: " + playerY);
    }


    public void checkPowerUp() {
        if (!powerup) {
            powerup = true;
            startTimer();
        } else {
            resetTimer();
        }
    }
    
    public void drawPlayer(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        if (!powerup) {
            if (reqDx == -1) {
                g2d.drawImage(model.left, playerX + 1, playerY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(model.right, playerX + 1, playerY + 1);
            } else if (reqDy == -1) {
                g2d.drawImage(model.up, playerX + 1, playerY + 1);
            } else {
                g2d.drawImage(model.down, playerX + 1, playerY + 1);
            }
        }
        else {
            if (reqDx == -1) {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(model.enhanced, playerX + 1, playerY + 1);
            } else if (reqDy == -1) {
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
                powerup = false;
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
    
    public void moveEnemy(GraphicsContext g2d) {
        int pos;
        int count;
        int BLOCK_SIZE = model.getBlockSize();
            
        for (int i = 0; i < maze.getEnemyCount(); i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + maze.getNBlocks() * (enemyY[i] / BLOCK_SIZE);

                short ch = model.getScreenData()[pos];
                count = 0;

                if ((ch & 1) == 0 && enemyDx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((ch & 2) == 0 && enemyDy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((ch & 4) == 0 && enemyDx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((ch & 8) == 0 && enemyDy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((ch & 15) == 15) {
                        enemyDx[i] = 0;
                        enemyDy[i] = 0;
                    } else {
                        enemyDx[i] = -enemyDx[i];
                        enemyDy[i] = -enemyDy[i];
                    }
                } else {
                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    enemyDx[i] = dx[count];
                    enemyDy[i] = dy[count];
                }
            }

            enemyX[i] += enemyDx[i] * enemySpeed[i];
            enemyY[i] += enemyDy[i] * enemySpeed[i];
            drawEnemy(g2d, enemyX[i] + 1, enemyY[i] + 1);


            boolean inGame = model.getInGame();

            
            if (playerX > (enemyX[i] - 12) && playerX < (enemyX[i] + 12) && playerY > (enemyY[i] - 12) && playerY < (enemyY[i] + 12) && inGame) {
                if (!powerup) {
                    model.setDying(true);
                }
                else {
                    removeEnemy(i);
                }
            }
        }
    }
    
    private void removeEnemy(int index) {
        int enemyCount = maze.getEnemyCount();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < maze.getEnemyCount() - 1; i++) {
            enemyX[i] = enemyX[i + 1];
            enemyY[i] = enemyY[i + 1];
            enemyDx[i] = enemyDx[i + 1];
            enemyDy[i] = enemyDy[i + 1];
            enemySpeed[i] = enemySpeed[i + 1];
        }
        model.playSound("kill.mp3");
        enemyCount--; // Decrease the count of ghosts
        maze.setEnemyCount(enemyCount);
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.spider, x, y);
    }
}