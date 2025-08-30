package it.unibo.runwarrior.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Rectangle;
import javax.swing.JFrame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.runwarrior.controller.coincontroller.api.CoinController;
import it.unibo.runwarrior.controller.coincontroller.impl.CoinControllerImpl;
import it.unibo.runwarrior.controller.collisions.impl.CoinDetectionImpl;
import it.unibo.runwarrior.controller.collisions.impl.CollisionDetectionImpl;
import it.unibo.runwarrior.controller.collisions.impl.KillDetectionImpl;
import it.unibo.runwarrior.controller.collisions.impl.PowerUpDetectionImpl;
import it.unibo.runwarrior.controller.player.CharacterComand;
import it.unibo.runwarrior.controller.score.api.ScoreController;
import it.unibo.runwarrior.controller.score.impl.ScoreControllerImpl;
import it.unibo.runwarrior.model.GameMap;
import it.unibo.runwarrior.model.Score;
import it.unibo.runwarrior.model.enemy.impl.EnemyImpl;
import it.unibo.runwarrior.model.player.ArmourWarrior;
import it.unibo.runwarrior.model.player.api.Character;
import it.unibo.runwarrior.model.player.impl.AbstractCharacterImpl;
import it.unibo.runwarrior.model.save.GameSaveManager;
import it.unibo.runwarrior.model.player.NakedWarrior;

/**
 * Test collision between entities and player, and consequences.
 */
class TestPlayerCollisions {
    private static final int FIFTY = 50;
    private static final int TWELVE = 12;
    private static final int FIVE_SIX_Z = 560;
    private static final int FIVE_SEV_SIX = 576;
    private static final int FIFTEEN = 15;
    private static final int FIVE_TWO_Z = 520;
    private static final int COLL_DOWN_TILE = 280;
    private static final int COLL_DOWN = 10_085;
    private static final int HUNDRED = 100;
    private static final int SIX_Z_FIVE = 605;
    private static final int SEVENTY = 70;
    private static final int POWERUP_X1 = 1984;
    private static final int POWERUP_X2 = 3290;
    private static final int POWERUP_X3 = 7343;
    private static final int POWERUP_X4 = 1949;
    private static final int SWORD_POINT = 585;
    private static final int TRY_TYLE = 36;
    private static final int TOLL = 5;
    private static final String FIRST_STRING = "tryMap.txt";
    private static final String SECOND_STRING = "Map2/forest_theme.txt";
    private static final String SKIN = "DEFAULT_SKIN";
    private final JFrame testFrame = new JFrame();
    private GameSaveManager gsm;
    private String prevSkin;
    private boolean prevPremiumSkin;
    private GameLoopController glc;
    private CharacterComand cmd;
    private GameMap gameMap1;
    private HandlerMapElement mapHandler1;
    private int tileSize;

    @BeforeEach
    void initCollisions() {
        gameMap1 = GameMap.load(FIRST_STRING, SECOND_STRING);
        mapHandler1 = new HandlerMapElement(gameMap1);
        cmd = new CharacterComand();
        gsm = GameSaveManager.getInstance();
        prevSkin = gsm.getSelectedSkinName();
        prevPremiumSkin = gsm.isSkinPremiumSbloccata();
        gsm.setSkinPremiumSbloccata(false);
        gsm.setSelectedSkinName(SKIN);
        glc = new GameLoopController(testFrame, FIRST_STRING, SECOND_STRING, 
        "/Map2/enemiesMap2.txt", "/Coins/CoinCoordinates_map2.txt", false);
        tileSize = TRY_TYLE;
    }

    private boolean isTouchingUp(final Rectangle playerArea, final Rectangle enemyArea) {
        return playerArea.y + playerArea.height <= enemyArea.y 
        && (playerArea.x + TOLL >= enemyArea.x && playerArea.x + TOLL <= enemyArea.x + enemyArea.width 
        || playerArea.x + playerArea.width - TOLL >= enemyArea.x 
        && playerArea.x + playerArea.width - TOLL <= enemyArea.x + enemyArea.width);
    }

    private boolean isBehindTile(final int x, final int y, final HandlerMapElement hM) {
        final int indexXtile = x / hM.getTileSize();
        final int indexYtile = y / hM.getTileSize();
        final int blockIndex = hM.getMap()[indexYtile][indexXtile];
        return hM.getBlocks().get(blockIndex).isCollision();
    }

    @Test
    void testCollisionTile() {
        final Character player = new NakedWarrior(glc, cmd, mapHandler1, null);
        final CollisionDetectionImpl collisionTiles = new CollisionDetectionImpl(gameMap1.getMapData(),
        mapHandler1.getBlocks(), tileSize, glc);

        collisionTiles.checkCollision(player);
        assertTrue(collisionTiles.touchSolid(TWELVE * tileSize, FIFTEEN * tileSize, false, player));
        assertFalse(collisionTiles.touchSolid(FIFTY * tileSize, FIFTEEN * tileSize, false, player));

        assertEquals("right", collisionTiles.checkCollisionDirection(HUNDRED * tileSize, FIVE_SIX_Z, 
        HUNDRED + 1, FIFTEEN, player));
        assertEquals("up", collisionTiles.checkCollisionDirection(SIX_Z_FIVE, TWELVE * tileSize, 
        FIFTEEN + 1, TWELVE, player));
        player.getArea().setLocation(COLL_DOWN, FIVE_TWO_Z + FIFTEEN);
        assertEquals("down", collisionTiles.checkCollisionDirection(COLL_DOWN, FIVE_TWO_Z + FIFTEEN, 
        COLL_DOWN_TILE, FIFTEEN - 1, player));

        player.getArea().setLocation(FIFTY * tileSize, (FIFTEEN - 1) * tileSize);
        assertTrue(collisionTiles.isInAir(player));
        player.getArea().setLocation(FIFTY * tileSize, (FIFTEEN * tileSize) + (tileSize / 2) 
        + AbstractCharacterImpl.TO_TOUCH_FLOOR);
        assertFalse(collisionTiles.isInAir(player));
        player.getArea().setLocation((HUNDRED + FIFTY) * tileSize - TOLL, FIVE_SIX_Z 
        + AbstractCharacterImpl.TO_TOUCH_FLOOR);
        assertEquals("left", collisionTiles.checkCollision(player));
    }

