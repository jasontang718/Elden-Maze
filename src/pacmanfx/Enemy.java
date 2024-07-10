package pacmanfx;

import javafx.scene.canvas.GraphicsContext;

public interface Enemy {
    void moveEnemy(GraphicsContext g2d);
    void drawEnemy(GraphicsContext g2d, int x, int y);

    void setEnemyX(int[] i);
    void setEnemyX(int i, int value);

    void setEnemyDx(int[] i);
    void setEnemyDx(int i, int value);

    void setEnemyY(int[] i);
    void setEnemyY(int i, int value);

    void setEnemyDy(int[] i);
    void setEnemyDy(int i, int value);

    void setEnemySpeed(int[] i);
    void setEnemySpeed(int i, int value);

    void setDx(int[] i);
    void setDy(int[] i);
}

abstract class AbstractEnemy implements Enemy {
    protected Model model;
    protected Character[] characters;
    protected Maze1 maze1;
    protected Maze2 maze2;
    protected Maze3 maze3;
    protected Maze[] mazes;

    protected int[] enemyX, enemyY, enemyDx, enemyDy, enemySpeed;
    protected int[] dx, dy;

    // Constructor to receive Model instance
    public AbstractEnemy(Model model, Character[] characters) {
        this.model = model;
        this.characters = characters;
        this.maze1 = new Maze1(model);
        this.maze2 = new Maze2(model);
        this.maze3 = new Maze3(model);
        this.mazes = new Maze[]{maze1, maze2, maze3};
    }

    @Override
    public void setEnemyX(int[] i) {
        this.enemyX = i;
    }

    @Override
    public void setEnemyX(int index, int value) {
        this.enemyX[index] = value;
    }

    @Override
    public void setEnemyY(int[] i) {
        this.enemyY = i;
    }

    @Override
    public void setEnemyY(int index, int value) {
        this.enemyY[index] = value;
    }

    @Override
    public void setEnemyDx(int[] i) {
        this.enemyDx = i;
    }

    @Override
    public void setEnemyDx(int index, int value) {
        this.enemyDx[index] = value;
    }

    @Override
    public void setEnemyDy(int[] i) {
        this.enemyDy = i;
    }

    @Override
    public void setEnemyDy(int index, int value) {
        this.enemyDy[index] = value;
    }

    @Override
    public void setEnemySpeed(int[] i) {
        this.enemySpeed = i;
    }

    @Override
    public void setEnemySpeed(int index, int value) {
        this.enemySpeed[index] = value;
    }

    @Override
    public void setDx(int[] i) {
        this.dx = i;
    }

    @Override
    public void setDy(int[] i) {
        this.dy = i;
    }

    @Override
    public void moveEnemy(GraphicsContext g2d) {
        int pos, count, BLOCK_SIZE = model.getBlockSize(), level = model.getCurrentLevel(), characterNo = model.getCharacterNo();

        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            double distance = Math.sqrt(Math.pow(characters[characterNo].getPlayerX() - enemyX[i], 2) + Math.pow(characters[characterNo].getPlayerY() - enemyY[i], 2));

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
                    if (count > 3) count = 3;
                    enemyDx[i] = dx[count];
                    enemyDy[i] = dy[count];
                }
            }

            if (!(characters[characterNo].getPowerUp() && characters[characterNo] == characters[2])){
                enemyX[i] += enemyDx[i] * enemySpeed[i];
                enemyY[i] += enemyDy[i] * enemySpeed[i];
            }
            
            if (!characters[characterNo].getSlowed() || (characters[characterNo].getSlowed() && distance <= 100)) {
                drawEnemy(g2d, enemyX[i] + 1, enemyY[i] + 1);
            }
            
            boolean powerUp = characters[characterNo].getPowerUp();
            boolean inGame = model.getInGame();
            
            if (characters[characterNo].getPlayerX() > (enemyX[i] - 12) && characters[characterNo].getPlayerX() < (enemyX[i] + 12)
                    && characters[characterNo].getPlayerY() > (enemyY[i] - 12) && characters[characterNo].getPlayerY() < (enemyY[i] + 12) && inGame) {
                if (!powerUp) {
                    model.setDying(true);
                    characters[characterNo].setSlowed(false);
                }
                else if (powerUp && (characters[characterNo] == characters[1] || characters[characterNo] == characters[2])){
                    
                }
                else {
                    removeEnemy(i);
                }
            }
        }
    }

    protected void removeEnemy(int i) {
        enemyX[i] = -100;
        enemyY[i] = -100;
        enemyDx[i] = 0;
        enemyDy[i] = 0;
        enemySpeed[i] = 0;
        model.playSound("kill.mp3");
    }
}

class Spider extends AbstractEnemy {
    public Spider(Model model, Character[] characters) {
        super(model, characters);
    }

    @Override
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.spiderImage, x, y);
    }
}

class Goblin extends AbstractEnemy {
    public Goblin(Model model, Character[] characters) {
        super(model, characters);
    }

    @Override
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.assassinImage, x, y);
    }
}

class Phantom extends AbstractEnemy{
    public Phantom(Model model, Character[] characters) {
        super(model, characters);
    }

    public void moveEnemy(GraphicsContext g2d) {
        int pos;
        int count;
        int BLOCK_SIZE = model.getBlockSize();
        int level = model.getCurrentLevel();
        int characterNo = model.getCharacterNo();
        
        for (int i = 0; i < mazes[level].getEnemyCount(); i++) {
            double distance = Math.sqrt(Math.pow(characters[characterNo].getPlayerX() - enemyX[i], 2) + Math.pow(characters[characterNo].getPlayerY() - enemyY[i], 2));
            
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

            if (!(characters[characterNo].getPowerUp() && characters[characterNo] == characters[2])){
                enemyX[i] += enemyDx[i] * enemySpeed[i];
                enemyY[i] += enemyDy[i] * enemySpeed[i];
            }
            
            if (!characters[characterNo].getSlowed()){
                drawEnemy(g2d, enemyX[i] + 1, enemyY[i] + 1);
            }
            else if (characters[characterNo].getSlowed() && distance <= 100){
                drawEnemy(g2d, enemyX[i] + 1, enemyY[i] + 1);
            }

            boolean inGame = model.getInGame();
            boolean powerUp = characters[characterNo].getPowerUp();
            
            if (characters[characterNo].getPlayerX() > (enemyX[i] - 12) && characters[characterNo].getPlayerX() < (enemyX[i] + 12)
                    && characters[characterNo].getPlayerY() > (enemyY[i] - 12) && characters[characterNo].getPlayerY() < (enemyY[i] + 12) && inGame) {
                if (!powerUp) {
                    characters[characterNo].checkSlowed(g2d);
                    removeEnemy(i);
                }
                else if (powerUp && (characters[characterNo] == characters[1] || characters[characterNo] == characters[2])){
                    
                }
                else {
                    removeEnemy(i);
                }
            }
        }
    }
    
    public void removeEnemy(int i) {
        enemyX[i] = -100;
        enemyY[i] = -100;
        enemyDx[i] = 0;
        enemyDy[i] = 0;
        enemySpeed[i] = 0;
        model.playSound("kill.mp3");
    }
    
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.fire, x, y);
    }
}

class Skeleton extends AbstractEnemy {
    public Skeleton(Model model, Character[] characters) {
        super(model, characters);
    }

    @Override
    public void drawEnemy(GraphicsContext g2d, int x, int y) {
        g2d.drawImage(model.skeletonImage, x, y);
    }
}
