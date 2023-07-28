package game;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import server.Server;

public class Player {

    public String name;
    public static int maxEdgeCount = 4;
    private Edge[] edges = new Edge[maxEdgeCount];
    private int edgePointer = 0;
    public Color color = getRandomColor();
    // Serverside
    public Socket client;
    public DataInputStream input;
    public DataOutputStream output;

    public Player(String name) {
        this.name = name;
    }

    public Player(String name, Socket client, DataInputStream input, DataOutputStream output) {
        this.name = name;
        this.client = client;
        this.input = input;
        this.output = output;
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

    public void disconnect(){
        try {
            input.close();
            output.close();
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMessage(){
        return "player " + name + " " + color.getRGB();
    }

    public static Color getRandomColor(){
        int random = (int)(Math.random() * 8);
        // Return random predefined color
        switch(random){
            case 1:
                return Color.BLUE;
            case 2:
                return Color.CYAN;
            case 3:
                return Color.GREEN;
            case 4:
                return Color.MAGENTA;
            case 5:
                return Color.ORANGE;
            case 6:
                return Color.PINK;
            case 7:
                return Color.RED;
            case 8:
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
}
