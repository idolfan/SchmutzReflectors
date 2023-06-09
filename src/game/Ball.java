package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;

import server.Server;

public class Ball {

    public static final double standardVelocity = 1;

    /** Position Vector */
    public double x;
    public double y;
    public double dx;
    public double dy;
    public double velocity;
    public double[] direction;
    public int radius = 15;
    public String uuid;
    public double[] historyX = new double[100];
    public double[] historyY = new double[100];
    public int historyPointer = 0;
    public Color color = Color.BLACK;
    public Color trailColor = new Color(0, 0, 0, 0.1f);
    public boolean updated = false;

    public Ball(double x, double y, double[] direction) {
        this(x, y, direction, UUID.randomUUID().toString());
    }

    public Ball(double x, double y, double[] direction, String uuid) {
        // Generate uuid
        this.uuid = uuid;
        this.x = x;
        this.y = y;
        this.velocity = standardVelocity;
        this.direction = direction;
        this.color = Player.getRandomColor();
        this.trailColor = new Color((color.getRed() / 256f), (color.getGreen() / 256f), (color.getBlue() / 256f), 0.1f);
    }

    public Ball(double x, double y, double[] direction, String uuid, Color color) {
        this(x, y, direction, uuid);
        this.color = color;
        this.trailColor = new Color((color.getRed() / 256f), (color.getGreen() / 256f), (color.getBlue() / 256f), 0.1f);
    }

    public void drawThis(Graphics2D g2d) {
        g2d.setColor(color);
        int x = (int) this.x;
        int y = (int) this.y;
        g2d.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
        /* System.out.println("Drawing ball at " + x + " " + y); */
        // Draw history
        g2d.setColor(trailColor);

        for (int i = 0; i < historyX.length; i += 2) {
            g2d.fillOval((int) (historyX[i] - radius), (int) (historyY[i] - radius), radius
                    * 2, radius * 2);
        }

    }

    public void tick() {
        dx = x + direction[0] * velocity;
        dy = y + direction[1] * velocity;
        World world = Server.world;
        // check for collision with edges and change direction
        boolean collision = false;
        for (Edge e : world.edges) {
            if (e.doCollision(this)) {
                this.updated = true;
                collision = true;
                break;
            }
        }
        // move the ball
            x = dx;
            y = dy;
    }

    public void updateDirection(double[] newDirection) {
        direction = newDirection;
    }

    public static String getMessages(boolean all) {
        String message = "";
        for (Ball b : Server.world.balls) {
            if (b.updated || all) {
                message += b.getMessage() + "%";
                b.updated = false;
            }
        }
        return message;
    }

    public String getMessage() {
        return "ball " + this.uuid + " " + this.x + " " + this.y + " " + this.direction[0] + " "
                + this.direction[1] + " " + this.color.getRGB();
    }

    /**
     * Should only be used by client.
     * Will not impact server, only for visual purposes.
     */
    public void move() {
        historyX[historyPointer] = x;
        historyY[historyPointer] = y;
        if (historyPointer < historyX.length - 1) {
            historyPointer++;
        } else {
            historyPointer = 0;
        }
        dx = x + direction[0] * velocity;
        dy = y + direction[1] * velocity;
        x = dx;
        y = dy;
    }

}
