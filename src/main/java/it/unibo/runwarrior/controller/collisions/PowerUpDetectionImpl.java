package it.unibo.runwarrior.controller.collisions;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import it.unibo.runwarrior.model.player.Character;
import it.unibo.runwarrior.model.player.AbstractCharacterImpl;
import it.unibo.runwarrior.controller.CharacterMovementHandler;
import it.unibo.runwarrior.controller.GameLoopController;
import it.unibo.runwarrior.model.PowerUp;
import it.unibo.runwarrior.view.PowerUpManager;

/**
 * Class that handles the collision between the player and the powerups.
 */
public class PowerUpDetectionImpl implements PowerUpDetection {
    private final GameLoopController glp;
    private final PowerUpManager powersManager;
    private List<PowerUp> powerCollision;
    private long hitWaitTime;
    private static final int TOLL = AbstractCharacterImpl.SPEED;
    private static final int WAIT = 200;

    /**
     * Constructor of powerup detection.
     *
     * @param glp game-loop panel
     * @param pMan obhect that prints powerups
     */
    public PowerUpDetectionImpl(final GameLoopController glp, final PowerUpManager pMan) {
        this.glp = glp;
        this.powersManager = pMan;
        this.powerCollision = new ArrayList<>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkCollisionWithPowers(final Character player, final CharacterMovementHandler move) {
        powerCollision.addAll(powersManager.getPowerUps());
        final Rectangle playerArea = player.getArea();
        String dir = "";
        for(final PowerUp pUp : powerCollision) {
            if(futureArea(playerArea).intersects(pUp.getTouchArea()) && !pUp.getTouchArea().isEmpty()) {
                if(isTouchingUp(playerArea, pUp.getTouchArea())){
                    dir = "up";
                    if(pUp.isEggOpen() && !pUp.isPowerTaken() && System.currentTimeMillis() - hitWaitTime > WAIT) {
                        glp.getPowersHandler().setPowers();
                        pUp.takePower();
                    }
                    else if(!pUp.isEggOpen()) {
                        System.out.println("boh");
                        move.setJumpKill();
                        pUp.openTheEgg();
                        hitWaitTime = System.currentTimeMillis();
                    }
                }
                else if(playerArea.x + playerArea.width >= pUp.getTouchArea().x && playerArea.x < pUp.getTouchArea().x) {
                    dir = "right";
                    if(pUp.isEggOpen() && !pUp.isPowerTaken()) {
                        glp.getPowersHandler().setPowers();
                        pUp.takePower();
                    }
                }
                else if(playerArea.x <= pUp.getTouchArea().x + pUp.getTouchArea().width) {
                    dir = "left";
                    if(pUp.isEggOpen() && !pUp.isPowerTaken()) {
                        glp.getPowersHandler().setPowers();
                        pUp.takePower();
                    }
                }
            }
        }
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle futureArea(final Rectangle r1) {
        final Rectangle futureArea = new Rectangle(r1);
        futureArea.translate(0, it.unibo.runwarrior.controller.CharacterMovementHandlerImpl.SPEED_JUMP_DOWN);
        return futureArea;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTouchingUp(final Rectangle playerArea, final Rectangle pUpArea){
        return playerArea.y + playerArea.height <= pUpArea.y && 
        ((playerArea.x + TOLL >= pUpArea.x && playerArea.x + TOLL <= pUpArea.x + pUpArea.width) ||
        (playerArea.x + playerArea.width - TOLL >= pUpArea.x && playerArea.x + playerArea.width - TOLL <= pUpArea.x + pUpArea.width));
    }
}
