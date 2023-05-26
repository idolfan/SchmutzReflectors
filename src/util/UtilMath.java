package util;

public class UtilMath {

    /** @returns dotProduct of two Vectors */
    public static double dotProduct(double[] v1, double[] v2) {
        double result = 0;
        for (int i = 0; i < v1.length; i++) {
            result += v1[i] * v2[i];
        }
        return result;
    }

    /**
     * Checks collision between an oriented edge and a circle
     * 
     * @param edge   contains the coordinates of the two points of the edge
     * @param circle contains the coordinates of the center of the circle and its
     *               radius
     * @return null when not colliding, otherwise the new position and direction of
     *         the circle
     */
    public static double[] isColliding(double[] edge, double[] circle) {
        double x1 = edge[0];
        double y1 = edge[1];
        double x2 = edge[2];
        double y2 = edge[3];
        double cx = circle[0];
        double cy = circle[1];
        double r = circle[2];
        double[] edgeVector = new double[] { x2 - x1, y2 - y1 };
        /* double[] edgeNormal = new double[] { edgeVector[1], -edgeVector[0] }; */
        /** Vector from p1 to c */
        double edgeVectorLengthSquared = edgeVector[0] * edgeVector[0] + edgeVector[1] * edgeVector[1];
        double[] circleVector = new double[] { cx - x1, cy - y1 };
        double projectionFactor = dotProduct(circleVector, edgeVector) / edgeVectorLengthSquared;
        double projectionX = x1 + projectionFactor * edgeVector[0];
        double projectionY = y1 + projectionFactor * edgeVector[1];
        double distance = Math.sqrt((cx - projectionX) * (cx - projectionX) + (cy - projectionY) * (cy - projectionY));
        if (projectionFactor < 0 || projectionFactor > 1) {
            return null;
        }
        if (distance > r) {
            return null;
        }
        return new double[] { projectionX + projectionX - cx, projectionY + projectionY - cy };

    }

    public static double[] getProjection() {

        return new double[] {};
    }

    public static double[] reflectVector(double[] vector, double[] normal) {
        // normalize normal
        double normalLength = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1]);
        normal[0] /= normalLength;
        normal[1] /= normalLength;
            double velocityDotProduct = dotProduct(normal, vector);
            double[] result = new double[]{vector[0] - 2 * velocityDotProduct * normal[0], vector[1] - 2 * velocityDotProduct * normal[1]};
            return result;
    }
}
