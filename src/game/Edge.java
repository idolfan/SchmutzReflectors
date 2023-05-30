package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.UUID;

import util.UtilMath;

public class Edge {

    public Point[] points = new Point[2];
    public Color color = Color.RED;
    public int width = 4;
    public String uuid;
    public Player owner;

    public Edge(String uuid, Player owner, Point p1, Point p2) {
        points[0] = p1;
        points[1] = p2;
        this.uuid = uuid;
        this.owner = owner;
    }

    public Edge(Player owner, Point p1, Point p2) {
        this(UUID.randomUUID().toString(), owner, p1, p2);
        if(owner == null)
            this.color = Player.getRandomColor();
    }

    public void drawThis(Graphics2D g) {
        g.setColor(owner != null ? owner.color : color);
        g.setStroke(new BasicStroke(width));
        g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
    }

    public boolean doCollision(Ball ball) {
        double x1 = this.points[0].x;
        double y1 = this.points[0].y;
        double x2 = this.points[1].x;
        double y2 = this.points[1].y;
        double cx = ball.dx;
        double cy = ball.dy;
        double r = ball.radius;
        double[] edgeVector = new double[] { x2 - x1, y2 - y1 };
        double edgeVectorLengthSquared = edgeVector[0] * edgeVector[0] + edgeVector[1] * edgeVector[1];
        double[] circleVector = new double[] { cx - x1, cy - y1 };
        double projectionFactor = UtilMath.dotProduct(circleVector, edgeVector) / edgeVectorLengthSquared;
        double projectionX = x1 + projectionFactor * edgeVector[0];
        double projectionY = y1 + projectionFactor * edgeVector[1];
        double distance = Math.sqrt((cx - projectionX) * (cx - projectionX) + (cy - projectionY) * (cy - projectionY));
        if (projectionFactor < 0 || projectionFactor > 1) {
            return false;
        }
        if (distance > r) {
            return false;
        }

        double[] newDirection = new double[2];
        double[] normal = new double[] { edgeVector[1], -edgeVector[0] };
        double[] res = UtilMath.reflectVector(new double[] { ball.direction[0], ball.direction[1] },
                normal);
        newDirection[0] = res[0];
        newDirection[1] = res[1];
        ball.direction = newDirection;
        return true;
    }

    public String getMessage() {
        return "edge " + uuid + " " + points[0].x + " " + points[0].y + " " + points[1].x + " "
                + points[1].y + " " + (owner != null ? owner.name: "");
    }
}
