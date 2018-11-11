/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package carsimulator.carui;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import carsimulator.Car;
import carsimulator.Gas;
import carsimulator.Location;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author justin
 */
public class Grid extends JFrame implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(Grid.class.getName());

    private BufferedImage img = null;
    private BufferedImage img2;
    private BufferedImage img2_rotated;
    private BufferedImage crashedText;
    private final int img2_width;
    private final int img2_height;
    
    private Car carModel;

    public Grid(Car carModel) throws IOException {
        this.carModel = carModel;
        
        try {
            this.img = ImageIO.read(getClass().getClassLoader().getResource("resources/map-small.png"));
            this.img2 = ImageIO.read(getClass().getClassLoader().getResource("resources/car-tiny.png"));
            this.crashedText = ImageIO.read(getClass().getClassLoader().getResource("resources/crashed-small.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
        this.img2_rotated = img2;
        // Declaring these here since they need to be final
        this.img2_width = img2.getWidth();
        this.img2_height = img2.getHeight();

        initComponents();
    }

    @Override
    public void run() {
        setSize(750, 750);
        setVisible(true);

        // TODO name these better.  I had to play a bit with these to get it to look right
        int magic_number1 = 1_000_000_000;
        int magic_number2 = 1_000_000;
        int crashCounter = 0;

        long lastLoopTime = System.nanoTime();
        long lastFpsTime = 0;
        long fps = 0;
        final int TARGET_FPS = 10;
        final long OPTIMAL_TIME = magic_number1 / TARGET_FPS;

        // keep looping round til the game ends
        while (true) {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            double delta = updateLength / ((double) OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= magic_number2) {
                //System.out.println("(FPS: " + fps + ")");
                lastFpsTime = 0;
                fps = 0;
            }
            
            // update the game logic
            doGameUpdates(delta);
            rotate(this.carModel.getDirection());
            
            if (carModel.isCrashed == true && crashCounter >= 50) {
                carModel.isCrashed = false;
                crashCounter = 0;
            }
            else if (carModel.isCrashed == true)
            {
                crashCounter++;
            }

            // draw everyting
            Graphics g = getGraphics();
            paint(g);

            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give 
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            try {
                Thread.sleep(((System.nanoTime() - lastLoopTime) + OPTIMAL_TIME) / magic_number2);
            } catch (Exception e) {
                // TODO handle the interrupt properly.
                LOGGER.log(Level.SEVERE, "Interrupt found: ", e);
            }
        }
    }

    public void doGameUpdates(double delta) {
//        x = x + 10;
//        y = y + 10;
    }

    public void rotate(double radians) {
        LOGGER.log(Level.INFO, "Rotating to {0} degrees", radians * 180 / Math.PI);
        AffineTransform tx = new AffineTransform();
        tx.rotate(radians + Math.PI/2, this.img2_width / 2, this.img2_height / 2);
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

        this.img2_rotated = op.filter(this.img2, null);
    }

    public void paint(Graphics g) {
        g.drawImage(this.img, 0, 0, this);
        g.drawImage(this.img2_rotated, this.carModel.getLocation().x, this.carModel.getLocation().y, this);
        
        if (carModel.isCrashed == true) {
            g.drawImage(this.crashedText, 25, 300, this);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
