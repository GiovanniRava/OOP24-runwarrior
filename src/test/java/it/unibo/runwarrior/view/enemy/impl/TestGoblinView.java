package it.unibo.runwarrior.view.enemy.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.runwarrior.controller.GameLoopController;

/**
 * Test if the images are loaded correctly and view is ok.
 */
class TestGoblinView {
    private static final int DIM_IMAGES = 36;
    private GoblinView goblinView;
    private final JFrame mainFrame = new JFrame();

    /**
     * Create a new GameLoopController for the view.
     */
    @BeforeEach
    void setUp() {
        final GameLoopController glc = new GameLoopController(mainFrame, "tryMap.txt", "Map2/forest_theme.txt", 
                            "/Map2/enemiesMap2.txt", "/Coins/CoinCoordinates_map2.txt", false);
        goblinView = new GoblinView(glc);
    }

    /**
     * Test the right uploading of the images.
     */
    @Test
    void testLoadResources() {
        assertNotNull(goblinView.getRightGoblin());
        assertNotNull(goblinView.getLeftGoblin());
        assertNotNull(goblinView.getRightGoblinMoving());
        assertNotNull(goblinView.getLeftGoblinMoving());
        assertEquals(DIM_IMAGES, goblinView.getRightGoblin().getWidth());
    }
}
