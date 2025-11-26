package it.unibo.runwarrior.controller.player.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.runwarrior.controller.SoundManager;
import it.unibo.runwarrior.controller.player.CharacterComand;
import it.unibo.runwarrior.controller.player.api.CharacterAnimationHandler;
import it.unibo.runwarrior.controller.player.api.CharacterMovementHandler;
import it.unibo.runwarrior.model.player.PlayerFrame;
import it.unibo.runwarrior.model.player.StickWizard;
import it.unibo.runwarrior.model.player.SwordWarrior;
import it.unibo.runwarrior.model.player.api.Character;

/**
 * Class that handle player frames changing.
 */
public class CharacterAnimationHandlerImpl implements CharacterAnimationHandler {
    private static final int ZERO = 0;
    private static final int ONE = 1;
    private static final int TWO = 2;
    private static final int THREE = 3;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;
    private static final int SEVEN = 7;
    private static final int EIGHT = 8;
    private static final int NINE = 9;
    private static final int TEN = 10;
    private static final int ELEVEN = 11;
    private static final int TIME_TO_CHANGE = 8;
    private static final int LIMIT_ATTACK = 60;
    private final CharacterComand cmd;
    private final CharacterMovementHandler movement;
    //private final GameMusic sound;
    private int changeFrame;
    private boolean crossWalk;
    private int useAttackMoving;
    private PlayerFrame playerFrame = PlayerFrame.STOP_FRAME;
    private BufferedImage right0;
    private BufferedImage right1;
    private BufferedImage right2;
    private BufferedImage left0;
    private BufferedImage left1;
    private BufferedImage left2;
    private BufferedImage jumpR;
    private BufferedImage jumpL;
    private BufferedImage attackR;
    private BufferedImage attackL;
    private BufferedImage tipR;
    private BufferedImage tipL;

    /**
     * Constructor of the player animation handler that sets current player images. Linked with keyboard.
     *
     * @param cmd keyboard handler
     * @param move player movement handler
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CharacterAnimationHandlerImpl(final CharacterComand cmd, final CharacterMovementHandler move) {
        this.cmd = cmd;
        this.movement = move;
        //this.sound = SoundManager.create("sword.wav");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setImages(final BufferedImage... im) {
        right0 = im[ZERO];
        right1 = im[ONE];
        right2 = im[TWO];
        left0 = im[THREE];
        left1 = im[FOUR];
        left2 = im[FIVE];
        jumpR = im[SIX];
        jumpL = im[SEVEN];
        attackR = im[EIGHT];
        attackL = im[NINE];
        tipR = im[TEN];
        tipL = im[ELEVEN];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void frameChanger() {
        if (!cmd.isStop() && !cmd.isJumping()) {
            changeFrame++;
            if (changeFrame > TIME_TO_CHANGE) {
                switch (playerFrame) {
                    case STOP_FRAME, ATTACK_FRAME, JUMP_FRAME -> {
                        playerFrame = crossWalk ? PlayerFrame.GO_FRAME1 : PlayerFrame.GO_FRAME2;
                        crossWalk = !crossWalk;
                    }
                    case GO_FRAME1, GO_FRAME2 -> playerFrame = PlayerFrame.STOP_FRAME;
                }
                changeFrame = 0;
            }
            useAttackMoving++;
            if (cmd.isAttacking() && movement.canAttack() && useAttackMoving > LIMIT_ATTACK) {
                playerFrame = PlayerFrame.ATTACK_FRAME;
                useAttackMoving = 0;
                if (hasSword(movement.getPlayer())) {
                    SoundManager.getAllSounds().get(4).play(false);
                }
            }
        } else if (cmd.isJumping()) {
            playerFrame = PlayerFrame.JUMP_FRAME;
            if (cmd.isAttacking() && movement.canAttack()) {
                playerFrame = PlayerFrame.ATTACK_FRAME;
                if (hasSword(movement.getPlayer())) {
                    SoundManager.getAllSounds().get(4).play(false);
                }
            }
        } else if (cmd.isAttacking() && movement.canAttack()) {
            playerFrame = PlayerFrame.ATTACK_FRAME;
            if (hasSword(movement.getPlayer())) {
                SoundManager.getAllSounds().get(4).play(false);
            }
        } else {
            playerFrame = PlayerFrame.STOP_FRAME;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage imagePlayer(final boolean rightDirection) {
        BufferedImage im = null;
        if (rightDirection) {
            switch (playerFrame) {
                case STOP_FRAME -> im = right0;
                case GO_FRAME1 -> im = right1;
                case GO_FRAME2 -> im = right2;
                case JUMP_FRAME -> im = jumpR;
                case ATTACK_FRAME -> {
                    im = attackR;
                }
            }
        } else {
            switch (playerFrame) {
                case STOP_FRAME -> im = left0;
                case GO_FRAME1 -> im = left1;
                case GO_FRAME2 -> im = left2;
                case JUMP_FRAME -> im = jumpL;
                case ATTACK_FRAME -> {
                    im = attackL;
                }
            }
        }
        return im;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PlayerFrame getFrame() {
        return this.playerFrame;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAttacking() {
        return playerFrame == PlayerFrame.ATTACK_FRAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BufferedImage getTip(final boolean rightDirection) {
        if (playerFrame == PlayerFrame.ATTACK_FRAME && rightDirection) {
            return copy(tipR);
        } else {
            return copy(tipL);
        }
    }

    /**
     * Create a copy of the image in order to not modify it.
     *
     * @param im original image
     * @return copy of the original
     */
    private BufferedImage copy(final BufferedImage im) {
        if (im != null) {
            final BufferedImage copy = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
            final Graphics2D g = copy.createGraphics();
            g.drawImage(im, 0, 0, null);
            return copy;
        }
        return null;
    }

    /**
     * Controls if the player has a weapon.
     *
     * @param pl current player
     * @return true if the player has a weapon
     */
    private boolean hasSword(final Character pl) {
        return pl.getClass().equals(SwordWarrior.class) || pl.getClass().equals(StickWizard.class);
    }
}
