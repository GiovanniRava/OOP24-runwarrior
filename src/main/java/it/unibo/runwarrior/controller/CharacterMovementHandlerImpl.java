package it.unibo.runwarrior.controller;

import it.unibo.runwarrior.view.PowerUpManager;
import it.unibo.runwarrior.controller.collisions.CoinDetectionImpl;
import it.unibo.runwarrior.controller.collisions.CollisionDetectionImpl;
import it.unibo.runwarrior.controller.collisions.KillDetectionImpl;
import it.unibo.runwarrior.controller.collisions.PowerUpDetectionImpl;
import it.unibo.runwarrior.model.player.Character;
import it.unibo.runwarrior.model.player.AbstractCharacterImpl;

/**
 * Class that handles player movement and his collisions.
 */
public class CharacterMovementHandlerImpl implements CharacterMovementHandler {
    private GameLoopController glp;
    private CharacterComand cmd;
    private Character player;
    private CollisionDetectionImpl collisionDetection;
    private PowerUpDetectionImpl pUpDetection;
    private KillDetectionImpl killDetection;
    private CoinDetectionImpl coinDetection;

    public static final int START_X = 96;
    private int startY;
    private int maxJump;
    private int midJump;
    protected int sizeCharacter;
    
    private static final int MIN_SCREEN_X = 0;//y IN CUI SI FERMA IL PLAYER NELLO SCHERMO
    private int maxScreenX;//x IN CUI SI FERMA IL PLAYER NELLO SCHERMO
    protected int playerX;//POSIZIONE ORIZZONTALE DEL PLAYER NELLA MAPPA
    protected int playerY;// * VERTICALE
    private int screenX;//POSIZIONE ORIZZONTALE DEL PLAYER NELLO SCHERMO
    private int endOfMap;
    private String collisionDir;
    private String tempDir;
    private boolean hitHead;
    private boolean jumpKill;
    private boolean descend;
    private boolean handleDoubleCollision;
    private boolean canAttack;

    public static final int SPEED_JUMP_UP = 12; 
    public static final int SPEED_JUMP_DOWN = 6;
    private boolean rightDirection = true;
    private int groundX;//variabile che permette lo scorrimento della mappa

    /**
     * Constructor of player movemnt that sets the following parametres, the collision with tiles, powerup and enemies
     * and the starting position.
     *
     * @param panel game-loop panel
     * @param player current player
     * @param cmd keyboard handler
     * @param collDet collision with map tiles
     * @param hM object that prints tiles
     * @param pMan object that prints powerups
     */
    public CharacterMovementHandlerImpl(final GameLoopController panel, final Character player, final CharacterComand cmd,
    final HandlerMapElement hM, final PowerUpManager pMan) {
        this.glp = panel;
        this.cmd = cmd;
        this.player = player;
        this.collisionDetection = new CollisionDetectionImpl(hM.getMap(), hM.getBlocks(), hM.getTileSize(), panel);
        this.pUpDetection = new PowerUpDetectionImpl(panel, pMan);
        this.killDetection = new KillDetectionImpl(panel, hM);
        this.coinDetection = new CoinDetectionImpl(hM.getTileSize(), glp.getCoinController(), glp.getScoreController());
        playerX = START_X;
        screenX = START_X;
        groundX = 0;
        sizeCharacter = hM.getTileSize() * 2;
        this.startY = hM.getFirstY() + AbstractCharacterImpl.TO_TOUCH_FLOOR;
        endOfMap = ((hM.getMap()[0].length - 1) * hM.getTileSize()) - hM.getTileSize();
        //impostare già anche ma e mid jump?
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStartY(final int y){
        playerY = y + AbstractCharacterImpl.TO_TOUCH_FLOOR;
        maxJump = playerY - (sizeCharacter*5/2);
        midJump = playerY - (sizeCharacter*3/2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocationAfterPowerup(final int x, final int y, final int realx, final int groundX, final long lastHit) {
        this.screenX = x;
        this.playerY = y;
        this.playerX = realx;
        maxJump = playerY - (sizeCharacter*5/2);
        midJump = playerY - (sizeCharacter*3/2);
        this.groundX = groundX;
        this.killDetection.setHitWaitTime(lastHit);
        this.collisionDetection.setHitWaitTime(lastHit);
        //System.out.println(playerX + " " + playerY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void movePlayer() {
        //System.out.println("-- " + playerY);
        maxScreenX = glp.getGlp().getWidth() / 2;
        player.updatePlayerPosition();
        collisionDir = collisionDetection.checkCollision(player);
        tempDir = pUpDetection.checkCollisionWithPowers(player, this);
        if (!tempDir.isEmpty()) {
            collisionDir = tempDir;
        }
        killDetection.checkCollisionWithEnemeies(player);
        coinDetection.controlCoinCollision(player);

        hitHead = "down".equals(collisionDir);
        if (hitHead) {
            cmd.setJump(false);
        }
        jump(cmd.isJumping(), maxJump);
        handleDoubleCollision = ("up".equals(collisionDir) || "down".equals(collisionDir)) && descend;
        if (jumpKill) {
            jumpAfterKill();
        }

        if (cmd.getRight() && !cmd.getLeft()) {
            rightDirection = true;
            if (!"right".equals(collisionDir) && !handleDoubleCollision && playerX < endOfMap) {
                playerX += AbstractCharacterImpl.SPEED;
                if (screenX < maxScreenX) {
                    screenX += AbstractCharacterImpl.SPEED;
                } else {
                    groundX -= AbstractCharacterImpl.SPEED;
                }
            }
        }
        if (cmd.getLeft() && !cmd.getRight()) {
            rightDirection = false;
            if (!"left".equals(collisionDir) && !handleDoubleCollision) {
                if (screenX > 0) {
                    playerX -= AbstractCharacterImpl.SPEED;
                }
                if (screenX > MIN_SCREEN_X) {
                    screenX -= AbstractCharacterImpl.SPEED;
                }
            }
        }
        canAttack = (!"right".equals(collisionDir) || !"left".equals(collisionDir));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jump (final boolean isJump, final int jumpHeight) {
        if (isJump && !descend) {
            if(playerY > jumpHeight){
                playerY -= SPEED_JUMP_UP;
            } else {
                playerY = jumpHeight;
                cmd.setJump(false);
                updateJumpVariable();
            }
        } else {
            if (collisionDetection.isInAir(player) && !jumpKill) {
                descend = true;
                playerY += SPEED_JUMP_DOWN;
                updateJumpVariable();
            }
            else if (!jumpKill) {
                descend = false;
                cmd.setDoubleJump(false);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setJumpKill() {
        this.jumpKill = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void jumpAfterKill(){
        if (playerY >= midJump && !hitHead) {
            cmd.setDoubleJump(true);
            playerY -= SPEED_JUMP_UP;
        } else {
            if(!hitHead){
                playerY = midJump;
            }
            jumpKill = false;
            cmd.setJump(false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateJumpVariable() {
        maxJump = startY - (sizeCharacter * 5 / 2) + (playerY - startY);
        midJump = startY - (sizeCharacter * 3 / 2) + (playerY - startY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canAttack() {
        return this.canAttack;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollisionDetectionImpl getCollisionDetection() {
        return this.collisionDetection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KillDetectionImpl getKillDetection() {
        return this.killDetection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getRightDirection() {
        return this.rightDirection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getGroundX() {
        return this.groundX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlX() {
        return this.playerX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getPlY() {
        return this.playerY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScX() {
        return this.screenX;
    }
}
