package server;

import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

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

    public static void send(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addEdge(int[] p1, int[] p2) {
        send("AddEdge%" + p1[0] + " " + p1[1] + " " + p2[0] + " " + p2[1]);
    }

    @Override
    public void run() {
        try {

            output = new DataOutputStream(client.getOutputStream());
            output.writeUTF("" + client.getLocalSocketAddress());

            input = new DataInputStream(client.getInputStream());
            /* System.out.println(input.readUTF()); */

            writeMessage("RequestWorld");

            while (true) {
                String message = input.readUTF();
                /* System.out.println("Read Input"); */
                process(message);

            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    public void process(String message) {
        /* System.out.println(message); */
        if (message.startsWith("GameData%")) {
            String[] args = message.split("%");
            if (/* args[1].equals("" + (currentBufferIndex + 1)) */true) {
                if (!args[1].equals("World"))
                    currentBufferIndex = Integer.parseInt(args[1]);
                /* System.out.println(args[1]); */
                for (int i = 2; i < args.length; i++) {
                    ;
                    String[] data = args[i].split(" ");
                    if (data[0].equals("ball")) {
                        processBall(args[i]);
                    } else if (data[0].equals("edge")) {
                        processEdge(args[i]);
                    } else if (data[0].equals("removeEdge")) {
                        processRemoveEdge(args[i]);
                    }

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
        // Add old position to history at 0 and move all other positions up via Array
        // functions
        Ball ball = Client.world.getBall(uuid);
        if (ball == null) {
            ball = new Ball(x, y, new Vector<Double>() {
                {
                    add(dx);
                    add(dy);
                }
            }, uuid);
            Client.world.balls.add(ball);
            return;
        }
        ball.historyX[ball.historyPointer] = ball.x;
        ball.historyY[ball.historyPointer] = ball.y;
        if (ball.historyPointer < ball.historyX.length - 1) {
            ball.historyPointer++;
        } else {
            ball.historyPointer = 0;
        }
        ball.x = x;
        ball.y = y;
        ball.direction.set(0, dx);
        ball.direction.set(1, dy);
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
        world.edges.add(new Edge(uuid,new Point(x, y), new Point(dx, dy), Player.getRandomColor()));
    }

    public void processRemoveEdge(String message) {
        String[] args = message.split(" ");
        System.out.println("Edge removal");
        System.out.println(args[1]);
        System.out.println(world.getEdge(args[1]));
        world.edges.remove(world.getEdge(args[1]));
    }

    public void writeMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
