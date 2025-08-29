package it.unibo.runwarrior.model.save;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * GameSaveManager handles the saving part of the game in the file game_save.txt.
 */
public final class GameSaveManager {
    private static final String SAVE_FILE = "game_save.txt";
    private static final String DEFAULT_STRING = "DEFAULT_SKIN";
    private static final String WIZARD = "WIZARD";
    private static final int MAX_LEVEL = 3;
    private static final Logger LOGGER = Logger.getLogger(GameSaveManager.class.getName());

    private int levelsCompleted;
    private int coinCollected;
    private boolean premiumSkinUnlocked;
    private String selectedSkinName;

    /**
     * Private constructor of the class GameSaveManager.
     */
    private GameSaveManager() {
        levelsCompleted = 0;
        coinCollected = 0;
        premiumSkinUnlocked = false;
    }

    /**
     * Create and initialize the instance.
     *
     * @return the instance created
     */
    private static GameSaveManager createInstance() {
        GameSaveManager gsm = loadFromFile(SAVE_FILE);
        if (gsm == null) {
            gsm = new GameSaveManager();
        }
        return gsm;
    }

    /**
     * Returns the singleton instance of the GameSaveManager.
     *
     * @return the only instance of GameSaveManager
     */
    public static GameSaveManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Save the changes of the variables in the file.
     */
    public void saveGame() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SAVE_FILE, StandardCharsets.UTF_8))) {
            writer.write(Integer.toString(levelsCompleted));
            writer.newLine();
            writer.write(Integer.toString(coinCollected));
            writer.newLine();
            writer.write(Boolean.toString(premiumSkinUnlocked));
            writer.newLine();
            writer.write(selectedSkinName != null ? selectedSkinName : DEFAULT_STRING);
            writer.newLine();
        } catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "Cannot write in saving file");
        }
    }

    /**
     * Read the variables from file so can be used.
     *
     * @param filePath of the saving file
     * @return the GameSaveManger instance
     */
    private static GameSaveManager loadFromFile(final String filePath) {
        final File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        final GameSaveManager gsm = new GameSaveManager();
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            line = reader.readLine();
            if (line != null) {
                gsm.levelsCompleted = Integer.parseInt(line.trim());
            }
            line = reader.readLine();
            if (line != null) {
                gsm.coinCollected = Integer.parseInt(line.trim());
            }
            line = reader.readLine();
            if (line != null) {
                gsm.premiumSkinUnlocked = Boolean.parseBoolean(line.trim());
            }
            line = reader.readLine();
            gsm.selectedSkinName = (line != null) ? line.trim() : DEFAULT_STRING;
            return gsm;
        } catch (IOException | NumberFormatException e) {
            LOGGER.log(Level.SEVERE, "Cannot load from file");
            return null;
        }
    }

    /**
     * Getters for the completed levels.
     *
     * @return the numeber of completed levels
     */
    public int getLevelsCompleted() {
        return levelsCompleted;
    }

    /**
     * Set the new number of levels completed.
     *
     * @param levelsCompleted the new number of completed levels that needs to be >0 && <=3
     */
    public void setLevelsCompleted(final int levelsCompleted) {
        if (levelsCompleted < 0 || levelsCompleted > 3) {
            throw new IllegalArgumentException(
                "I livelli sono compresi tra 1 e " + MAX_LEVEL + " Provato a salvare: " + levelsCompleted);
        }
        this.levelsCompleted = levelsCompleted;
        saveGame();
    }

    /**
     * Getters forr the coin.
     *
     * @return the numbers of coin collected
     */
    public int getCoinCollected() {
        return coinCollected;
    }

    /**
     * Set the number of coin collected.
     *
     * @param coinCollected the new number of coin collected 
     */
    public void setCoinCollected(final int coinCollected) {
        this.coinCollected = coinCollected;
        saveGame();
    }

    /**
     * Tells whether or not the player has unlocked the skin premium.
     *
     * @return a boolean 
     */
    public boolean isSkinPremiumSbloccata() {
        return premiumSkinUnlocked;
    }

    /**
     * Set the new skin as unlocked when it's purchased.
     *
     * @param unlocked a boolean that indicates that the new skin has been purchased
     */
    public void setSkinPremiumSbloccata(final boolean unlocked) {
        this.premiumSkinUnlocked = unlocked;
        saveGame();
    }

    /**
     * Called every conclusion of the level to add the coin to the final amount.
     *
     * @param quantity the number of coin to be added
     */
    public void addCoin(final int quantity) {
        this.coinCollected += quantity;
        saveGame();
    }

    /**
     * Save the game when the user exit the application.
     */
    public void onGameExit() {
        saveGame();
    }

    /**
     * Reset the game to starting value.
     */
    public void resetGame() {
        this.coinCollected = 0;
        this.levelsCompleted = 0;
        this.premiumSkinUnlocked = false;
        this.selectedSkinName = DEFAULT_STRING;
        saveGame();
    }

    /**
     * Tells if the user has selected the premium skin or the basic one.
     *
     * @return a string that indicates the skin type
     */
    public String getSelectedSkinName() {
        return selectedSkinName;
    }

    /**
     * Set the skin in which the user want to play the game.
     *
     * @param name is the name of the skin used
     */
    public void setSelectedSkinName(final String name) {
        if (WIZARD.equalsIgnoreCase(name) && !premiumSkinUnlocked) {
            return; //se la skin non è sbloccata non viene salvata
        }
        this.selectedSkinName = name;
        saveGame();
    }

    /**
     * Holder class that contains the singleton instance.
     * Loaded only when getInstance() is called for the first time.
     */
    private static final class Holder {
        private static final GameSaveManager INSTANCE = createInstance();
    }
}
