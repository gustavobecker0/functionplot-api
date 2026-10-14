
package com.example.functionplot.service;

import com.example.functionplot.dto.PlotPoint;
import com.example.functionplot.dto.PlotRequest;
import com.example.functionplot.dto.SvgPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PlotService {

    private final CoordinateTransformer coordinateTransformer;
    private final SvgGeneratorService svgGenerator;

    public PlotService(
            CoordinateTransformer coordinateTransformer,
            SvgGeneratorService svgGenerator
    ) {
        this.coordinateTransformer = coordinateTransformer;
        this.svgGenerator = svgGenerator;
    }

    public double evaluatePolynomial(
            List<Double> coefficients,
            double xValue
    ) {
        Objects.requireNonNull(
                coefficients,
                "Coefficients cannot be null"
        );

        if (coefficients.isEmpty()) {
            throw new IllegalArgumentException(
                    "Coefficients cannot be empty"
            );
        }

        double result = 0.0;

        for (int i = coefficients.size() - 1; i >= 0; i--) {
            result = result * xValue + coefficients.get(i);
        }

        return result;
    }

    public List<PlotPoint> samplePolynomial(
            List<Double> coefficients,
            double xMin,
            double xMax,
            int numberOfPoints
    ) {
        Objects.requireNonNull(
                coefficients,
                "Coefficients cannot be null"
        );

        if (coefficients.isEmpty()) {
            throw new IllegalArgumentException(
                    "Coefficients cannot be empty"
            );
        }

        if (!Double.isFinite(xMin) || !Double.isFinite(xMax)) {
            throw new IllegalArgumentException(
                    "Range boundaries must be finite numbers"
            );
        }

        if (xMin >= xMax) {
            throw new IllegalArgumentException(
                    "xMin must be less than xMax"
            );
        }

        if (numberOfPoints < 2) {
            throw new IllegalArgumentException(
                    "At least two points are required"
            );
        }

        double deltaX = (xMax - xMin) / (numberOfPoints - 1);

        List<PlotPoint> points = new ArrayList<>(numberOfPoints);

        for (int i = 0; i < numberOfPoints; i++) {
            double x = (i == numberOfPoints - 1)
                    ? xMax
                    : xMin + i * deltaX;

            double y = evaluatePolynomial(coefficients, x);

            if (!Double.isFinite(y)) {
                throw new IllegalArgumentException(
                        "Polynomial produced a non-finite result"
                );
            }

            points.add(new PlotPoint(x, y));
        }

        return points;
    }


    public String generateSvg(PlotRequest request) {
        Objects.requireNonNull(request, "Plot request cannot be null");

        int numberOfPoints = request.getWidth();

        List<PlotPoint> points = samplePolynomial(
                request.getCoefficients(),
                request.getXMin(),
                request.getXMax(),
                numberOfPoints
        );

        double yMin = points.stream()
                .mapToDouble(PlotPoint::yValue)
                .min()
                .orElseThrow();

        double yMax = points.stream()
                .mapToDouble(PlotPoint::yValue)
                .max()
                .orElseThrow();

        List<SvgPoint> svgPoints = coordinateTransformer.transform(
                points,
                request.getXMin(),
                request.getXMax(),
                request.getWidth(),
                request.getHeight()
        );

        return svgGenerator.generate(
                svgPoints,
                request.getWidth(),
                request.getHeight(),
                request.getXMin(),
                request.getXMax(),
                yMin,
                yMax
        );
    }
}
