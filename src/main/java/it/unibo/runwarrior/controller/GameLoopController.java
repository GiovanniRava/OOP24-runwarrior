package it.unibo.runwarrior.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.runwarrior.controller.coincontroller.api.CoinController;
import it.unibo.runwarrior.controller.coincontroller.impl.CoinControllerImpl;
import it.unibo.runwarrior.controller.enemy.EnemySpawner;
import it.unibo.runwarrior.controller.enemy.impl.EnemyHandlerImpl;
import it.unibo.runwarrior.controller.player.CharacterComand;
import it.unibo.runwarrior.controller.score.api.ScoreController;
import it.unibo.runwarrior.controller.score.impl.ScoreControllerImpl;
import it.unibo.runwarrior.model.player.api.Character;
import it.unibo.runwarrior.model.GameMap;
import it.unibo.runwarrior.model.Score;
import it.unibo.runwarrior.model.player.NakedWarrior;
import it.unibo.runwarrior.model.player.NakedWizard;
import it.unibo.runwarrior.model.save.GameSaveManager;
import it.unibo.runwarrior.view.GameLoopPanel;
import it.unibo.runwarrior.view.PowerUpManager;
import it.unibo.runwarrior.view.enemy.impl.EnemyViewFactoryImpl;
import it.unibo.runwarrior.view.enemy.impl.GoblinView;
import it.unibo.runwarrior.view.enemy.impl.GuardView;
import it.unibo.runwarrior.view.enemy.impl.MonkeyView;
import it.unibo.runwarrior.view.enemy.impl.SnakeView;
import it.unibo.runwarrior.view.enemy.impl.WizardView;

/**
 * Controller del ciclo principale di gioco.
 * Gestisce l'inizializzazione dei campi necessari e gestisce l'aggiornamento.
 */
public class GameLoopController {
    public static final Logger LOGGER = Logger.getLogger(GameLoopController.class.getName());
    private static final String WARNING = "EI_EXPOSE_REP";
    private static final int TYPE_FIVE = 5;
    private final GameLoopPanel glp;
    private Character player;
    private final CharacterComand commands;
    private final PowersHandler powerUpsHandler;
    private final PowerUpController powerController;
    private final PowerUpManager powersManager;

    private final HandlerMapElement mapHandler;
    private final EnemyHandlerImpl enemyHandler;
    private final EnemyViewFactoryImpl enemyViewFactory;
    private final EnemySpawner enemySpawner;
    private final CoinController coinController;
    private final ScoreController scoreController;
    private final SoundManager soundManager;
    private final int levelIndex;

    /**
     * Constructor of the class. 
     *
     * @param mainFrame is the frame in which menu is shown
     * @param mapPath the path for loading of the map
     * @param themePath the path for loading the images of tile
     * @param enemiesPath the path for loadin the enemies position
     * @param coinsPath the path for loading the coin position
     * @param createPanel true if the glp has to be created
     */
    public GameLoopController(final JFrame mainFrame, final String mapPath, final String themePath,
                                final String enemiesPath, final String coinsPath, final boolean createPanel) {
        final GameMap gameMap = GameMap.load(mapPath, themePath);
        this.coinController = new CoinControllerImpl();
        this.levelIndex = calculateLevelIndex(mapPath);
        final List<int[]> coords = coinController.loadCoinFromFile(coinsPath);
        for (final int[] coord : coords) {
            coinController.addCoins(coord[0], coord[1]);
        }
        this.commands = new CharacterComand();
        this.mapHandler = new HandlerMapElement(gameMap);
        this.soundManager = new SoundManager();
        this.powerController = new PowerUpController(this, mapHandler, gameMap.getMapData());
        this.powersManager = new PowerUpManager(powerController.getPowerUps(), mapHandler);
        this.powerUpsHandler = new PowersHandler(this, commands, mapHandler, powerController);
        this.enemyViewFactory = new EnemyViewFactoryImpl();
        initializeEnemyViewFactory();
        this.enemyHandler = new EnemyHandlerImpl(this, this.enemyViewFactory);
        this.enemySpawner = new EnemySpawner(enemyHandler, this);
        try (InputStream is = GameLoopController.class.getResourceAsStream(enemiesPath)) {
            enemySpawner.loadEnemiesFromStream(is);
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot load enemies");
        }

        final Score score = new Score();
        this.scoreController = new ScoreControllerImpl(score);
        if (createPanel) {
            this.glp = new GameLoopPanel(mainFrame, this);
        } else {
            this.glp = null;
        }
        initializePlayer();
    }

