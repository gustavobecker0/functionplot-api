
package com.example.functionplot.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlotRequest {

    @NotNull
    @NotEmpty
    private List<Double> coefficients;

    @NotNull
    private Double xMin;

    @NotNull
    private Double xMax;

    @NotNull
    @Min(100)
    @Max(2000)
    private Integer width;

    @NotNull
    @Min(100)
    @Max(2000)
    private Integer height;

}
