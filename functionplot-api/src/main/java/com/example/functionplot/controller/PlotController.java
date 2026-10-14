
package com.example.functionplot.controller;

import com.example.functionplot.dto.PlotRequest;
import com.example.functionplot.service.PlotService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/plots")
public class PlotController {

    private final PlotService plotService;

    public PlotController(PlotService plotService) {
        this.plotService = plotService;
    }

    @PostMapping(produces = "image/svg+xml")
    public ResponseEntity<String> createPlot(
            @Valid @RequestBody PlotRequest request
    ) {
        String svg = plotService.generateSvg(request);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("image/svg+xml"))
                .body(svg);
    }
}
