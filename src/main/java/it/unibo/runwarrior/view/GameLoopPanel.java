package it.unibo.runwarrior.view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import it.unibo.runwarrior.model.Character;
import it.unibo.runwarrior.model.CharacterImpl;

public class GameLoopPanel extends JPanel implements Runnable{
    
    public static final int WIDTH = 960;
    public static final int HEIGHT = 672;
    public static final int MLD = 1000000000;
    public static final int FPS = 60;

    private Thread gameThread;
    public Character player;

    public GameLoopPanel(){
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        player = new CharacterImpl(this);
    }

    public void startGame(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run(){
        double timeFor1Frame = MLD/FPS; //16.666.666,67 in ns
        long lastTime = System.nanoTime();
        long currentTime;
        double waitingTime = 0;

        while(true){
            currentTime = System.nanoTime();
            waitingTime += (currentTime-lastTime);
            lastTime = currentTime;
            
            if(waitingTime >= timeFor1Frame){
                update();
                repaint();
                waitingTime  -= timeFor1Frame;
            }
        }
    }

    public void update(){
        player.update();
    }

    @Override
    protected void paintComponent(Graphics gr){
        super.paintComponent(gr);
        Graphics2D gr2 = (Graphics2D) gr;
        gr2.dispose();
    }
}
