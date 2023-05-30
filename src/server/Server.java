package server;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import game.Edge;
import game.Game;
import game.Player;
import game.World;

public class Server implements Runnable {

    public static ServerSocket server;
    public static int port = 8888;
    public static String dataBuffer = "";
    public static HashMap<String, Player> players = new HashMap<>();

    public static World world;

    public Server(int port) {
        try {
            server = new ServerSocket(port);
            world = new World("SERVER");
            Thread thread = new Thread(this);
            thread.start();
        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    @Override
    public void run() {
        while (Game.running) {
            try {
                server.setSoTimeout(100000);
                System.out.println("Waiting for client at " + server.getLocalPort());
                Socket client = server.accept();
                System.out.println("Got Connection");

                DataInputStream input = new DataInputStream(client.getInputStream());
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                System.out.println(client.getRemoteSocketAddress() + " connected.");
                String name = input.readUTF();
                Server.players.put(name, new Player(name, client, input, output));
                output.writeUTF("Welcome to the server, " + name + "!");
                writePlayersMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeGameData(String clientName) {
        DataOutputStream output = players.get(clientName).output;
        try {
            /* System.out.println(dataBuffer.get("" + dataBufferIndex)); */
            if(!dataBuffer.equals(""))
                output.writeUTF("GameData%" + dataBuffer);
        } catch (IOException e) {
            // catch IOException and SocketException
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("Client disconnected");
                players.get(clientName).disconnect();
            }
        }
    }

    public static void addData(String data) {
        dataBuffer += ("" + data);
        for (String clientName : players.keySet()) {
            writeGameData(clientName);
        }
        dataBuffer = "";
    }

    /**
     * Process all inputs from all clients
     */
    public static void processAll() {
        for (String key : players.keySet()) {
            DataInputStream input = players.get(key).input;
            DataOutputStream output = players.get(key).output;
            try {
                // Only process if there is data to process
                if (input.available() <= 0)
                    continue;
                String data = input.readUTF();
                String[] dataSplit = data.split("%");
                if (dataSplit[0].equals("RequestWorld")) {
                    output.writeUTF(world.getMessage(true));

                } else if (dataSplit[0].equals("AddEdge")) {
                    processAddEdge(players.get(key), dataSplit[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void process() {
        for (Player player : players.values()) {
            try {
                ;
                System.out.println(player.input.readUTF());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Server startServer(int port) {
        Server.port = port;
        Server s = new Server(port);
        return s;
    }

    public static void processAddEdge(Player player, String message) {
        String[] points = message.split(" ");
        Edge edge = new Edge(player, new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1])),
                new Point(Integer.parseInt(points[2]), Integer.parseInt(points[3])));
        System.out.println("Received edge");
        world.edges.add(edge);
        player.addEdge(edge);
        addData("GameData%" + edge.getMessage());
    }

    public static void removeEdge(Edge edge) {
        world.edges.remove(edge);
        addData("GameData%removeEdge " + edge.uuid);
    }

    public void writePlayersMessage(){
        String message = "GameData%";
        for(Player player : players.values()){
            message += player.getMessage() + " ";
        }
        addData(message);
    }

}
