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
