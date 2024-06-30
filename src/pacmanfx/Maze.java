/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pacmanfx;

import javafx.scene.canvas.GraphicsContext;

/**
 *
 * @author ongzh
 */
public interface Maze {
    short[] getLevelData();
    int getEnemyCount();
    void setEnemyCount(int enemyCount);
    int getHBlocks();
    int getVBlocks();
    void drawMaze(GraphicsContext g2d);
}
