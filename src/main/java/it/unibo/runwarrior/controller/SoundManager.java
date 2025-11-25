package it.unibo.runwarrior.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import it.unibo.runwarrior.view.GameMusic;

/**
 * Manager to handle all the sounds of the game.
 */
public final class SoundManager {
    private static List<GameMusic> allSounds = new ArrayList<>();

    public static final Logger LOGGER = Logger.getLogger(SoundManager.class.getName());

    /**
     * Constructor of the class.
     */
    private SoundManager() {
        SoundManager.create("gameMusic.wav");
        SoundManager.create("hit.wav");
        SoundManager.create("jumpKill.wav");
        SoundManager.create("power.wav");
        SoundManager.create("sword.wav");
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

    public static List<GameMusic> getAllSounds() {
        return allSounds;
    }
}
