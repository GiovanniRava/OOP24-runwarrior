package it.unibo.runwarrior.view;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import it.unibo.runwarrior.controller.HandlerMapElement;
import it.unibo.runwarrior.model.PowerUpImpl;

/**
 * Class that creates powerup objects and print them on the panel.
 */
public class PowerUpFactoryImpl {
    public static final int END_OF_POWERUP = 222;
    public static final int NUM_POWERUP = 6;
    public static final int FIRST_DISTANCE_POWERUP = 55;
    private ArrayList<PowerUpImpl> powerUps = new ArrayList<>();
    private HandlerMapElement mapHandler;
    private int tileSize;
    private int distance;
    private int space;

    /**
     * Constructor that creates the list of powerup by using the game-loop panel, the map handler and the map.
     *
     * @param glp game-loop panel
     * @param hM map handler
     * @param map map
     */
    public PowerUpFactoryImpl(final GameLoopPanel glp, final HandlerMapElement hM, final int[][] map) {
        this.mapHandler = hM;
        this.tileSize = hM.getTileSize();
        distance = FIRST_DISTANCE_POWERUP;
        space = END_OF_POWERUP / NUM_POWERUP;
        for (int i = 0; i < NUM_POWERUP; i++) {
            final PowerUpImpl p = new PowerUpImpl(glp);
            int row = 0;
            while (map[row][distance] != 1) {
                row++;
            }
            p.getTouchArea().setBounds(distance * tileSize, (row - 1) * tileSize, tileSize, tileSize);
            powerUps.add(p);
            distance = distance + space;
        }
    }

    /**
     * can be used when a powerup comes from a dead enemy.
     * It adds the powerup to the list so that it could be printed. Not used.
     *
     * @param pow powerup object
     */
    public void powerUpAppearance(final PowerUpImpl pow) {
        this.powerUps.add(pow);
    }

    /**
     * Prints the egg if the powerup isn't taken or the powerup if the egg has been opened.
     * If the powerup is taken, set his area at 0.
     *
     * @param gr2 Graphics object used to print
     */
    public void printPowerUp(final Graphics2D gr2) {
        BufferedImage im;
        for (final PowerUpImpl p : powerUps) {
            p.powerUpImage();
            if (!p.isEggOpen()) {
                im = p.getEgg();
            } else {
                im = p.getImage();
            }
            if (!p.isPowerTaken()) {
                gr2.drawImage(im, 
                p.getTouchArea().x + mapHandler.getShift(), p.getTouchArea().y, tileSize, p.getTouchArea().height, null);
            } else {
                p.getTouchArea().setSize(0, 0);
            }
        }
    }

    /**
     * @return the list of powerups
     */
    public ArrayList<PowerUpImpl> getPowerUps() {
        return this.powerUps;
    }
}
