package it.unibo.runwarrior.model;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;

import it.unibo.runwarrior.controller.CharacterAnimationHandler;
import it.unibo.runwarrior.controller.CharacterComand;
import it.unibo.runwarrior.controller.CharacterMovementHandler;
import it.unibo.runwarrior.controller.CollisionDetection;
import it.unibo.runwarrior.controller.HandlerMapElement;
import it.unibo.runwarrior.view.GameLoopPanel;

public abstract class CharacterImpl implements Character{
    protected int sizeCharacter;
    protected int toTouchFloor = 2;
    protected Rectangle collisionArea;
    protected boolean rightDirection;
    private int speed = 5;

    protected BufferedImage right0, right1, right2, left0, left1, left2, attackR, attackL, tipR, tipL;

    protected CharacterComand cmd;
    protected CharacterAnimationHandler animation;
    protected CharacterMovementHandler movement;
    private List<Rectangle> enemies; // da mettere nel KillDetection

    public CharacterImpl(GameLoopPanel panel, CharacterComand commands, CollisionDetection collision, HandlerMapElement mapHandler){
        this.cmd = commands;
        playerImage();
        this.animation = new CharacterAnimationHandler(commands, right0, right1, right2, left0, left1, left2, attackR, attackL, tipR, tipL);
        setStartY(mapHandler.getFirstY(), mapHandler.getTileSize());
        this.movement = new CharacterMovementHandler(panel, this, commands, collision, mapHandler);
        collisionArea = new Rectangle(movement.getPlX()+(sizeCharacter/4), movement.getPlY()+(sizeCharacter/4),
                                        sizeCharacter/2, sizeCharacter-(sizeCharacter/4)-toTouchFloor);
    }

    private void setStartY(int y, int tileSize){
        sizeCharacter = tileSize*2;
    }

    @Override
    public void update() {
        this.rightDirection = movement.getRightDirection();
        movement.movePlayer();
        System.out.println((collisionArea.x + collisionArea.width));
        animation.frameChanger();
    }

    public void updatePlayerPosition() {
        collisionArea.setLocation(movement.getPlX() + (sizeCharacter/4), movement.getPlY() + (sizeCharacter/4));
        updateAttackCollision();
    }

    public abstract void updateAttackCollision();

    @Override
    public void drawPlayer(Graphics2D gr2) {
        BufferedImage im = null;
        BufferedImage tip = null;
        im = animation.imagePlayer(rightDirection);
        tip = animation.getTip(rightDirection);
        gr2.drawImage(tip, movement.getScX() + sizeCharacter, movement.getPlY(), sizeCharacter, sizeCharacter, null);
        gr2.drawImage(im, movement.getScX(), movement.getPlY(), sizeCharacter, sizeCharacter, null);
    }

    public void drawRectangle(Graphics2D gr){
        gr.drawRect(collisionArea.x, collisionArea.y, collisionArea.width, collisionArea.height);//si sposta in avanti perchè segue playerX non screenX
    }

    @Override
    public abstract void playerImage();

    @Override
    // public void setLocationAfterPowerup(int x, int y, int realx) {
    //     this.playerY = y;
    //     this.playerX = realx;
    //     this.screenX = x;
    // }

    public Rectangle getArea(){
        return collisionArea;
    }

    public int getSpeed(){
        return speed;
    }
}
