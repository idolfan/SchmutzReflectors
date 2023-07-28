package server;

import java.awt.Point;
import java.awt.Rectangle;

import game.Edge;
import game.Goal;
import game.Player;
import util.UtilMath;

public class ProcessMessages {

    public static void addEdge(Player player, String message) {
        System.out.println("Received edge");
        String[] points = message.split(" ");
        // Check if new edge would intersect with penalty boxes
        if (Edge.checkEdgeServer(Integer.parseInt(points[0]), Integer.parseInt(points[1]), Integer.parseInt(points[2]),
                Integer.parseInt(points[3]))) {
            System.out.println("Edge intersects with penalty box");
            return;
        }
        Edge edge = new Edge(new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1])),
                new Point(Integer.parseInt(points[2]), Integer.parseInt(points[3])));
        Server.world.edges.add(edge);
        player.addEdge(edge);
        Server.sendDataToAll("GameData%" + edge.getMessage());
    }
}
