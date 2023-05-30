package server;

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import game.Ball;
import game.Edge;
import game.Player;
import game.World;

public class Client implements Runnable {

    public static Socket client;
    public static String lastInput;
    public static DataInputStream input;
    public static DataOutputStream output;
    public static int currentBufferIndex = 0;
    public static World world;

    public Client(String ip, int port, World world) {
        Client.world = world;
        try {
            client = new Socket(ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Thread thread = new Thread(this);
        thread.start();

    }

    public static Client startClient(String ip, int port) {
        Client.world = new World("CLIENT");
        Client client = new Client(ip, port, world);
        return client;
    }

    public static void writeAddEdge(int[] p1, int[] p2) {
        writeMessage("AddEdge%" + p1[0] + " " + p1[1] + " " + p2[0] + " " + p2[1]);
    }

    @Override
    public void run() {
        try {

            output = new DataOutputStream(client.getOutputStream());
            output.writeUTF("" + client.getLocalSocketAddress());

            input = new DataInputStream(client.getInputStream());

            writeMessage("RequestWorld");

            while (true) {
                String message = input.readUTF();
                process(message);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void process(String message) {
        System.out.println(message);
        String[] args = message.split("%");
        if (args[0].equals("GameData")) {
            /* if (!args[1].equals("World")) */
            for (int i = 1; i < args.length; i++) {
                ;
                String[] data = args[i].split(" ");
                if (data[0].equals("ball")) {
                    processBall(args[i]);
                } else if (data[0].equals("edge")) {
                    processEdge(args[i]);
                } else if (data[0].equals("removeEdge")) {
                    processRemoveEdge(args[i]);
                } else if (data[0].equals("player")) {
                    processPlayer(args[i]);
                } else if (data[0].equals("removePlayer")) {
                    processRemovePlayer(args[i]);
                }

            }
        }
    }

    public void processBall(String message) {
        String[] args = message.split(" ");
        String uuid = args[1];
        double x = Double.parseDouble(args[2]);
        double y = Double.parseDouble(args[3]);
        double dx = Double.parseDouble(args[4]);
        double dy = Double.parseDouble(args[5]);
        Color color = Color.decode(args[6]);
        Ball ball = Client.world.getBall(uuid);
        if (ball == null) {
            ball = new Ball(x, y, new double[] { dx, dy }, uuid);
            Client.world.balls.add(ball);
            System.out.println("New ball created");
            return;
        }
        ball.x = x;
        ball.y = y;
        ball.direction[0] = dx;
        ball.direction[1] = dy;
        /* System.out.println("Ball update"); */
    }

    public void processEdge(String message) {
        String[] args = message.split(" ");
        System.out.println("Edge creation");
        System.out.println(message);
        String uuid = args[1];
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int dx = Integer.parseInt(args[4]);
        int dy = Integer.parseInt(args[5]);
            Player player = null;
        if(args.length > 6)
            player = world.getPlayer(args[6]);
        world.edges.add(new Edge(uuid, player, new Point(x, y), new Point(dx, dy)));
    }

    public void processRemoveEdge(String message) {
        String[] args = message.split(" ");
        System.out.println("Edge removal");
        System.out.println(args[1]);
        System.out.println(world.getEdge(args[1]));
        world.edges.remove(world.getEdge(args[1]));
    }

    public void processPlayer(String message){
        String[] args = message.split(" ");
        String name = args[1];
        Color color = Color.decode(args[2]);
        Player player = world.getPlayer(name);
        if(player == null){
            player = new Player(name, color);
            world.players.add(player);
            System.out.println("New player created. Color: " + color);
            return;
        }
        player.color = color;
    }

    public void processRemovePlayer(String message){
        String[] args = message.split(" ");
        System.out.println("Player removal");
        System.out.println(args[1]);
        System.out.println(world.getPlayer(args[1]));
        world.players.remove(world.getPlayer(args[1]));
    }

    public static void writeMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
