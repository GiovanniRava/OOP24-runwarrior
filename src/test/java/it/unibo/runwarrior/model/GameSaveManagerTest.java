package it.unibo.runwarrior.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.runwarrior.model.save.GameSaveManager;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;

/**
 * GameSaveManagerTest checks if the singleton is respected.
 * Checks if the saving and loading works.
 */
class GameSaveManagerTest {
    private static final int COIN_NUM = 115;
    private static final int WRONG_LEVEL_NUM = 5;
    private GameSaveManager gsm;
    private int prevLevelsCompleted;
    private int prevCoinCollected;
    private boolean prevPremiumSkin;
    private String prevSkin;

    /**
     * Create a test instance of the game with random variables.
     */
    @BeforeEach
    void resetSingleton() {
        gsm = GameSaveManager.getInstance();

        prevLevelsCompleted = gsm.getLevelsCompleted();
        prevCoinCollected = gsm.getCoinCollected();
        prevSkin = gsm.getSelectedSkinName();
        prevPremiumSkin = gsm.isSkinPremiumSbloccata();

        gsm.setLevelsCompleted(2);
        gsm.setCoinCollected(100);
        gsm.setSkinPremiumSbloccata(false);
    }

    /**
     * Set the game state before the state.
     */
    @AfterEach
    void setPreviousState() {
        gsm.setCoinCollected(prevCoinCollected);
        gsm.setLevelsCompleted(prevLevelsCompleted);
        gsm.setSkinPremiumSbloccata(prevPremiumSkin);
        gsm.setSelectedSkinName(prevSkin);
    }

    /**
     * Check if the pattern singleton is effective and there is only one saving file.
     */
    @Test
    void testSingletonInstance() {
        final GameSaveManager anotherInstance = GameSaveManager.getInstance();
        assertSame(gsm, anotherInstance, "Le istanze devono essere le stesse (singleton)");
    }

    /**
     * Test both the saving in the file and the loading. Every time a setter method is called
     * is called also saveGame().
     */
    @Test
    void testSaveAndLoadFromFile() {
        assertEquals(2, GameSaveManager.getInstance().getLevelsCompleted());
        gsm.setLevelsCompleted(3);
        assertEquals(3, GameSaveManager.getInstance().getLevelsCompleted());
        assertEquals(100, GameSaveManager.getInstance().getCoinCollected());
        gsm.setCoinCollected(COIN_NUM);
        assertEquals(COIN_NUM, GameSaveManager.getInstance().getCoinCollected());
        gsm.setSkinPremiumSbloccata(true);
        assertTrue(GameSaveManager.getInstance().isSkinPremiumSbloccata());
    }

    /**
     * Checks that the right exception is thrown.
     */
    @Test
    void testWrongLevelsSaving() {
        assertThrows(IllegalArgumentException.class, () -> gsm.setLevelsCompleted(WRONG_LEVEL_NUM));
    }
}
