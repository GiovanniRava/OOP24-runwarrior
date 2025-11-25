package it.unibo.runwarrior.controller;

import java.util.ArrayList;
import java.util.List;

import it.unibo.runwarrior.view.GameMusic;

/**
 * Manager to handle all the sounds of the game.
 */
public final class SoundManager {
    private static List<GameMusic> allSounds = new ArrayList<>();

    /**
     * Constructor of the class.
     */
    private SoundManager() {
        //void
    }

    /**
     * Create a new instance of GameMusic and add it to the list.
     *
     * @param musicFile the name of the music file to load.
     * @return the created GameMusic object.
     */
    public static GameMusic create(final String musicFile) {
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
}
