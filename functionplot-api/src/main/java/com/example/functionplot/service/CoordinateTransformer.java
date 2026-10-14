
package com.example.functionplot.service;

import com.example.functionplot.dto.PlotPoint;
import com.example.functionplot.dto.SvgPoint;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CoordinateTransformer {

    public static final double LEFT_MARGIN = 60;
    public static final double RIGHT_MARGIN = 20;
    public static final double TOP_MARGIN = 20;
    public static final double BOTTOM_MARGIN = 40;

    public List<SvgPoint> transform(
            List<PlotPoint> points,
            double xMin,
            double xMax,
            int width,
            int height
    ) {
        Objects.requireNonNull(points, "Points cannot be null");

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Points cannot be empty");
        }

        if (!Double.isFinite(xMin)
                || !Double.isFinite(xMax)
                || xMin >= xMax) {
            throw new IllegalArgumentException("Invalid x range");
        }

        double plotWidth = width - LEFT_MARGIN - RIGHT_MARGIN;
        double plotHeight = height - TOP_MARGIN - BOTTOM_MARGIN;

        if (plotWidth <= 0 || plotHeight <= 0) {
            throw new IllegalArgumentException(
                    "Canvas is too small for the margins"
            );
        }

        double yMin = Double.POSITIVE_INFINITY;
        double yMax = Double.NEGATIVE_INFINITY;

        for (PlotPoint point : points) {
            if (!Double.isFinite(point.xValue())
                    || !Double.isFinite(point.yValue())) {
                throw new IllegalArgumentException(
                        "Point coordinates must be finite"
                );
            }

            yMin = Math.min(yMin, point.yValue());
            yMax = Math.max(yMax, point.yValue());
        }

        List<SvgPoint> result = new ArrayList<>(points.size());

        for (PlotPoint point : points) {
            double svgX = LEFT_MARGIN
                    + (point.xValue() - xMin) / (xMax - xMin) * plotWidth;

            double svgY;

            if (yMax == yMin) {
                svgY = TOP_MARGIN + plotHeight / 2.0;
            } else {
                svgY = TOP_MARGIN + plotHeight
                        - (point.yValue() - yMin) / (yMax - yMin) * plotHeight;
            }

            result.add(new SvgPoint(svgX, svgY));
        }

        return result;
    }
}
