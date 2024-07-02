package pacmanfx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public interface Maze {
    short[] getLevelData();
    int getEnemyCount();
    void setEnemyCount(int enemyCount);
    int getHBlocks();
    int getVBlocks();
    void drawMaze(GraphicsContext g2d);
}

class Maze1 implements Maze{
    private Model model;
    private final int H_BLOCKS = 25;
    private final int V_BLOCKS = 24;
    private static final int BLOCK_SIZE = 40;
    private int enemyCount = 2;
    
    private final short[] levelData = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,18,26,18,26,26,18,26,26,18,26,18,26,22,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,0,21,0,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,25,26,26,16,26,26,28,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,17,26,20,0,19,26,18,24,18,26,22,0,17,26,20,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,20,0,21,0,21,0,17,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,25,26,24,18,24,26,28,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,25,26,24,26,26,26,26,24,26,26,26,26,24,26,44,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };
    
    public Maze1(Model model) {
        this.model = model;
    }
    
    public short[] getLevelData(){
        return levelData;
    }
    
    public int getEnemyCount(){
        return enemyCount;
    }
    
    public void setEnemyCount(int enemyCount){
        this.enemyCount = enemyCount;
    }
    
    public int getHBlocks() {
        return H_BLOCKS;
    }
    
    public int getVBlocks() {
        return V_BLOCKS;
    }
     
    public void drawMaze(GraphicsContext g2d) {
        short i = 0;
        int x, y;

        for (y = 0; y < model.getScreenVSize(); y += BLOCK_SIZE) {
            for (x = 0; x < model.getScreenHSize(); x += BLOCK_SIZE) {

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

}

class Maze2 implements Maze{
    private Model model;
    private final int H_BLOCKS = 25;
    private final int V_BLOCKS = 24;
    private static final int BLOCK_SIZE = 40;
    private int enemyCount = 4;
    
    private final short[] levelData = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,26,26,26,18,26,26,18,26,26,18,26,26,18,26,26,18,26,26,26,26,22,0,
        0,21,0,0,0,0,21,0,0,21,0,0,21,0,0,21,0,0,21,0,0,0,0,21,0,
        0,21,0,0,0,0,21,0,19,24,22,0,21,0,19,24,22,0,21,0,0,0,0,21,0,
        0,17,26,26,26,26,20,0,21,0,21,0,21,0,21,0,21,0,17,26,26,26,26,20,0,
        0,21,0,0,0,0,21,0,21,0,17,26,16,26,20,0,21,0,21,0,0,0,0,21,0,
        0,17,26,26,18,26,24,26,20,0,21,0,21,0,21,0,17,26,24,26,18,26,26,20,0,
        0,21,0,0,21,0,0,0,21,0,21,0,21,0,21,0,21,0,0,0,21,0,0,21,0,
        0,21,0,19,24,26,18,26,28,0,17,26,24,26,20,0,25,26,18,26,24,22,0,21,0,
        0,21,0,21,0,0,21,0,0,0,21,0,0,0,21,0,0,0,21,0,0,21,0,21,0,
        0,21,0,21,0,0,21,0,19,26,24,26,18,26,24,26,22,0,21,0,0,21,0,21,0,
        0,17,26,24,22,0,25,26,20,0,0,0,21,0,0,0,17,26,28,0,19,24,26,20,0,
        0,21,0,0,21,0,0,0,21,0,0,0,21,0,0,0,21,0,0,0,21,0,0,21,0,
        0,21,0,0,17,26,26,26,24,26,18,26,16,26,18,26,24,26,26,26,20,0,0,21,0,
        0,17,26,26,20,0,0,0,0,0,21,0,21,0,21,0,0,0,0,0,17,26,26,20,0,
        0,21,0,0,21,0,0,19,26,26,28,0,21,0,25,26,26,22,0,0,21,0,0,21,0,
        0,17,26,18,20,0,0,21,0,0,0,0,21,0,0,0,0,21,0,0,17,18,26,20,0,
        0,21,0,17,24,26,26,24,18,26,18,26,24,26,18,26,18,24,26,26,24,20,0,21,0,
        0,21,0,21,0,0,0,0,21,0,21,0,0,0,21,0,21,0,0,0,0,21,0,21,0,
        0,17,26,24,26,26,22,0,21,0,17,26,26,26,20,0,21,0,19,26,26,24,26,20,0,
        0,21,0,0,0,0,21,0,25,26,20,0,0,0,17,26,28,0,21,0,0,0,0,21,0,
        0,21,0,0,0,0,21,0,0,0,21,0,0,0,21,0,0,0,21,0,0,0,0,21,0,
        0,25,26,26,26,26,24,26,26,26,24,26,26,26,24,26,26,26,24,26,26,26,26,44,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
    };
    
    public Maze2(Model model) {
        this.model = model;
    }
    
    public short[] getLevelData(){
        return levelData;
    }
    
    public int getEnemyCount(){
        return enemyCount;
    }
    
    public void setEnemyCount(int enemyCount){
        this.enemyCount = enemyCount;
    }
    
    public int getHBlocks() {
        return H_BLOCKS;
    }
    
    public int getVBlocks() {
        return V_BLOCKS;
    }
     
    public void drawMaze(GraphicsContext g2d) {
        short i = 0;
        int x, y;

        for (y = 0; y < model.getScreenVSize(); y += BLOCK_SIZE) {
            for (x = 0; x < model.getScreenHSize(); x += BLOCK_SIZE) {

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

}

class Maze3 implements Maze{
    private Model model;
    private final int H_BLOCKS = 25;
    private final int V_BLOCKS = 24;
    private static final int BLOCK_SIZE = 40;
    private int enemyCount = 6;
    
    private final short[] levelData = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,18,26,18,26,26,18,26,26,18,26,18,26,22,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,0,21,0,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,25,26,26,16,26,26,28,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,17,26,20,0,19,26,18,24,18,26,22,0,17,26,20,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,20,0,21,0,21,0,17,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,25,26,24,18,24,26,28,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,17,26,26,26,26,16,26,26,26,26,20,0,21,0,0,0,0,0,0,0,0,0,
        0,21,0,21,0,0,0,0,21,0,0,0,0,21,0,21,0,0,0,0,0,0,0,0,0,
        0,25,26,24,26,26,26,26,24,26,26,26,26,24,26,44,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };

    
    public Maze3(Model model) {
        this.model = model;
    }
    
    public short[] getLevelData(){
        return levelData;
    }
    
    public int getEnemyCount(){
        return enemyCount;
    }
    
    public void setEnemyCount(int enemyCount){
        this.enemyCount = enemyCount;
    }
    
    public int getHBlocks() {
        return H_BLOCKS;
    }
    
    public int getVBlocks() {
        return V_BLOCKS;
    }
     
    public void drawMaze(GraphicsContext g2d) {
        short i = 0;
        int x, y;

        for (y = 0; y < model.getScreenVSize(); y += BLOCK_SIZE) {
            for (x = 0; x < model.getScreenHSize(); x += BLOCK_SIZE) {

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

}