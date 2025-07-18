package it.unibo.runwarrior.model.enemy;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import it.unibo.runwarrior.view.GameLoopPanel;
import it.unibo.runwarrior.controller.EnemyHandler;

public class Guard extends EnemyImpl {

    private BufferedImage rightGuard;
    private BufferedImage leftGuard;
    private BufferedImage rightGuardMoving;
    private BufferedImage leftGuardMoving;
    private BufferedImage rightGuardRunning;
    private BufferedImage leftGuardRunning;

    public Guard(int x, int y, final int width, final int height, final boolean solid,  
                final EnemyHandler handler, final GameLoopPanel glp) {
        super(x, y, width, height, solid, handler, glp);
        setVelocityX(1);
        try {
            rightGuard = ImageIO.read(getClass().getResourceAsStream("/Guardia/rightGuard.png"));
            leftGuard = ImageIO.read(getClass().getResourceAsStream("/Guardia/leftGuard.png"));
            rightGuardMoving = ImageIO.read(getClass().getResourceAsStream("/Guardia/rightGuardMoving.png"));
            leftGuardMoving = ImageIO.read(getClass().getResourceAsStream("/Guardia/leftGuardMoving.png"));
            rightGuardRunning = ImageIO.read(getClass().getResourceAsStream("/Guardia/rightRunningGuard.png"));
            leftGuardRunning = ImageIO.read(getClass().getResourceAsStream("/Guardia/leftRunningGuard.png"));
            image = rightGuard;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final void render(final Graphics g) {
        BufferedImage currentImage;
        if (velocityX == 0) {
            currentImage = image;
        } else if (velocityX > 0) {
            currentImage = step ? rightGuardMoving : rightGuardRunning;
            image = rightGuard;
        } else {
            currentImage = step ? leftGuardMoving : leftGuardRunning;
            image = leftGuard;
        }
        int shift = glp.getMapHandler().getShift();
        System.out.println("SNAKE XS: " + (x+shift)+ "X:"+ x);
        g.drawImage(currentImage, x + shift, y, width, height, null);
    }
}
