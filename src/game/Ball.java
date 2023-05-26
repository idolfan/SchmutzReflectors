package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;
import java.util.Vector;

import server.Server;

public class Ball {
    
    public static final double standardVelocity = 2;

    /** Position Vector */
    public double x;
    public double y;
    public double dx;
    public double dy;
    public double velocity;
    public Vector<Double> direction;
    public int radius = 15;
    public String uuid;
    public double[] historyX = new double[100];
    public double[] historyY = new double[100];
    public int historyPointer = 0;
    public Color color = Color.BLACK;
    public Color trailColor = new Color(0, 0, 0, 0.1f);

    public Ball(double x,double y, Vector<Double> direction) {
        this(x, y, direction, UUID.randomUUID().toString());
    }

    public Ball(double x,double y, Vector<Double> direction, String uuid) {
        // Generate uuid
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.velocity = standardVelocity;
        this.direction = direction;
        this.color = Player.getRandomColor();
        this.trailColor = new Color((color.getRed()/256f),(color.getGreen()/256f), (color.getBlue()/256f), 0.1f);
    }

    public void drawThis(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillOval((int)(x - radius),(int)(y - radius), radius * 2, radius * 2);
        /* System.out.println("Drawing ball at " + x + " " + y); */
        // Draw history
        g2d.setColor(trailColor);
        for (int i = 0; i < historyX.length; i += 2) {
            g2d.fillOval((int)(historyX[i] - radius),(int)(historyY[i] - radius), radius * 2, radius * 2);
        }
    }

    public void tick() {
        dx = x + direction.get(0) * velocity;
        dy = y + direction.get(1) * velocity;
        World world = Server.world;
        // check for collision with edges and change direction
        for (Edge e : world.edges) {
            if (e.doCollision(this)) {
                /* System.out.println("Collision"); */
                /* direction = e.getNewDirection(this); */
               /*  direction.set(0, 0.0);
                direction.set(1, 0.0); */
            }
        }
        // move the ball
        x = dx;
        y = dy;
    }

    public void updateDirection( Vector<Double> newDirection) {
        direction = newDirection;
    }

    public static String getMessages() {
        String message = "";
        for(Ball b : Server.world.balls) {
            message += "ball " + b.uuid + " " + b.x + " " + b.y + " " + b.direction.get(0) + " " + b.direction.get(1) + "%";
        }
        return message;
    }


}
