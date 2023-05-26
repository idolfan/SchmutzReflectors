package server;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import game.Edge;
import game.Game;
import game.Player;
import game.World;

public class Server implements Runnable {

    public static ServerSocket server;
    public static int port = 8888;
    public static HashMap<String, Socket> clients = new HashMap<>();
    public static HashMap<String, DataInputStream> inputs = new HashMap<>();
    public static HashMap<String, DataOutputStream> outputs = new HashMap<>();
    public static Map<String, String> dataBuffer = new HashMap<String, String>();
    public static HashMap<String, Player> players = new HashMap<>();
    public static int dataBufferIndex = 0;

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
                DataInputStream input = new DataInputStream(client.getInputStream());
                String answer = input.readUTF();
                Server.clients.put(answer, client);
                Server.inputs.put(answer, input);
                Server.players.put(answer, new Player(answer));
                System.out.println(client.getRemoteSocketAddress() + " connected.");
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                Server.outputs.put(answer, output);
                output.writeUTF("Welcome to the server, " + answer + "!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeGameData(String clientName) {
        DataOutputStream output = outputs.get(clientName);
        try {
            /* System.out.println(dataBuffer.get("" + dataBufferIndex)); */
            if (dataBuffer.get("" + dataBufferIndex) != null && !dataBuffer.get("" + dataBufferIndex).equals(""))
                output.writeUTF("GameData%" + dataBufferIndex + "%" + dataBuffer.get("" + dataBufferIndex));
        } catch (IOException e) {
            // catch IOException and SocketException
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("Client disconnected");
                clients.remove(clientName);
                inputs.remove(clientName);
                outputs.remove(clientName);
                players.remove(clientName);
            }
        }
    }

    public static void addData(String data) {
        dataBufferIndex += 1;
        dataBuffer.put("" + (dataBufferIndex), data);
        for (String clientName : clients.keySet()) {
            writeGameData(clientName);
        }
    }

    /**
     * Process all inputs from all clients
     */
    public static void processAll() {
        for (String key : inputs.keySet()) {
            DataInputStream input = inputs.get(key);
            try {
                // Only process if there is data to process
                if (input.available() <= 0)
                    continue;
                String data = input.readUTF();
                String[] dataSplit = data.split("%");
                if (dataSplit[0].equals("RequestWorld")) {
                    outputs.get(key).writeUTF(world.getMessage(true));

                } else if (dataSplit[0].equals("AddEdge")) {
                    processAddEdge(players.get(key), dataSplit[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void process() {
        for (DataInputStream input : inputs.values()) {
            try {
                ;
                System.out.println(input.readUTF());
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
        Edge edge = new Edge(new Point(Integer.parseInt(points[0]), Integer.parseInt(points[1])),
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

}
