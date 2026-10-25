package roz.graph.dto;

import java.util.List;

public class MathDtos {

    // --- 2D Plot ---
    public record Plot2DRequest(String formula, double xMin, double xMax, int points) {}
    public record Plot2DResponse(List<Double> x, List<Double> y) {}

    // --- 3D Plot ---
    public record Plot3DRequest(String formula, double xMin, double xMax, double yMin, double yMax, int resolution) {}
    public record Plot3DResponse(List<Double> vertices, List<Integer> indices) {}

    // --- Derivative ---
    public record DerivativeRequest(String formula, double x) {}
    public record DerivativeResponse(double x, double derivative) {}

    // --- Integral ---
    public record IntegralRequest(String formula, double a, double b) {}
    public record IntegralResponse(double area) {}

    // --- Roots & Extrema ---
    public record RootsRequest(String formula, double xMin, double xMax, double step) {}
    public record Point(double x, double y) {}
    public record RootsResponse(List<Point> roots, List<Point> extrema) {}
}