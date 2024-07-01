package pacmanfx;

import javafx.scene.canvas.GraphicsContext;

public interface Enemy {
    void moveEnemy(GraphicsContext g2d);
    void drawEnemy(GraphicsContext g2d, int x, int y);

    public void setEnemyX(int[] i);
    public void setEnemyX(int i, int value);

    public void setEnemyDx(int[] i);
    public void setEnemyDx(int i, int value);

    public void setEnemyY(int[] i);
    public void setEnemyY(int i, int value);

    public void setEnemyDy(int[] i);
    public void setEnemyDy(int i, int value);

    public void setEnemySpeed(int[] i);
    public void setEnemySpeed(int i, int value);

    public void setDx(int[] i);
    public void setDy(int[] i);
}

class Spider implements Enemy {
    private Model model;
    private Player player;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;

    private int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    private int[] dx, dy;

    
    // Constructor to receive Model instance
    public Spider(Model model, Player player) {
        this.model = model;
        this.player = player;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);

        mazes = new Maze[]{maze1, maze2, maze3};    }
    
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
        int level = model.getCurrentLevel();
        
        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + mazes[level].getHBlocks() * (enemyY[i] / BLOCK_SIZE);

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
        int level = model.getCurrentLevel();
        int enemyCount = mazes[level].getEnemyCount();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < mazes[level].getEnemyCount() - 1; i++) {
            enemyX[i] = enemyX[i + 1];
            enemyY[i] = enemyY[i + 1];
            enemyDx[i] = enemyDx[i + 1];
            enemyDy[i] = enemyDy[i + 1];
            enemySpeed[i] = enemySpeed[i + 1];
        }
        model.playSound("kill.mp3");
        enemyCount--; // Decrease the count of ghosts
        mazes[level].setEnemyCount(enemyCount);
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.spiderImage, x, y);
    }
}

class Goblin implements Enemy {
    private Model model;
    private Player player;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;

    private int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    private int[] dx, dy;

    
    // Constructor to receive Model instance
    public Goblin(Model model, Player player) {
        this.model = model;
        this.player = player;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);

        mazes = new Maze[]{maze1, maze2, maze3};    }
    
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
        int level = model.getCurrentLevel();
        
        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + mazes[level].getHBlocks() * (enemyY[i] / BLOCK_SIZE);

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
        int level = model.getCurrentLevel();
        int enemyCount = mazes[level].getEnemyCount();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < mazes[level].getEnemyCount() - 1; i++) {
            enemyX[i] = enemyX[i + 1];
            enemyY[i] = enemyY[i + 1];
            enemyDx[i] = enemyDx[i + 1];
            enemyDy[i] = enemyDy[i + 1];
            enemySpeed[i] = enemySpeed[i + 1];
        }
        model.playSound("kill.mp3");
        enemyCount--; // Decrease the count of ghosts
        mazes[level].setEnemyCount(enemyCount);
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.assassin, x, y);
    }
}

class Phantom implements Enemy {
    private Model model;
    private Player player;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;

    private int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    private int[] dx, dy;

    
    // Constructor to receive Model instance
    public Phantom(Model model, Player player) {
        this.model = model;
        this.player = player;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);

        mazes = new Maze[]{maze1, maze2, maze3};    }
    
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
        int level = model.getCurrentLevel();
        
        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + mazes[level].getHBlocks() * (enemyY[i] / BLOCK_SIZE);

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
            boolean powerUp = player.getPowerUp();
            
            if (player.getPlayerX() > (enemyX[i] - 12) && player.getPlayerX() < (enemyX[i] + 12) && player.getPlayerY() > (enemyY[i] - 12) && player.getPlayerY() < (enemyY[i] + 12) && inGame) {
                if (!powerUp) {
                    player.checkBlinded();
                    removeEnemy(i);
                }
                else {
                    removeEnemy(i);
                }
            }
        }
    }
    
    private void removeEnemy(int index) {
        int level = model.getCurrentLevel();
        int enemyCount = mazes[level].getEnemyCount();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < mazes[level].getEnemyCount() - 1; i++) {
            enemyX[i] = enemyX[i + 1];
            enemyY[i] = enemyY[i + 1];
            enemyDx[i] = enemyDx[i + 1];
            enemyDy[i] = enemyDy[i + 1];
            enemySpeed[i] = enemySpeed[i + 1];
        }
        model.playSound("kill.mp3");
        enemyCount--; // Decrease the count of ghosts
        mazes[level].setEnemyCount(enemyCount);
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.heart, x, y);
    }
}

class Skeleton implements Enemy {
    private Model model;
    private Player player;
    private Maze1 maze1;
    private Maze2 maze2;
    private Maze3 maze3;
    private Maze[] mazes;

    private int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    private int[] dx, dy;

    
    // Constructor to receive Model instance
    public Skeleton(Model model, Player player) {
        this.model = model;
        this.player = player;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);

        mazes = new Maze[]{maze1, maze2, maze3};    }
    
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
        int level = model.getCurrentLevel();
        
        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            if (enemyX[i] % BLOCK_SIZE == 0 && enemyY[i] % BLOCK_SIZE == 0) {
                pos = enemyX[i] / BLOCK_SIZE + mazes[level].getHBlocks() * (enemyY[i] / BLOCK_SIZE);

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
        int level = model.getCurrentLevel();
        int enemyCount = mazes[level].getEnemyCount();
        // Shift elements to the left to remove the ghost at indexToRemove
        for (int i = index; i < mazes[level].getEnemyCount() - 1; i++) {
            enemyX[i] = enemyX[i + 1];
            enemyY[i] = enemyY[i + 1];
            enemyDx[i] = enemyDx[i + 1];
            enemyDy[i] = enemyDy[i + 1];
            enemySpeed[i] = enemySpeed[i + 1];
        }
        model.playSound("kill.mp3");
        enemyCount--; // Decrease the count of ghosts
        mazes[level].setEnemyCount(enemyCount);
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.skeletonImage, x, y);
    }
}