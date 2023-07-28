package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

import server.Server;

public class World {

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<Ball> balls = new ArrayList<>();
    public ArrayList<Goal> goals = new ArrayList<>();
    public String type;
    public double edgesStored = 0;
    public static double maxEdgesStored = 4;

    public World(String type) {
        this.type = type;
    }

    public void configure(int[] configuration) {
        Player.maxEdgeCount = configuration[1];
        World.maxEdgesStored = configuration[2];
    }

    /** Creates a World for a server */
    public World(int[] configuration) {
        this.type = "SERVER";
        configure(configuration);
        // Add 4 edges
        edges.add(new Edge(new Point(0, 0), new Point(WIDTH, 0)));
        edges.add(new Edge(new Point(WIDTH, 0), new Point(WIDTH, HEIGHT)));
        edges.add(new Edge(new Point(WIDTH, HEIGHT), new Point(0, HEIGHT)));
        edges.add(new Edge(new Point(0, HEIGHT), new Point(0, 0)));
        //
        /*
         * edges.add(new Edge(new Point(730, 300), new Point(1190, 300)));
         * edges.add(new Edge(new Point(1220, 330), new Point(1220, 750)));
         * edges.add(new Edge(new Point(1190, 780), new Point(730, 780)));
         * edges.add(new Edge(new Point(700, 750), new Point(700, 330)));
         */

        // Add 5 random edges
        /*
         * for (int i = 0; i < 1; i++) {
         * edges.add(new Edge(new Point((int) (Math.random() * WIDTH), (int)
         * (Math.random() * HEIGHT)),
         * new Point((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT))));
         * }
         */
        balls.add(new Ball(500, 300, new double[] { -2.5, -1.5 }));
        balls.add(new Ball(700, 800, new double[] { 2, 1.5 }));
        balls.add(new Ball(500, 500, new double[] { 2.5, -1 }));

        goals.add(new Goal(0, HEIGHT / 2 - 200, 20, HEIGHT / 2 + 200, Color.BLUE));
        goals.add(new Goal(WIDTH, HEIGHT / 2 - 200, WIDTH - 20, HEIGHT / 2 + 200, Color.RED));
    }

    public Ball getBall(String uuid) {
        for (Ball b : balls) {
            if (b.uuid.equals(uuid)) {
                return b;
            }
        }
        return null;
    }

    public Goal getGoal(String uuid) {
        for (Goal goal : goals) {
            if (goal.uuid.equals(uuid)) {
                return goal;
            }
        }
        return null;
    }

    public Edge getEdge(String uuid) {
        for (Edge e : edges) {
            if (e.uuid.equals(uuid)) {
                return e;
            }
        }
        return null;
    }

    public void drawThis(Graphics2D g2d) {
        ArrayList<Edge> tempEdges = new ArrayList<>(edges);
        for (Edge e : tempEdges) {
            e.drawThis(g2d);
        }
        ArrayList<Ball> tempBalls = new ArrayList<>(balls);
        for (Ball b : tempBalls) {
            b.drawThis(g2d);
        }
        for (Goal goal : goals) {
            goal.drawThis(g2d);
        }
        // Score
        int scoreX = 10;
        int scoreY = 20;
        for (Goal goal : goals) {
            g2d.setColor(goal.color);
            // Set Font Size
            g2d.setFont(g2d.getFont().deriveFont(20f));
            g2d.drawString(Integer.toString(goal.score), scoreX, scoreY);
            scoreX += 100;
        }
    }

    public void handleChange(String changeString) {
        if (type.equals("SERVER")) {
            Server.sendDataToAll(changeString);
        }
    }

    public void tick() {
        if (type.equals("SERVER")) {
            for (Ball b : balls) {
                b.tick();
            }
            handleChange(Ball.getMessages(false));
        } else {
            ArrayList<Ball> tempBalls = new ArrayList<>(balls);
            for (Ball b : tempBalls) {
                b.move();
            }
            edgesStored += 0.25 / Game.tick;
            if (edgesStored > maxEdgesStored) {
                edgesStored = maxEdgesStored;
            }
        }
    }

    public String getMessage(boolean all) {
        String result = "GameData%World%";
        for (Edge e : edges) {
            result += e.getMessage() + "%";
        }
        result += Ball.getMessages(all);
        for (Goal goal : goals) {
            result += goal.getMessage() + "%";
        }
        return result;
    }

}