package server;

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import game.Ball;
import game.Edge;
import game.Goal;
import game.Player;
import game.World;
import util.AudioFilePlayer;

public class Client implements Runnable {

    public static Socket client;
    public static String lastInput;
    public static DataInputStream input;
    public static DataOutputStream output;
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
        /* System.out.println(message); */
        String[] args = message.split("%");
        if (args[0].equals("GameData") && args.length > 1) {
            int startIndex = 1;
            if (!args[1].equals("World"))
                startIndex = 2;
            for (int i = 1; i < args.length; i++) {
                String[] data = args[i].split(" ");
                if (data[0].equals("ball")) {
                    processBall(args[i]);
                } else if (data[0].equals("edge")) {
                    processEdge(args[i]);
                } else if (data[0].equals("removeEdge")) {
                    processRemoveEdge(args[i]);
                } else if (data[0].equals("goal")) {
                    processGoal(args[i]);
                } else if(data[0].equals("config")) {
                    processConfig(args[i]);
                }

            }
        }
    }

    public void processConfig(String message){
        String[] args = message.split(" ");
        int[] config = new int[args.length - 1];
        for(int i = 1; i < args.length; i++){
            config[i - 1] = Integer.parseInt(args[i]);
        }
        System.out.println("Config:");
        for(int i = 0; i < config.length; i++){
            System.out.println(config[i]);
        }
        System.out.println("Config end");
        Client.world.configure(config);
    }

    public void processBall(String message) {
        String[] args = message.split(" ");
        String uuid = args[1];
        double x = Double.parseDouble(args[2]);
        double y = Double.parseDouble(args[3]);
        double dx = Double.parseDouble(args[4]);
        double dy = Double.parseDouble(args[5]);
        Ball ball = Client.world.getBall(uuid);
        if (ball == null) {
            ball = new Ball(x, y, new double[] { dx, dy }, uuid);
            Client.world.balls.add(ball);
            System.out.println("New ball created");
            return;
        }
        ball.x = x;
        ball.y = y;
        if (ball.direction[0] != dx || ball.direction[1] != dy) {
            if (ball.directionChanged >= 2) {
                /* System.out.println("playsound"); */
                AudioFilePlayer.playCounter();

            }
            ball.directionChanged = 0;
        }
        ball.direction[0] = dx;
        ball.direction[1] = dy;
        /* System.out.println("Ball update"); */
    }

    public void processGoal(String message) {
        String[] args = message.split(" ");
        String uuid = args[1];
        int x = Integer.parseInt(args[2]);
        int y = Integer.parseInt(args[3]);
        int x2 = Integer.parseInt(args[4]);
        int y2 = Integer.parseInt(args[5]);
        Color color = new Color(Integer.parseInt(args[6]), true);
        int score = Integer.parseInt(args[7]);

        Goal goal = Client.world.getGoal(uuid);
        if (goal == null) {
            goal = new Goal(x, y, x2, y2, uuid, color);
            Client.world.goals.add(goal);
            System.out.println("New goal created");
            return;
        }
        goal.x = x;
        goal.y = y;
        goal.x2 = x2;
        goal.y2 = y2;
        goal.color = color;
        if(goal.score != score){
            System.out.println("Goal sound");
            AudioFilePlayer.playOctaveCounter();
        }
        goal.score = score;
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
        world.edges.add(new Edge(uuid, new Point(x, y), new Point(dx, dy), Player.getRandomColor()));
    }

    public void processRemoveEdge(String message) {
        String[] args = message.split(" ");
        System.out.println("Edge removal");
        System.out.println(args[1]);
        System.out.println(world.getEdge(args[1]));
        world.edges.remove(world.getEdge(args[1]));
    }

    public static void writeMessage(String message) {
        try {
            output.writeUTF(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