    /**
     * Update the state of the game in the order: player, enemySpawner, enemyHandler.
     */
    public void update() {
        player.update();
        enemySpawner.update();
        enemyHandler.updateWithMap(mapHandler.getCollisionRectangles());
    }

    /**
     * Chooses one of the two player with whom the game starts.
     * To be connected with the shop
     */
    private void initializePlayer() {
        final String selectedSkin = GameSaveManager.getInstance().getSelectedSkinName();
        final boolean wizardUnlocked = GameSaveManager.getInstance().isSkinPremiumSbloccata();
        if ("WIZARD".equalsIgnoreCase(selectedSkin) && wizardUnlocked) {
            player = new NakedWizard(this, commands, mapHandler, powerController);
        } else {
            player = new NakedWarrior(this, commands, mapHandler, powerController);
        }
        player.getMovementHandler().setStartY(mapHandler.getFirstY());
        powerUpsHandler.setIndex();
    }

    /**
     * Getters for the main player.
     *
     * @return the player
     */
    @SuppressFBWarnings(WARNING)
    public Character getPlayer() {
        return this.player;
    }

    /**
     * Sets the current player in the panel. Suppress warning cause the player has to change in a reactive way.
     *
     * @param pl current player
     * @param realX x position in the map
     * @param x x position in the screen
     * @param y y position in the screen
     * @param shift variable to slide map
     * @param lastHit handles immortality time
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public void setPlayer(final Character pl, final int realX, final int x, final int y, final int shift, final long lastHit) {
        this.player = pl;
        this.player.getMovementHandler().setLocationAfterPowerup(x, y, realX, shift, lastHit);
        this.coinController.updatePlayer(pl);
    }

    /**
     * Return the main gameLoopPanel in which everything is shown.
     *
     * @return GameLoopPanel
     */
    @SuppressFBWarnings(WARNING)
    public GameLoopPanel getGlp() {
        return this.glp;
    }

    /**
     * @return the current set of character commands
     */
    @SuppressFBWarnings(WARNING)
    public CharacterComand getCommands() {
        return this.commands;
    }

    /**
     * @return the handler that controls power‑ups
     */
    @SuppressFBWarnings(WARNING)
    public PowersHandler getPowersHandler() {
        return this.powerUpsHandler;
    }

    /**
     * @return the manager responsible for rendering power‑ups
     */
    public PowerUpManager getPowersManager() {
        return this.powersManager;
    }

    /**
     * @return the map handler that provides tiles and collision list
     */
    @SuppressFBWarnings(WARNING)
    public HandlerMapElement getMapHandler() {
        return this.mapHandler;
    }

    /**
     * @return the component that controls enemies
     */
    @SuppressFBWarnings(WARNING)
    public EnemyHandlerImpl getEnemyHandler() {
        return this.enemyHandler;
    }

    /**
     * @return the controller that manages coins on the map
     */
    @SuppressFBWarnings(WARNING)
    public CoinController getCoinController() {
        return this.coinController;
    }

    /**
     * @return the controller that updates the score
     */
    @SuppressFBWarnings(WARNING)
    public ScoreController getScoreController() {
        return this.scoreController;
    }

    /**
     * Map the int type with the correct EnemyView.
     */
    private void initializeEnemyViewFactory() {
        enemyViewFactory.register(1, new GuardView(this));
        enemyViewFactory.register(2, new SnakeView(this));
        enemyViewFactory.register(3, new WizardView(this));
        enemyViewFactory.register(4, new GoblinView(this));
        enemyViewFactory.register(TYPE_FIVE, new MonkeyView(this));
    }

    /**
     * @param mapPath the path for loading of the map
     * @return the number of the current level
     */
    private int calculateLevelIndex(final String mapPath) {
        final String lowerCasePath = mapPath.toLowerCase(Locale.ROOT);
        if (lowerCasePath.contains("map_1") || lowerCasePath.contains("map1")) {
            return 1;
        } else if (lowerCasePath.contains("map_2") || lowerCasePath.contains("map2")) {
            return 2;
        } else if (lowerCasePath.contains("map_3") || lowerCasePath.contains("map3")) {
            return 3;
        } else {
            return 1;
        }
}

    /**
     * @return the current level 
     */
    public final int getCurrentLevel() {
        return this.levelIndex;
    }
}
