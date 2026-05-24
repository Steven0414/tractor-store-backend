package com.tractorstore.service;

/**
 * Pure-Java color distance calculations in the RGB color space.
 *
 * <p>Euclidean distance formula:
 * <pre>distance = sqrt((r1-r2)² + (g1-g2)² + (b1-b2)²)</pre>
 * Range: [0, ~441.67] where 0 = identical colors and ~441.67 = black vs white.
 */
public final class ColorDistanceService {

    private ColorDistanceService() {}

    /**
     * Parses a hex color string (e.g. "#FF6600" or "FF6600") into an int[3] {r, g, b}.
     *
     * @throws IllegalArgumentException if the string is not a valid hex color
     */
    public static int[] hexToRgb(String hex) {
        String clean = hex.startsWith("#") ? hex.substring(1) : hex;
        if (clean.length() != 6) {
            throw new IllegalArgumentException("Invalid hex color: " + hex);
        }
        int r = Integer.parseInt(clean.substring(0, 2), 16);
        int g = Integer.parseInt(clean.substring(2, 4), 16);
        int b = Integer.parseInt(clean.substring(4, 6), 16);
        return new int[]{r, g, b};
    }

    /**
     * Computes the Euclidean distance between two hex colors in RGB space.
     */
    public static double distance(String hexA, String hexB) {
        int[] a = hexToRgb(hexA);
        int[] b = hexToRgb(hexB);
        double dr = a[0] - b[0];
        double dg = a[1] - b[1];
        double db = a[2] - b[2];
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Returns the minimum Euclidean distance from {@code candidateHex} to any color
     * in {@code referenceHexColors}. If the list is empty, returns Double.MAX_VALUE.
     */
    public static double minDistanceToAny(String candidateHex, java.util.List<String> referenceHexColors) {
        double min = Double.MAX_VALUE;
        for (String ref : referenceHexColors) {
            double d = distance(candidateHex, ref);
            if (d < min) {
                min = d;
            }
        }
        return min;
    }
}