    @Test
    void testCollisionPowerup() {
        final PowerUpController pCon = new PowerUpController(glc, mapHandler1, mapHandler1.getMap());
        final Character player = new NakedWarrior(glc, cmd, mapHandler1, pCon);
        final PowerUpDetectionImpl collisionPowerups = new PowerUpDetectionImpl(glc, pCon);

        assertTrue(isTouchingUp(new Rectangle(FIFTY, FIFTY, FIFTY, FIFTY), 
        new Rectangle(SEVENTY, FIFTY * 2, FIFTY, FIFTY)));
        assertFalse(isTouchingUp(new Rectangle(FIFTY, FIFTY, FIFTY, FIFTY), 
        new Rectangle(SEVENTY, SEVENTY + TOLL * 2, FIFTY, FIFTY)));

        player.getArea().setLocation(POWERUP_X1, FIVE_TWO_Z + TWELVE);
        assertEquals("up", collisionPowerups.checkCollisionWithPowers(player, player.getMovementHandler()));
        player.getArea().setLocation(POWERUP_X2, FIVE_SIX_Z);
        assertEquals("right", collisionPowerups.checkCollisionWithPowers(player, player.getMovementHandler()));
        player.getArea().setLocation(POWERUP_X3, FIVE_SIX_Z);
        assertEquals("left", collisionPowerups.checkCollisionWithPowers(player, player.getMovementHandler()));

        player.getArea().setLocation(POWERUP_X4, FIVE_SIX_Z);
        final int i = glc.getPowersHandler().getPowers();
        collisionPowerups.checkCollisionWithPowers(player, player.getMovementHandler());
        assertEquals(i + 1, glc.getPowersHandler().getPowers());
        assertEquals(ArmourWarrior.class, glc.getPlayer().getClass());
    }

    @Test
    void testCollisionEnemies() {
        final PowerUpController pCon = new PowerUpController(glc, mapHandler1, mapHandler1.getMap());
        final Character player = new NakedWarrior(glc, cmd, mapHandler1, pCon);
        final EnemyImpl first = new EnemyImpl(POWERUP_X4, FIVE_SEV_SIX, tileSize, tileSize, true, glc.getEnemyHandler(), glc, 0);
        final EnemyImpl second = new EnemyImpl(POWERUP_X3, FIVE_SEV_SIX, tileSize, tileSize, true, glc.getEnemyHandler(), glc, 0);
        glc.getEnemyHandler().addEnemy(first);
        glc.getEnemyHandler().addEnemy(second);
        final KillDetectionImpl collisionEnemies = new KillDetectionImpl(glc, mapHandler1);

        assertTrue(isBehindTile(POWERUP_X2, SWORD_POINT, mapHandler1));
        assertFalse(isBehindTile(POWERUP_X1, SWORD_POINT, mapHandler1));
        assertEquals(2, glc.getEnemyHandler().getEnemies().size());
        player.getArea().setLocation(POWERUP_X4 + TOLL, FIVE_TWO_Z);
        collisionEnemies.checkCollisionWithEnemeies(player);
        assertTrue(isTouchingUp(player.getArea(), first.getBounds()));
        assertEquals(1, glc.getEnemyHandler().getEnemies().size());

        player.getArea().setLocation(POWERUP_X3 - TOLL, FIVE_SIX_Z);
        assertTrue(player.getArea().intersects(glc.getEnemyHandler().getEnemies().getFirst().getBounds()));
        final int i = glc.getPowersHandler().getPowers();
        collisionEnemies.checkCollisionWithEnemeies(player);
        assertEquals(i - 1, glc.getPowersHandler().getPowers());
    }

    @Test
    void testCoinCollision() {
        final CoinController coinController = new CoinControllerImpl();
        final Score score = new Score();
        final ScoreController scoreController = new ScoreControllerImpl(score);
        final Character player = new NakedWarrior(glc, cmd, mapHandler1, null);
        final CoinDetectionImpl collisionCoins = new CoinDetectionImpl(tileSize, coinController, scoreController);

        coinController.addCoins(FIFTEEN + 1, FIFTY);
        assertEquals(coinController.getCoinsCollected(), 0);
        player.getArea().setLocation(FIFTY * tileSize, FIVE_SIX_Z);
        collisionCoins.controlCoinCollision(player);
        assertEquals(coinController.getCoinsCollected(), 1);
    }

    @AfterEach
    void setPreviousState() {
        gsm.setSkinPremiumSbloccata(prevPremiumSkin);
        gsm.setSelectedSkinName(prevSkin);
    }
}
