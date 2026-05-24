package com.tractorstore.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColorDistanceServiceTest {

    @Test
    void hexToRgb_parsesCorrectly() {
        int[] rgb = ColorDistanceService.hexToRgb("#FF6600");
        assertArrayEquals(new int[]{255, 102, 0}, rgb);
    }

    @Test
    void hexToRgb_withoutHash() {
        int[] rgb = ColorDistanceService.hexToRgb("FF6600");
        assertArrayEquals(new int[]{255, 102, 0}, rgb);
    }

    @Test
    void distance_sameColor_isZero() {
        assertEquals(0.0, ColorDistanceService.distance("#CC0000", "#CC0000"), 0.001);
    }

    @Test
    void distance_blackToWhite_isMax() {
        double d = ColorDistanceService.distance("#000000", "#FFFFFF");
        assertEquals(Math.sqrt(3 * 255.0 * 255.0), d, 0.001);
    }

    @Test
    void distance_redIsCloserToOrangeThankToGreen() {
        double redToOrange  = ColorDistanceService.distance("#FF0000", "#FF6600");
        double greenToOrange = ColorDistanceService.distance("#00FF00", "#FF6600");
        assertTrue(redToOrange < greenToOrange, "Red should be closer to orange than green");
    }

    @Test
    void minDistanceToAny_returnsSmallest() {
        List<String> refs = List.of("#000000", "#FF0000");
        double d = ColorDistanceService.minDistanceToAny("#CC0000", refs);
        double expectedToRed = ColorDistanceService.distance("#CC0000", "#FF0000");
        assertEquals(expectedToRed, d, 0.001);
    }

    @Test
    void minDistanceToAny_emptyList_returnsMaxValue() {
        double d = ColorDistanceService.minDistanceToAny("#CC0000", List.of());
        assertEquals(Double.MAX_VALUE, d);
    }
}
