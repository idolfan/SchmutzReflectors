package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.UUID;

import server.Server;
import util.AudioFilePlayer;

public class Goal {
    
    public int x;
    public int y;
    public int x2;
    public int y2;
    public String uuid;
    public Color color;
    public int score = 0;
    private boolean updated;
    public int penaltyBoxX;
    public int penaltyBoxY;
    public int penaltyBoxX2;
    public int penaltyBoxY2;


    public Goal(int x, int y, int x2, int y2, Color color) {
        this(x,y,x2,y2,UUID.randomUUID().toString(), color);
    }

    public Goal(int x, int y, int x2,int y2,String uuid, Color color){
        this.x = x > x2 ? x2 : x;
        this.y = y > y2 ? y2 : y;
        this.x2 = x > x2 ? x : x2;
        this.y2 = y > y2 ? y : y2;
        this.uuid = uuid;
        this.color = new Color((color.getRed() / 256f), (color.getGreen() / 256f), (color.getBlue() / 256f), 0.5f);
        this.penaltyBoxX = x - 200;
        this.penaltyBoxY = y - 200;
        this.penaltyBoxX2 = x2 + 200;
        this.penaltyBoxY2 = y2 + 200;

    }

    public boolean isInside(int x, int y) {
        return (x >= this.x && x <= this.x2 && y >= this.y && y <= this.y2);
    }

    public static void checkForGoal(Ball ball) {
        for (Goal goal : Server.world.goals) {
            if (goal.isInside((int) ball.x, (int) ball.y)) {
                System.out.println("Goal!");
                ball.x = Game.WIDTH / 2;
                ball.y = Game.HEIGHT / 2;
                ball.direction = new double[] { 0.5*(Math.random() - 0.5), Math.random() < 0.5 ? -1 : 1 };
                ball.updated = true;
                goal.score++;
                goal.updated = true;
                Server.sendDataToAll("GameData%" + goal.getMessage());
            }
        }
    }

    public void drawThis(Graphics2D g2d) {
        g2d.setColor(color);
        g2d.fillRect(x, y, x2 - x, y2 - y);
        // Penalty Box
        g2d.setColor(new Color((color.getRed() / 256f), (color.getGreen() / 256f), (color.getBlue() / 256f), 0.1f));
        g2d.fillRect(penaltyBoxX, penaltyBoxY, penaltyBoxX2 - penaltyBoxX, penaltyBoxY2 - penaltyBoxY);
    }

    public String getMessage() {
        return "goal " + uuid + " " + x + " " + y + " " + x2 + " " + y2 + " " + color.getRGB() + " " + score;
    }

}
