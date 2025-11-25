package it.unibo.runwarrior.view;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Class used to create and play the music of the game.
 */
public class GameMusic {
    public static final Logger LOGGER = Logger.getLogger(GameMusic.class.getName());
    private Clip clip;

    /**
     * Constructor of the music during the game.
     * It takes the music file, creates the audio stream, creates and open the clip and play it once or endlessly.
     *
     * @param musicFile music file wav
     */
    public GameMusic(final String musicFile) {
        try {
            final URL musicURL = GameMusic.class.getResource("/Music/" + musicFile);
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicURL);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            LOGGER.log(Level.SEVERE, "Cannot load or open audio files");
        }
    }

    /**
     * Play the sound chosen.
     *
     * @param loop boolean to play music costantly if it's true
     */
    public void play(final boolean loop) {
        stop();
        clip.setFramePosition(0);
        if (loop) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            clip.start();
        }
    }

    /**
     * Stop the music.
     */
    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }

    /**
     * Close the current istance of the clip
     */
    public void close() {
        if (this.clip != null) {
            this.clip.close();
        }
    }
}
