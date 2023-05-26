package game;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Vector;

import server.Server;

public class World {

    public static final int WIDTH = 1920;
    public static final int HEIGHT = 1080;
    public ArrayList<Edge> edges = new ArrayList<>();
    public ArrayList<Ball> balls = new ArrayList<>();
    public String type;

    public World(String type) {
        this.type = type;
        if (type.equals("SERVER")) {
            // Add 4 edges
            edges.add(new Edge(new Point(0, 0), new Point(WIDTH, 0)));
            edges.add(new Edge(new Point(WIDTH, 0), new Point(WIDTH, HEIGHT)));
            edges.add(new Edge(new Point(WIDTH, HEIGHT), new Point(0, HEIGHT)));
            edges.add(new Edge(new Point(0, HEIGHT), new Point(0, 0)));
            //
            edges.add(new Edge(new Point(730, 300), new Point(1190, 300)));
            edges.add(new Edge(new Point(1220, 330), new Point(1220, 750)));
            edges.add(new Edge(new Point(1190, 780), new Point(730, 780)));
            edges.add(new Edge(new Point(700, 750), new Point(700, 330)));

            // Add 5 random edges
           /*  for (int i = 0; i < 1; i++) {
                edges.add(new Edge(new Point((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT)),
                        new Point((int) (Math.random() * WIDTH), (int) (Math.random() * HEIGHT))));
            } */
            balls.add(new Ball(500, 300, new Vector<Double>() {
                {
                    add(-2.5);
                    add(-1.5);
                }
            }));
            balls.add(new Ball(700, 800, new Vector<Double>() {
                {
                    add(2.0);
                    add(1.5);
                }
            }));
            balls.add(new Ball(500, 500, new Vector<Double>() {
                {
                    add(2.5);
                    add(-1.0);
                }
            }));
        }
        else {
        }
    }

    public Ball getBall(String uuid){
        for (Ball b : balls) {
            if (b.uuid.equals(uuid)) {
                return b;
            }
        }
        return null;
    }

    public Edge getEdge(String uuid){
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
    }

    public void handleChange(String changeString) {
        if (type.equals("SERVER")) {
            Server.addData(changeString);
        }
    }

    public void tick() {
        for (Ball b : balls) {
            b.tick();
        }
        handleChange(Ball.getMessages());
    }

    public String getMessage() {
        String result = "GameData%World%";
        for (Edge e : edges) {
            result += e.getMessage() + "%";
        }
        result += Ball.getMessages();
        return result;
    }
}