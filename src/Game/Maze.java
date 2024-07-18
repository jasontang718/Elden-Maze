package Game;


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

abstract class GeneralMaze implements Maze {
    protected Controller controller;
    protected Character player;
    protected final int H_BLOCKS = 25;
    protected final int V_BLOCKS = 24;
    protected static final int BLOCK_SIZE = 40;
    protected int enemyCount;
    protected short[] levelData;
    
    public GeneralMaze(Controller controller, int enemyCount, short[] levelData) {
        this.controller = controller;
        this.enemyCount = enemyCount;
        this.levelData = levelData;
    }

    @Override
    public short[] getLevelData() {
        return levelData;
    }

    @Override
    public int getEnemyCount() {
        return enemyCount;
    }

    @Override
    public void setEnemyCount(int enemyCount) {
        this.enemyCount = enemyCount;
    }

    @Override
    public int getHBlocks() {
        return H_BLOCKS;
    }

    @Override
    public int getVBlocks() {
        return V_BLOCKS;
    }

    @Override
    public void drawMaze(GraphicsContext g2d) {
        short i = 0;
        int x, y;
        for (y = 0; y < controller.getScreenVSize(); y += BLOCK_SIZE) {
            for (x = 0; x < controller.getScreenHSize(); x += BLOCK_SIZE) {

                g2d.setStroke(Color.GREY);
                g2d.setLineWidth(5);

                if (levelData[i] == 0) {
                    if(controller.getCurrentLevel() == 0){
                        g2d.drawImage(controller.mazeWall1, x, y);                    
                    }
                    else if(controller.getCurrentLevel() == 1){
                        g2d.drawImage(controller.mazeWall2, x, y);                    
                    }
                    else if(controller.getCurrentLevel() == 2){
                        g2d.drawImage(controller.mazeWall3, x, y);                    
                    }
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

                if ((controller.screenData[i] & 16) != 0) {
                    double coinX = x + BLOCK_SIZE / 2 - (controller.coin).getWidth() / 2;
                    double coinY = y + BLOCK_SIZE / 2 - (controller.coin).getHeight() / 2;
                    g2d.drawImage(controller.coin, coinX, coinY);
                }

                if ((controller.screenData[i] & 32) != 0) {
                    double powerupX = x + BLOCK_SIZE / 2 - (controller.powerOrb).getWidth() / 2;
                    double powerupY = y + BLOCK_SIZE / 2 - (controller.powerOrb).getHeight() / 2;
                    g2d.drawImage(controller.powerOrb, powerupX, powerupY);
                }
                
                if ((controller.screenData[i] & 64) != 0) {
                    if(controller.getCurrentLevel() == 1){
                        double trapX = x + BLOCK_SIZE / 2 - (controller.fire).getWidth() / 2;
                        double trapY = y + BLOCK_SIZE / 2 - (controller.fire).getHeight() / 2;
                        g2d.drawImage(controller.fire, trapX, trapY);                    
                    }
                    else {
                        double trapX = x + BLOCK_SIZE / 2 - (controller.spike).getWidth() / 2;
                        double trapY = y + BLOCK_SIZE / 2 - (controller.spike).getHeight() / 2;
                        g2d.drawImage(controller.spike, trapX, trapY);
                    }
                }

                i++;
            }
        }
    }
}

class Maze1 extends GeneralMaze {
    private static final short[] LEVEL_DATA = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,26,26,18,26,26,26,18,26,26,18,26,26,18,26,26,26,18,26,26,26,22,0,
        0,21,0,0,0,21,0,0,0,21,0,0,21,0,0,21,0,0,0,21,0,0,0,21,0,
        0,21,0,19,26,24,26,22,0,21,0,19,24,22,0,21,0,19,26,24,26,22,0,21,0,
        0,21,0,21,0,0,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,21,0,21,0,
        0,21,0,21,0,0,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,21,0,21,0,
        0,21,0,25,26,18,26,28,0,21,0,25,18,28,0,21,0,25,26,18,26,28,0,21,0,
        0,21,0,0,0,21,0,0,0,21,0,0,21,0,0,21,0,0,0,21,0,0,0,21,0,
        0,17,26,26,18,24,18,26,26,16,26,26,16,26,26,16,26,26,18,24,18,26,26,20,0,
        0,21,0,0,21,0,21,0,0,21,0,0,21,0,0,21,0,0,21,0,21,0,0,21,0,
        0,21,0,0,21,0,21,0,19,24,22,0,21,0,19,24,22,0,21,0,21,0,0,21,0,
        0,17,26,26,24,26,20,0,21,0,21,0,21,0,21,0,21,0,17,26,24,26,26,20,0,
        0,21,0,0,0,0,17,26,20,0,17,26,24,26,20,0,17,26,20,0,0,0,0,21,0,
        0,21,0,19,26,26,20,0,21,0,21,0,0,0,21,0,21,0,17,26,26,22,0,21,0,
        0,21,0,21,0,0,21,0,25,26,24,26,18,26,24,26,28,0,21,0,0,21,0,21,0,
        0,21,0,21,0,0,21,0,0,0,0,0,21,0,0,0,0,0,21,0,0,21,0,21,0,
        0,21,0,17,26,26,80,26,22,0,19,26,24,26,22,0,19,26,80,26,26,20,0,21,0,
        0,21,0,21,0,0,21,0,21,0,21,0,0,0,21,0,21,0,21,0,0,21,0,21,0,
        0,17,26,20,0,0,21,0,21,0,21,0,39,0,21,0,21,0,21,0,0,17,26,20,0,
        0,21,0,21,0,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,21,0,21,0,
        0,21,0,25,26,26,20,0,25,26,24,26,16,26,24,26,28,0,17,26,26,28,0,21,0,
        0,21,0,0,0,0,21,0,0,0,0,0,21,0,0,0,0,0,21,0,0,0,0,21,0,
        0,25,26,26,26,26,24,26,26,26,26,26,24,26,26,26,26,26,24,26,26,26,26,28,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };

