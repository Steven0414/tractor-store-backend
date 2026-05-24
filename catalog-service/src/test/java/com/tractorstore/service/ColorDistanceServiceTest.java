package com.tractorstore.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColorDistanceServiceTest {

    @Test
    void distance_sameColor_isZero() {
        assertEquals(0.0, ColorDistanceService.distance("#CC0000", "#CC0000"), 0.001);
    }

    @Test
    void distance_blackToWhite_isMax() {
        double expected = Math.sqrt(3 * 255.0 * 255.0);
        assertEquals(expected, ColorDistanceService.distance("#000000", "#FFFFFF"), 0.001);
    }

    @Test
    void distance_redIsCloserToOrangeThanGreen() {
        double redToOrange   = ColorDistanceService.distance("#FF0000", "#FF6600");
        double greenToOrange = ColorDistanceService.distance("#00FF00", "#FF6600");
        assertTrue(redToOrange < greenToOrange, "Red should be closer to orange than green");
    }

    @Test
    void distance_withoutHashPrefix() {
        assertEquals(
            ColorDistanceService.distance("#FF6600", "#CC0000"),
            ColorDistanceService.distance("FF6600", "CC0000"),
            0.001
        );
    }

    @Test
    void minDistanceToAny_returnsSmallest() {
        List<String> refs = List.of("#000000", "#FF0000");
        double d        = ColorDistanceService.minDistanceToAny("#CC0000", refs);
        double toRed    = ColorDistanceService.distance("#CC0000", "#FF0000");
        assertEquals(toRed, d, 0.001);
    }

    @Test
    void minDistanceToAny_emptyList_returnsMaxValue() {
        assertEquals(Double.MAX_VALUE,
            ColorDistanceService.minDistanceToAny("#CC0000", List.of()));
    }

    @Test
    void distance_invalidHex_throws() {
        assertThrows(IllegalArgumentException.class,
            () -> ColorDistanceService.distance("#GGGGGG", "#000000"));
    }
}
