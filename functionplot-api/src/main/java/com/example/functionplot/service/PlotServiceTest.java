
package com.example.functionplot.service;

import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertThrows;
import static org.testng.AssertJUnit.assertEquals;

public class PlotServiceTest {

    private final CoordinateTransformer coordinateTransformer =
            new CoordinateTransformer();

    private final SvgGeneratorService svgGenerator =
            new SvgGeneratorService();

    private final PlotService plotService =
            new PlotService(coordinateTransformer, svgGenerator);

    @Test
    public void shouldEvaluatePolynomialCorrectly() {
        List<Double> coefficients = List.of(
                1.0, 2.0, 3.0, 4.0, 5.0
        );

        double result = plotService.evaluatePolynomial(
                coefficients,
                2.0
        );

        assertEquals(129.0, result, 0.0001);
    }

    @Test
    public void shouldRejectEmptyCoefficients() {
        assertThrows(
                IllegalArgumentException.class,
                () -> plotService.evaluatePolynomial(List.of(), 2.0)
        );
    }
}