    public Maze1(Controller model) {
        super(model, 2, LEVEL_DATA);
    }
}

class Maze2 extends GeneralMaze {
    private static final short[] LEVEL_DATA = {
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
        0,19,26,26,26,18,26,26,26,18,26,26,18,26,26,18,26,26,26,18,26,26,26,22,0,
        0,21,0,0,0,21,0,0,0,21,0,0,21,0,0,21,0,0,0,21,0,0,0,21,0,
        0,21,0,19,26,24,26,22,0,21,0,19,24,22,0,21,0,19,26,24,26,22,0,21,0,
        0,21,0,21,0,0,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,21,0,21,0,
        0,21,0,21,0,0,0,21,0,21,0,21,0,21,0,21,0,21,0,0,0,21,0,21,0,
        0,21,0,25,26,18,26,28,0,21,0,25,18,28,0,21,0,25,26,18,26,28,0,21,0,
        0,21,0,0,0,21,0,0,0,21,0,0,21,0,0,21,0,0,0,21,0,0,0,21,0,
        0,17,26,26,18,24,18,26,26,16,26,26,16,26,26,16,26,26,18,24,18,26,26,20,0,
        0,21,0,0,21,0,21,0,0,21,0,0,21,0,0,21,0,0,21,0,21,0,0,21,0,
        0,21,0,0,21,0,21,0,19,24,22,0,21,0,19,24,22,0,21,0,21,0,0,21,0,
        0,17,26,26,24,26,20,0,21,0,21,0,21,0,21,0,21,0,17,26,24,26,26,20,0,
        0,21,0,0,0,0,17,26,20,0,17,26,24,26,20,0,17,26,20,0,0,0,0,21,0,
        0,21,0,19,26,26,20,0,21,0,21,0,0,0,21,0,21,0,17,26,26,22,0,21,0,
        0,21,0,21,0,0,21,0,25,26,24,26,18,26,24,26,28,0,21,0,0,21,0,21,0,
        0,21,0,21,0,0,21,0,0,0,0,0,21,0,0,0,0,0,21,0,0,21,0,21,0,
        0,21,0,17,26,26,80,26,22,0,19,26,24,26,22,0,19,26,80,26,26,20,0,21,0,
        0,21,0,21,0,0,21,0,21,0,21,0,0,0,21,0,21,0,21,0,0,21,0,21,0,
        0,17,26,20,0,0,21,0,21,0,21,0,39,0,21,0,21,0,21,0,0,17,26,20,0,
        0,21,0,21,0,0,21,0,21,0,21,0,21,0,21,0,21,0,21,0,0,21,0,21,0,
        0,21,0,25,26,26,20,0,25,26,24,26,16,26,24,26,28,0,17,26,26,28,0,21,0,
        0,21,0,0,0,0,21,0,0,0,0,0,21,0,0,0,0,0,21,0,0,0,0,21,0,
        0,25,26,26,26,26,24,26,26,26,26,26,24,26,26,26,26,26,24,26,26,26,26,28,0,
        0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
    };

    public Maze2(Controller model) {
        super(model, 4, LEVEL_DATA);
    }
}

class Maze3 extends GeneralMaze {
    private static final short[] LEVEL_DATA = {
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

    public Maze3(Controller model) {
        super(model, 6, LEVEL_DATA);
    }
}
