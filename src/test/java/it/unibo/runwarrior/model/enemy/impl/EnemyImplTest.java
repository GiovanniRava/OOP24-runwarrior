package it.unibo.runwarrior.model.enemy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;
import java.util.List;

import javax.swing.JFrame;

import it.unibo.runwarrior.controller.GameLoopController;
import it.unibo.runwarrior.controller.enemy.impl.EnemyHandlerImpl;
import it.unibo.runwarrior.view.enemy.impl.EnemyViewFactoryImpl;

/**
 * Test class for EnemyImpl, in which is tested the right positioning and collision.
 */
public class EnemyImplTest {
    private static final int STANDARD_X = 50;
    private static final int STANDARD_Y = 100;
    private static final int TILE_SIZE = 64;
    private static final int COLLISION_X = 15;
    private static final int COLLISION_TILE = 48;
    private static final int COLLISION_TILE_WIDHT = 33;
    private static final int STANDARD_VEL = 2;
    private static final int OBSTACLE_X = 60;
    private EnemyImpl enemy;
    private GameLoopController glc;
    private EnemyHandlerImpl enemyHandler;
    private EnemyViewFactoryImpl enemyViewFactory;
    private JFrame mainFrame;

    /**
     * Sets up a new EnemyImpl instance.
     */
    @BeforeEach
    public void setUp() {
        glc = new GameLoopController(mainFrame, "tryMap.txt", "Map2/forest_theme.txt", 
                                        "/Map2/enemiesMap2.txt", "/Coins/CoinCoordinates_map2.txt");
        glc.getGlp().startGame();
        glc.getGlp().endGame();
        enemyHandler = new EnemyHandlerImpl(glc, enemyViewFactory);
        enemy = new EnemyImpl(STANDARD_X, STANDARD_Y, TILE_SIZE, TILE_SIZE, true, enemyHandler, glc, 1); 
    }

    /**
     * Verifies that getBounds() returns the correct rectangle based on position and size.
     */
    @Test
    public void testGetBounds() {
        final Rectangle bounds = enemy.getBounds();
        assertEquals(COLLISION_X, bounds.x);
        assertEquals(STANDARD_Y, bounds.y);
        assertEquals(COLLISION_TILE_WIDHT, bounds.width);
        assertEquals(COLLISION_TILE, bounds.height);
    }

    /**
     * Ensures that checkMapCollision inverts velocity.
     */
    @Test
    public void testCheckMapCollisionBlocksMovement() {
        final Rectangle obstacle = new Rectangle(OBSTACLE_X, STANDARD_Y, TILE_SIZE, TILE_SIZE);
        enemy.setVelocityX(STANDARD_VEL);
        enemy.checkMapCollision(List.of(obstacle));
        assertEquals(-STANDARD_VEL, enemy.getVelocityX());
    }

    /**
     * This method check if when the enemy is dead is removed from the list.
     */
    @Test 
    public void testDie() {
        enemyHandler.addEnemy(enemy);
        assertEquals(1, enemyHandler.getEnemies().size());
        enemy.die();
        assertEquals(0, enemyHandler.getEnemies().size());
    }
}

