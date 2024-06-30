/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pacmanfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

/**
 *
 * @author ongzh
 */
public class Enemy {
    private Model model;
    private Maze maze;
    private Player player;
    
    private int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    private int[] dx, dy;

    // Constructor to receive Model instance
    public Enemy(Model model, Player player) {
        this.model = model;
        this.maze = new Maze(model);
        this.player = player;
    }
    
    public void setEnemyX(int[] i){
        this.enemyX = i;
    }
    
    public void setEnemyX(int index, int value){
        this.enemyX[index] = value;
    }
    
    public void setEnemyY(int[] i){
        this.enemyY = i;
    }
    
    public void setEnemyY(int index, int value){
        this.enemyY[index] = value;
    }
    
    public void setEnemyDx(int[] i){
        this.enemyDx = i;
    }
    
    public void setEnemyDx(int index, int value){
        this.enemyDx[index] = value;
    }
    
    public void setEnemyDy(int[] i){
        this.enemyDy = i;
    }
    
    public void setEnemyDy(int index, int value){
        this.enemyDy[index] = value;
    }
    
    public void setEnemySpeed(int[] i){
        this.enemySpeed = i;
    }
    
    public void setEnemySpeed(int index, int value){
        this.enemySpeed[index] = value;
    }
    
    public void setDx(int[] i){
        this.dx = i;
    }
    
    public void setDy(int[] i){
        this.dy = i;
    }    
    
    public void moveEnemy(GraphicsContext g2d) {
        int pos;
        int count;
        int BLOCK_SIZE = model.getBlockSize();
        Phantom enemyDrawer = new Phantom(model, player);
            
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
            enemyDrawer.drawEnemy(g2d, enemyX[i] + 1, enemyY[i] + 1);


            boolean inGame = model.getInGame();
            boolean powerUp = player.getPowerUp();
            
            if (player.getPlayerX() > (enemyX[i] - 12) && player.getPlayerX() < (enemyX[i] + 12) && player.getPlayerY() > (enemyY[i] - 12) && player.getPlayerY() < (enemyY[i] + 12) && inGame) {
                if (!powerUp) {
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
    
    
}

class Spider extends Enemy {
    private Model model;
    private Player player;
    private Maze maze;
    
    public Spider(Model model, Player player) {
        super(model, player);
        this.model = model;
        this.player = player;
        this.maze = new Maze(model);
        
    }

    // Move the drawEnemy method here
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.heart, x, y);
    }
}

class Phantom extends Enemy {
    private Model model;
    private Player player;
    private Maze maze;
    
    public Phantom(Model model, Player player) {
        super(model, player);
        this.model = model;
        this.player = player;
        this.maze = new Maze(model);
        
    }

    // Move the drawEnemy method here
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.heart, x, y);
    }
}