package roz.graph.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PlotRequest {
    private String formula; // Например, "sin(x)*cos(y)"
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    private int resolution;
}