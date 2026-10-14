
package com.example.functionplot.service;

import com.example.functionplot.dto.SvgPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
public class SvgGeneratorService {

    public String generate(
            List<SvgPoint> points,
            int width,
            int height,
            double xMin,
            double xMax,
            double yMin,
            double yMax
    ) {
        Objects.requireNonNull(points, "Points cannot be null");

        if (points.size() < 2) {
            throw new IllegalArgumentException(
                    "At least two points are required"
            );
        }

        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                    "Width and height must be positive"
            );
        }

        if (!Double.isFinite(xMin)
                || !Double.isFinite(xMax)
                || xMin >= xMax) {
            throw new IllegalArgumentException("Invalid X range");
        }

        if (!Double.isFinite(yMin)
                || !Double.isFinite(yMax)
                || yMin > yMax) {
            throw new IllegalArgumentException("Invalid Y range");
        }

        double plotWidth = width
                - CoordinateTransformer.LEFT_MARGIN
                - CoordinateTransformer.RIGHT_MARGIN;

        double plotHeight = height
                - CoordinateTransformer.TOP_MARGIN
                - CoordinateTransformer.BOTTOM_MARGIN;

        if (plotWidth <= 0 || plotHeight <= 0) {
            throw new IllegalArgumentException(
                    "Canvas is too small for the margins"
            );
        }

        double left = CoordinateTransformer.LEFT_MARGIN;
        double top = CoordinateTransformer.TOP_MARGIN;
        double right = left + plotWidth;
        double bottom = top + plotHeight;

        StringBuilder svg = new StringBuilder();

        svg.append("""
                <svg xmlns="http://www.w3.org/2000/svg"
                     width="%d"
                     height="%d"
                     viewBox="0 0 %d %d">
                """.formatted(width, height, width, height));

        // White background
        svg.append("""
                <rect width="100%%" height="100%%" fill="white"/>
                """.replace("100%%", "100%"));

        for (int i = 0; i <= 5; i++) {
            double fraction = i / 5.0;

            double x = left + fraction * plotWidth;
            double value = xMin + fraction * (xMax - xMin);

            svg.append("""
                    <line x1="%.2f" y1="%.2f"
                          x2="%.2f" y2="%.2f"
                          stroke="#e5e7eb"
                          stroke-width="1"/>
                    <text x="%.2f" y="%.2f"
                          text-anchor="middle"
                          font-size="11"
                          fill="#374151">%s</text>
                    """.formatted(
                    x, top,
                    x, bottom,
                    x, bottom + 18,
                    format(value)
            ));
        }

        for (int i = 0; i <= 5; i++) {
            double fraction = i / 5.0;

            double y = bottom - fraction * plotHeight;
            double value = yMin + fraction * (yMax - yMin);

            svg.append("""
                    <line x1="%.2f" y1="%.2f"
                          x2="%.2f" y2="%.2f"
                          stroke="#e5e7eb"
                          stroke-width="1"/>
                    <text x="%.2f" y="%.2f"
                          text-anchor="end"
                          font-size="11"
                          fill="#374151">%s</text>
                    """.formatted(
                    left, y,
                    right, y,
                    left - 8, y + 4,
                    format(value)
            ));
        }

        if (yMin <= 0 && yMax >= 0 && yMax != yMin) {
            double yZero = bottom
                    - (0 - yMin) / (yMax - yMin) * plotHeight;

            svg.append("""
                    <line x1="%.2f" y1="%.2f"
                          x2="%.2f" y2="%.2f"
                          stroke="#111827"
                          stroke-width="1.5"/>
                    """.formatted(left, yZero, right, yZero));
        }

        if (xMin <= 0 && xMax >= 0) {
            double xZero = left
                    + (0 - xMin) / (xMax - xMin) * plotWidth;

            svg.append("""
                    <line x1="%.2f" y1="%.2f"
                          x2="%.2f" y2="%.2f"
                          stroke="#111827"
                          stroke-width="1.5"/>
                    """.formatted(xZero, top, xZero, bottom));
        }

        svg.append("<polyline points=\"");

        for (int i = 0; i < points.size(); i++) {
            SvgPoint point = Objects.requireNonNull(
                    points.get(i),
                    "Points cannot contain null elements"
            );

            if (!Double.isFinite(point.x())
                    || !Double.isFinite(point.y())) {
                throw new IllegalArgumentException(
                        "Point coordinates must be finite"
                );
            }

            if (i > 0) {
                svg.append(' ');
            }

            svg.append(String.format(
                    Locale.ROOT,
                    "%.2f,%.2f",
                    point.x(),
                    point.y()
            ));
        }

        svg.append("""
                "
                fill="none"
                stroke="#2563eb"
                stroke-width="2"
                stroke-linejoin="round"
                stroke-linecap="round"/>
                </svg>
                """);

        return svg.toString();
    }

    private String format(double value) {
        if (Math.abs(value) < 0.0000001) {
            value = 0;
        }

        return String.format(Locale.ROOT, "%.2f", value)
                .replaceAll("\\.?0+$", "");
    }
}
