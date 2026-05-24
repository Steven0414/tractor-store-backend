package com.tractorstore.service;

import java.util.List;

/**
 * Pure-Java color distance calculations in the RGB color space.
 *
 * Euclidean distance: sqrt((r1-r2)² + (g1-g2)² + (b1-b2)²)
 * Range: [0, ~441.67] where 0 = identical colors.
 */
public final class ColorDistanceService {

    private ColorDistanceService() {}

    /** RGB components parsed from a hex string, e.g. "#FF6600" or "FF6600". */
    record Rgb(int r, int g, int b) {
        static Rgb parse(String hex) {
            String clean = hex.startsWith("#") ? hex.substring(1) : hex;
            if (clean.length() != 6) {
                throw new IllegalArgumentException("Invalid hex color: " + hex);
            }
            return new Rgb(
                Integer.parseInt(clean.substring(0, 2), 16),
                Integer.parseInt(clean.substring(2, 4), 16),
                Integer.parseInt(clean.substring(4, 6), 16)
            );
        }
    }

    /** Euclidean distance between two hex colors in RGB space. */
    public static double distance(String hexA, String hexB) {
        var a = Rgb.parse(hexA);
        var b = Rgb.parse(hexB);
        double dr = a.r() - b.r();
        double dg = a.g() - b.g();
        double db = a.b() - b.b();
        return Math.sqrt(dr * dr + dg * dg + db * db);
    }

    /**
     * Minimum Euclidean distance from {@code candidateHex} to any color
     * in {@code referenceColors}. Returns {@link Double#MAX_VALUE} if list is empty.
     */
    public static double minDistanceToAny(String candidateHex, List<String> referenceColors) {
        return referenceColors.stream()
            .mapToDouble(ref -> distance(candidateHex, ref))
            .min()
            .orElse(Double.MAX_VALUE);
    }
}
