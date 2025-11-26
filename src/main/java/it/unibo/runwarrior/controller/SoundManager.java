package it.unibo.runwarrior.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.unibo.runwarrior.view.GameMusic;

/**
 * Manager to handle all the sounds of the game.
 */

public final class SoundManager {
    public static final Logger LOGGER = Logger.getLogger(SoundManager.class.getName());
    private static List<GameMusic> allSounds = new ArrayList<>();

    /**
     * Constructor of the class.
     */
    public SoundManager() {
        create("gameMusic.wav");
        create("hit.wav");
        create("jumpKill.wav");
        create("power.wav");
        create("sword.wav");
    }

    /**
     * Create a new instance of GameMusic and add it to the list.
     *
     * @param musicFile the name of the music file to load.
     * @return the created GameMusic object.
     */
    public static GameMusic create(final String musicFile) {
        if (musicFile == null) {
            LOGGER.log(Level.SEVERE, "Cannot load or open audio files");
        }
        final GameMusic sound = new GameMusic(musicFile);
        allSounds.add(sound);
        return sound;
    }

    /**
     * Close all registered GameMusic objects.
     */
    public static void closeAll() {
        for (final GameMusic sound : allSounds) {
            sound.close();
        }
        allSounds.clear();
    }

    /**
     * Getters for allsounds.
     *
     * @return the list of sounds
     */
    public static List<GameMusic> getAllSounds() {
        return new ArrayList<>(allSounds);
    }
}
