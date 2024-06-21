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
        
    public int pacmanX, pacmanY, pacmanDx, pacmanDy;
    public int PACMAN_SPEED = 1;
    private Timeline powerupTimer;
    private boolean powerup = false;
    public int[] ghostX, ghostY, ghostDx, ghostDy, ghostSpeed;
    public int[] dx, dy;

    // Constructor to receive Model instance
    public Player(Model model) {
        this.model = model;
        this.maze = new Maze(model);
    }
    

    public int getPacmanX(){
        return pacmanX;
    }
    
    public int getPacmanY(){
        return pacmanY;
    }

    public void setPacmanX(int x) {
        this.pacmanX = x;
    }
    
    public void setPacmanY(int y) {
        this.pacmanY = y;
    }
    
    public void setPacmanDx(int dx) {
        this.pacmanDx = dx;
    }

    public void setPacmanDy(int dy) {
        this.pacmanDy = dy;
    }
    
    public boolean getPowerUp() {
        return powerup;
    }
    
    public void setPowerUp(boolean value){
        this.powerup = value;
    }
    
   public void movePacman() {
        int pos;
        short ch;
        int BLOCK_SIZE = model.getBlockSize();
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();

        
        if (pacmanX % BLOCK_SIZE == 0 && pacmanY % BLOCK_SIZE == 0) {
            pos = pacmanX / BLOCK_SIZE + maze.getNBlocks() * (pacmanY / BLOCK_SIZE);
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
        
        System.out.println("Before movement - PacmanX: " + pacmanX + ", PacmanY: " + pacmanY);
        System.out.println("Dx: " + pacmanDx + ", Dy: " + pacmanDy);

        // Movement logic
        pacmanX += PACMAN_SPEED * pacmanDx;
        pacmanY += PACMAN_SPEED * pacmanDy;

        // Debugging output after movement
        System.out.println("After movement - PacmanX: " + pacmanX + ", PacmanY: " + pacmanY);
    }


    public void checkPowerUp() {
        if (!powerup) {
            powerup = true;
            startTimer();
        } else {
            resetTimer();
        }
    }
    
    public void drawPacman(GraphicsContext g2d) {
        int reqDx = model.getReqDx();
        int reqDy = model.getReqDy();
        if (!powerup) {
            if (reqDx == -1) {
                g2d.drawImage(model.left, pacmanX + 1, pacmanY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(model.right, pacmanX + 1, pacmanY + 1);
            } else if (reqDy == -1) {
                g2d.drawImage(model.up, pacmanX + 1, pacmanY + 1);
            } else {
                g2d.drawImage(model.down, pacmanX + 1, pacmanY + 1);
            }
        }
        else {
            if (reqDx == -1) {
                g2d.drawImage(model.enhanced, pacmanX + 1, pacmanY + 1);
            } else if (reqDx == 1) {
                g2d.drawImage(model.enhanced, pacmanX + 1, pacmanY + 1);
            } else if (reqDy == -1) {
                g2d.drawImage(model.enhanced, pacmanX + 1, pacmanY + 1);
            } else {
                g2d.drawImage(model.enhanced, pacmanX + 1, pacmanY + 1);
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
    
    public void moveGhosts(GraphicsContext g2d) {
        int pos;
        int count;
        int BLOCK_SIZE = model.getBlockSize();
            
        for (int i = 0; i < model.getNGhosts(); i++) {
            if (ghostX[i] % BLOCK_SIZE == 0 && ghostY[i] % BLOCK_SIZE == 0) {
                pos = ghostX[i] / BLOCK_SIZE + maze.getNBlocks() * (ghostY[i] / BLOCK_SIZE);

                short ch = model.getScreenData()[pos];
                count = 0;

                if ((ch & 1) == 0 && ghostDx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((ch & 2) == 0 && ghostDy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((ch & 4) == 0 && ghostDx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((ch & 8) == 0 && ghostDy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {
                    if ((ch & 15) == 15) {
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
            model.drawGhost(g2d, ghostX[i] + 1, ghostY[i] + 1);


            boolean inGame = model.getInGame();

            
            if (pacmanX > (ghostX[i] - 12) && pacmanX < (ghostX[i] + 12) && pacmanY > (ghostY[i] - 12) && pacmanY < (ghostY[i] + 12) && inGame) {
                if (!powerup) {
                    model.setDying(true);
                }
                else {
                    removeGhost(i);
                }
            }
        }
    }
    
    private void removeGhost(int index) {
        int nGhosts = model.getNGhosts();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < model.getNGhosts() - 1; i++) {
            ghostX[i] = ghostX[i + 1];
            ghostY[i] = ghostY[i + 1];
            ghostDx[i] = ghostDx[i + 1];
            ghostDy[i] = ghostDy[i + 1];
            ghostSpeed[i] = ghostSpeed[i + 1];
        }
        model.playSound("kill.mp3");
        nGhosts--; // Decrease the count of ghosts
        model.setNGhosts(nGhosts);
    }
}