package pacmanfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Maze {
    private Model model;
    private Player player;
    private final int N_BLOCKS = 17;
    private static final int BLOCK_SIZE = 40;
     short[] screenData;
     private final int SCREEN_SIZE = getNBlocks() * BLOCK_SIZE;
     private int score;
 private int nGhosts = 6;
    private static final int MAX_GHOSTS = 12;
     private int currentSpeed = 1;
    private static final int MAX_SPEED = 6;
    public Maze(Model model) {
        this.model = model;
       
      
    }
    
    public int getNBlocks() {
        return N_BLOCKS;
    }
    
    public short[] getscreenData(){
        return screenData;
    }
    public void setscreenData(short [] screenData){
        this.screenData = screenData;
    }
    
      final short[] levelData = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,18,26,18,26,26,18,26,26,18,26,18,26,22,0,
        0,21,0,21,0,21,0,0,21,0,0,21,0,21,0,21,0,
        0,21,0,21,0,25,26,26,16,26,26,28,0,21,0,21,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,
        0,17,26,20,0,19,26,18,24,18,26,22,0,17,26,20,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,
        0,21,0,17,26,20,0,21,0,21,0,17,26,20,0,21,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,
        0,21,0,21,0,25,26,24,18,24,26,28,0,21,0,21,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,
        0,25,26,24,26,26,26,26,24,26,26,26,26,24,26,44,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
    };
    void drawMaze(GraphicsContext g2d) {
    short i = 0;
    int x, y;

    for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
        for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

            g2d.setStroke(Color.GREY);
            g2d.setLineWidth(5);

            if (levelData[i] == 0) {
                g2d.drawImage(model.floor2, x, y);
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

            if ((model.screenData[i] & 16) != 0) {
                double coinX = x + BLOCK_SIZE/2 - (model.coin).getWidth()/2;
                double coinY = y + BLOCK_SIZE/2 - (model.coin).getHeight()/2;
                g2d.drawImage(model.coin, coinX, coinY);
            
            }
            
            if ((model.screenData[i] & 32) != 0) {
                double powerupX = x + BLOCK_SIZE/2 - (model.sword).getWidth()/2;
                double powerupY = y + BLOCK_SIZE/2 - (model.sword).getHeight()/2;
                g2d.drawImage(model.sword, powerupX, powerupY);
                
            }

            i++;
        }
    }
}
     void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < getNBlocks() * getNBlocks() && finished) {
            if ((model.screenData[i]) != 0) {
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
            model.initLevel();
        }
    }
}

