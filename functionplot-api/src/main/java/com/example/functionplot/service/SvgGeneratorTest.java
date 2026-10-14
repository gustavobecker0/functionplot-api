
package com.example.functionplot.service;

import com.example.functionplot.dto.SvgPoint;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertTrue;

class SvgGeneratorTest {

    private final SvgGeneratorService generator =
            new SvgGeneratorService();

    @Test
    void shouldGenerateSvgWithDimensionsAndPoints() {
        List<SvgPoint> points = List.of(
                new SvgPoint(60.0, 20.0),
                new SvgPoint(50.0, 50.0),
                new SvgPoint(40.0, 80.0)
        );

        String svg = generator.generate(
                points,
                100,
                100,
                -1.0,
                1.0,
                -1.0,
                1.0
        );

        assertTrue(svg.contains("<svg"));
        assertTrue(svg.contains("width=\"100\""));
        assertTrue(svg.contains("height=\"100\""));
        assertTrue(svg.contains("60.00,20.00"));
        assertTrue(svg.contains("50.00,50.00"));
        assertTrue(svg.contains("40.00,80.00"));
        assertTrue(svg.contains("<polyline"));
        assertTrue(svg.contains("<line"));
        assertTrue(svg.contains("</svg>"));
    }

    @Test
    void shouldRejectFewerThanTwoPoints() {
        assertThrows(
                IllegalArgumentException.class,
                () -> generator.generate(
                        List.of(new SvgPoint(0.0, 0.0)),
                        100,
                        100,
                        -1.0,
                        1.0,
                        -1.0,
                        1.0
                )
        );
    }
}