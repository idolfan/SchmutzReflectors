package game;

import java.awt.Color;

import server.Server;

public class Player {

    public String name;
    public int maxEdgeCount = 4;
    private Edge[] edges = new Edge[maxEdgeCount];
    private int edgePointer = 0;
    public Color color = getRandomColor();

    public Player(String name) {
        this.name = name;
    }

    public void addEdge(Edge edge) {
        if (edges[edgePointer] != null)
            removeEdge(edges[edgePointer]);
        edges[edgePointer] = edge;
        if (edgePointer < maxEdgeCount - 1)
            edgePointer++;
        else
            edgePointer = 0;
    }

    public void removeEdge(Edge edge) {
        for (int i = 0; i < edges.length; i++) {
            if (edges[i] == edge) {
                Server.removeEdge(edge);
                edges[i] = null;
                return;
            }
        }
        System.err.println("Edge not found");
    }

    public static Color getRandomColor(){
        int random = (int)(Math.random() * 11);
        // Return random predefined color
        switch(random){
            case 0:
                return Color.BLACK;
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.DARK_GRAY;
            case 4:
                return Color.GRAY;
            case 5:
                return Color.GREEN;
            case 6:
                return Color.LIGHT_GRAY;
            case 7:
                return Color.MAGENTA;
            case 8:
                return Color.ORANGE;
            case 9:
                return Color.PINK;
            case 10:
                return Color.RED;
            case 11:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
}
