package game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.UUID;
import java.util.Vector;

import util.UtilMath;

public class Edge {

    public Point[] points = new Point[2];

    /** Vector perpendicular to the edge */
    public Point normal;
    public Color color = Color.RED;
    public int width = 4;
    public String uuid;

    public Edge( String uuid, Point p1, Point p2, Color color) {
        points[0] = p1;
        points[1] = p2;
        normal = new Point(p2.y - p1.y, p1.x - p2.x);
        this.uuid = uuid;
        this.color = color;
    }

    public Edge(Point p1, Point p2){
        this(UUID.randomUUID().toString(),p1, p2, Player.getRandomColor());
    }

    public void drawThis(Graphics2D g) {
        g.setColor(color);
        g.setStroke(new BasicStroke(width));
        g.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
    }

    /**
     * returns whether the ball will collide with this edge in the next tick
     * 
     * @param ball the ball to check for collision with
     * @return whether the ball will collide with this edge in the next tick
     */
    /*
     * public boolean isColliding(Ball ball) {
     * return UtilMath.isColliding(new double[] {points[0].x, points[0].y,
     * points[1].x, points[1].y}, new double[]{ ball.dx, ball.dy, ball.radius});
     * }
     */

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
        double[] projectionVector = new double[] { (cx - projectionX) / (distance / r),
                (cy - projectionY) / (distance / r) };
        double remainingDistance = r - distance;
        /* ball.dx = projectionX + projectionVector[0] + remainingDistance * projectionVector[0];
        ball.dy = projectionY + projectionVector[1] + remainingDistance * projectionVector[1]; */

        Vector<Double> newDirection = new Vector<Double>();
        double[] normal = new double[] { edgeVector[1], -edgeVector[0] };
        double[] res = UtilMath.reflectVector(new double[] { ball.direction.get(0), ball.direction.get(1) },
                normal);
        newDirection.add(res[0]);
        newDirection.add(res[1]);
        ball.direction = newDirection;
        return true;
    }



    /**
     * returns the new direction of the ball after colliding with this edge
     * 
     * @param ball the ball to check for collision with
     * @return the new direction of the ball after colliding with this edge
     */
    public Vector<Double> getNewDirection(Ball ball) {
        Vector<Double> result = new Vector<Double>();
        result.add(ball.direction.get(0)
                - 2 * (ball.direction.get(0) * normal.x + ball.direction.get(1) * normal.y) * normal.x);
        result.add(ball.direction.get(1)
                - 2 * (ball.direction.get(0) * normal.x + ball.direction.get(1) * normal.y) * normal.y);
        return result;
    }

    public String getMessage() {
        return "edge " + uuid + " " + points[0].x + " " + points[0].y +" "+  points[1].x + " " + points[1].y;
    }
}
