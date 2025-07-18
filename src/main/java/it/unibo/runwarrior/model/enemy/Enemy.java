package it.unibo.runwarrior.model.enemy;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public interface Enemy {
    /**
     * @param g
     * Renders the current enemy on the screen using the provided graphics context.
     */
    public void render(Graphics g);

    /**
     * Handles the logic that should be executed when the entity dies.
     * This include playing an animation or removing the entity from the game. 
     */
    public void die();
    
    /**
     * Updates the state of the entity.
     * This include movement, animation and collision
     */
    public void update();

    /**
     * @param obstacles
     * Check whether the current enemy collides with any of the obstacles in the map
     */
    public void checkMapCollision(List<Rectangle> obstacles);

}
