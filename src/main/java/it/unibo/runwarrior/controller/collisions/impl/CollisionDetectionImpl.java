package it.unibo.runwarrior.controller.collisions.impl;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.runwarrior.model.player.api.Character;
import it.unibo.runwarrior.view.GameMusic;
import it.unibo.runwarrior.model.player.impl.AbstractCharacterImpl;
import it.unibo.runwarrior.controller.GameLoopController;
import it.unibo.runwarrior.controller.SoundManager;
import it.unibo.runwarrior.controller.collisions.api.CollisionDetection;
import it.unibo.runwarrior.model.MapElement;

/**
 * Class that handles the collision between the player and the map tiles.
 */
public class CollisionDetectionImpl implements CollisionDetection {
    public static final int SEC_3 = 3000;
    public static final int FEET_HEAD_TOLL = 5;
    public static final String RIGHT = "right";
    public static final String LEFT = "left";
    public static final String UP = "up";
    public static final String DOWN = "down";
    private final int[][] map;
    private final List<MapElement> blocks;
    private final int tileSize;
    private final List<String> directions;
    private Rectangle playerArea;
    private final GameLoopController glc;
    private long hitWaitTime;
    private int gameOverY;
    private boolean end;
    //private final GameMusic sound;

    /**
     * Constructor of the collision detection. It's necessary to make reference of the specific classes.
     *
     * @param map current map
     * @param blocks list of the block types
     * @param tileSize size of the tile
     * @param glc game-loop controller
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CollisionDetectionImpl(final int[][] map, final List<MapElement> blocks, 
    final int tileSize, final GameLoopController glc) {
        this.map = Arrays.copyOf(map, map.length);
        this.blocks = blocks;
        this.tileSize = tileSize;
        this.glc = glc;
        this.playerArea = new Rectangle();
        this.directions = new ArrayList<>();
        //sound = SoundManager.create("hit.wav");
    }

    /**
     * {@inheritDoc}
     */
    @SuppressFBWarnings(
    value = "NS_DANGEROUS_NON_SHORT_CIRCUIT",
    justification = "evey touchSolid has to be checked to verify every point collision")
    @Override
    public String checkCollision(final Character player) {
        playerArea = player.getArea();
        String dir = "";
        this.directions.clear();
        if (touchSolid(playerArea.x + playerArea.width - FEET_HEAD_TOLL, playerArea.y, true, player) 
            | touchSolid(playerArea.x + playerArea.width, playerArea.y + playerArea.height / 2, true, player) 
            | touchSolid(playerArea.x + playerArea.width, playerArea.y + playerArea.height, true, player) 
            | touchSolid(playerArea.x + FEET_HEAD_TOLL, playerArea.y, true, player) 
            | touchSolid(playerArea.x, playerArea.y + playerArea.height / 2, true, player) 
            | touchSolid(playerArea.x, playerArea.y + playerArea.height, true, player)) {
                dir = directions.stream().filter(s -> RIGHT.equals(s) | LEFT.equals(s))
                        .distinct().findFirst().orElse("");
        }
        if (dir.isEmpty() && directions.contains(UP)) {
            dir = UP;
        }
        if (directions.contains(DOWN)) {
            dir = DOWN;
        }
        return dir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean touchSolid(final int x, final int y, final boolean checkDirections, final Character player) {
        gameOverY = y;
        int indexXtile = x / tileSize;
        int indexYtile = y / tileSize;
        int blockIndex = map[indexYtile][indexXtile];
        end = blocks.get(blockIndex).isPortal();
        if (blocks.get(blockIndex).isCollision() || y <= 0) {
            if (checkDirections) {
                this.directions.add(checkCollisionDirection(x, y, indexXtile, indexYtile, player));
            }
            if (!blocks.get(blockIndex).isHarmless() && System.currentTimeMillis() - hitWaitTime > SEC_3) {
                SoundManager.getAllSounds().get(1).play(false);
                hitWaitTime = System.currentTimeMillis();
                glc.getPowersHandler().losePower(false);
            }
            return true;
        } else if (!blocks.get(blockIndex).isPortal()) {
            for (int i = playerArea.width; i >= 0; i = i - playerArea.width) {
                indexXtile = (playerArea.x + i) / tileSize;
                indexYtile = playerArea.y / tileSize;
                blockIndex = map[indexYtile][indexXtile];
                if (blocks.get(blockIndex).isCollision() && checkDirections) {
                    final String tempDir = checkCollisionDirection(x, y, indexXtile, indexYtile, player);
                    if (RIGHT.equals(tempDir) || LEFT.equals(tempDir)) {
                        this.directions.add(tempDir);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String checkCollisionDirection(final int x, final int y, final int indexXtile, final int indexYtile, 
    final Character player) {
        String direction = "";
        final int tileX = indexXtile * tileSize;
        final int tileY = indexYtile * tileSize;
        final Rectangle tileRec = new Rectangle(tileX, tileY, tileSize, tileSize);
        if (y == tileRec.y && x >= tileRec.x && x <= tileRec.x + tileRec.width) {
            direction = UP;
        } else if (isInAir(player) && y == playerArea.y 
            && (tileRec.y + tileRec.height - y) < it.unibo.runwarrior.controller.player.impl.CharacterMovementHandlerImpl
            .SPEED_JUMP_UP && x >= tileRec.x && x <= tileRec.x + tileRec.width || y <= 0) {
            direction = DOWN;
        } else if (x - AbstractCharacterImpl.SPEED <= tileRec.x) {
            direction = RIGHT;
        } else if (x + AbstractCharacterImpl.SPEED >= tileRec.x + tileRec.width) {
            direction = LEFT;
        }
        return direction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInAir(final Character pl) {
        return !touchSolid(pl.getArea().x + FEET_HEAD_TOLL, pl.getArea().y + pl.getArea().height, false, pl) 
        && !touchSolid(pl.getArea().x + pl.getArea().width - FEET_HEAD_TOLL, pl.getArea().y + pl.getArea().height, 
        false, pl);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getHitWaitTime() {
        return this.hitWaitTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHitWaitTime(final long lastHit) {
        hitWaitTime = lastHit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean gameOver() {
        return gameOverY >= glc.getGlp().getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean win() {
        return this.end;
    }
}
