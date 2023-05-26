package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import basis.KeyHandler;
import basis.MouseMotionListener;
import display.View;
import game.Game;
import game.Settings;
import game.World;
import server.Client;

public class ViewWorld extends View {

    public World world;
    public int[] firstPoint;
    public int[] secondPoint;
    public boolean placing;

    public ViewWorld(View parrent, Settings settings, World world) {
        super(parrent, settings);
        this.world = world;
    }

    @Override
    public void drawThis(Graphics g) {
        world.drawThis((Graphics2D) g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke(2));
        if (firstPoint != null && secondPoint != null)
            g2d.drawLine(firstPoint[0], firstPoint[1], secondPoint[0], secondPoint[1]);
    }

    @Override
    public void handleThisInputs() {
        Settings settings = Game.renderer.getSettings("world");

        /*
         * if (KeyHandler.consumeKey(settings.get("selectFirstPoint"))) {
         * firstPoint = new int[] { MouseMotionListener.mouseX,
         * MouseMotionListener.mouseY };
         * } else if(KeyHandler.consumeKey(settings.get("selectSecondPoint"))){
         * secondPoint = new int[] { MouseMotionListener.mouseX,
         * MouseMotionListener.mouseY };
         * }
         * 
         * if (firstPoint != null && secondPoint != null) {
         * System.out.println("Trying to add edge");
         * Client.addEdge(firstPoint, secondPoint);
         * firstPoint = null;
         * secondPoint = null;
         * }
         */

        if (KeyHandler.consumeKey(settings.get("cancelSelect"))) {
            System.out.println("Canceling edge");
            KeyHandler.consumeKey(settings.get("select"));
            firstPoint = null;
            secondPoint = null;
            placing = false;
        }
        boolean selecting = KeyHandler.useKeyForTick(settings.get("select"));
        if (placing) {
            secondPoint = new int[] { MouseMotionListener.mouseX, MouseMotionListener.mouseY };
        }
        if (selecting && !placing) {
            System.out.println("Selecting..");
            placing = true;
            firstPoint = new int[] { MouseMotionListener.mouseX, MouseMotionListener.mouseY };
        } else if (!selecting && placing) {
            placing = false;
            System.out.println("Trying to add edge");
            secondPoint = new int[] { MouseMotionListener.mouseX, MouseMotionListener.mouseY };
            if (firstPoint[0] == secondPoint[0] && firstPoint[1] == secondPoint[1]) {
                System.out.println("Not adding edge, points are the same");
                return;
            }
            Client.writeAddEdge(firstPoint, secondPoint);
            firstPoint = null;
            secondPoint = null;
        }
    }
}
