package views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import basis.KeyHandler;
import basis.MouseMotionListener;
import display.View;
import game.Edge;
import game.Game;
import game.Player;
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
        // Edge preview
        if (firstPoint != null && secondPoint != null
                && Edge.checkEdgeClient(firstPoint[0], firstPoint[1], secondPoint[0], secondPoint[1]))
            g2d.setColor(Color.RED);
        else if (world.edgesStored < 1)
            g2d.setColor(Color.BLUE);
        else
            g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        if (firstPoint != null && secondPoint != null)
            g2d.drawLine(firstPoint[0], firstPoint[1], secondPoint[0], secondPoint[1]);
        // Edges available
        int length = 500;
        int height = 50;
        // Rising green bar
        g2d.setColor(new Color(0.16f, 1f, 0.16f, 0.3f));
        g2d.fillRect(0, Game.HEIGHT - 10 - height, (int) (world.edgesStored / World.maxEdgesStored * length),
                Game.HEIGHT - 10);
        // Edge markers
        g2d.setColor(new Color(0, 0, 0, 0.3f));
        for (int i = 0; i < World.maxEdgesStored; i++) {
            int linePos = (int) ((i + 1) * ((double) length / World.maxEdgesStored));
            g2d.fillRect(linePos - 5, Game.HEIGHT - 10 - height, 10, height);
        }
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
            /* System.out.println("Canceling edge"); */
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
            /* System.out.println("Selecting.."); */
            placing = true;
            firstPoint = new int[] { MouseMotionListener.mouseX, MouseMotionListener.mouseY };
        } else if (!selecting && placing) {
            placing = false;
            System.out.println("Trying to add edge");
            secondPoint = new int[] { MouseMotionListener.mouseX, MouseMotionListener.mouseY };
            if (firstPoint[0] == secondPoint[0] && firstPoint[1] == secondPoint[1]) {
                System.out.println("Not adding edge, points are the same");
            } else if (world.edgesStored < 1) {
                System.out.println("Not adding edge, no edges left");

            } else if (Edge.checkEdgeClient(firstPoint[0], firstPoint[1], secondPoint[0], secondPoint[1])) {
                System.out.println("Not adding edge, illegal placement");
            } else {
                Client.writeAddEdge(firstPoint, secondPoint);
                world.edgesStored--;
            }
            firstPoint = null;
            secondPoint = null;
        }
    }
}
