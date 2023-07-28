package server;

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
    public static Map<String, String> dataBuffer = new HashMap<String, String>();
    public static HashMap<String, Player> players = new HashMap<>();
    public static int[] config;

    public static World world;

    public Server(int[] configuration) {
        try {
            config = configuration;
            server = new ServerSocket(configuration[0]);
            world = new World(configuration);
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
                DataOutputStream output = new DataOutputStream(client.getOutputStream());
                System.out.println(client.getRemoteSocketAddress() + " connected.");
                String name = input.readUTF();
                Server.players.put(name, new Player(name, client, input, output));
                output.writeUTF("Welcome to the server, " + name + "!");
                WriteMessages.playersMessage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeDataToClient(String clientName, String data) {
        DataOutputStream output = players.get(clientName).output;
        try {
            /* System.out.println(dataBuffer.get("" + dataBufferIndex)); */
                output.writeUTF("GameData%" + data);
        } catch (IOException e) {
            // catch IOException and SocketException
            System.out.println(e.getMessage());
            if (e.getMessage().equals("Connection reset")) {
                System.out.println("Client disconnected");
                players.get(clientName).disconnect();
            }
        }
    }

    public static void sendDataToAll(String data) {
        for (String clientName : players.keySet()) {
            writeDataToClient(clientName, data);
        }
    }

    /** Process all inputs from all clients */
    public static void process() {
        for (Player player : players.values()) {
            try {
                if (player.input.available() <= 0)
                    continue;
                String data = player.input.readUTF();
                String[] dataSplit = data.split("%");
                if (dataSplit[0].equals("RequestWorld")) {
                    String message = world.getMessage(true) + "config ";
                    for (int i = 0; i < config.length; i++) {
                        message += config[i] + " ";
                    }
                    message += "%";
                    player.output.writeUTF(message);

                } else if (dataSplit[0].equals("AddEdge")) {
                    ProcessMessages.addEdge(player, dataSplit[1]);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Server startServer(int[] configuration) {
        Server.port = configuration[0];
        Server s = new Server(configuration);
        return s;
    }

    public static void removeEdge(Edge edge) {
        world.edges.remove(edge);
        sendDataToAll("GameData%removeEdge " + edge.uuid);
    }

}
