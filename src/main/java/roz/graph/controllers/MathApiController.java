package roz.graph.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roz.graph.dto.MathDtos.*;
import roz.graph.services.MathService;

@RestController
@RequestMapping("/api")
public class MathApiController {

    private final MathService mathService;

    public MathApiController(MathService mathService) {
        this.mathService = mathService;
    }

    @PostMapping("/plot/2d")
    public ResponseEntity<Plot2DResponse> plot2d(@RequestBody Plot2DRequest request) {
        return ResponseEntity.ok(mathService.calculate2D(request));
    }

    @PostMapping("/plot/3d")
    public ResponseEntity<Plot3DResponse> plot3d(@RequestBody Plot3DRequest request) {
        return ResponseEntity.ok(mathService.calculate3D(request));
    }

    @PostMapping("/math/derivative")
    public ResponseEntity<DerivativeResponse> derivative(@RequestBody DerivativeRequest request) {
        return ResponseEntity.ok(mathService.calculateDerivative(request));
    }

    @PostMapping("/math/integral")
    public ResponseEntity<IntegralResponse> integral(@RequestBody IntegralRequest request) {
        return ResponseEntity.ok(mathService.calculateIntegral(request));
    }

    @PostMapping("/math/roots")
    public ResponseEntity<RootsResponse> roots(@RequestBody RootsRequest request) {
        return ResponseEntity.ok(mathService.findRootsAndExtrema(request));
    }

    @ExceptionHandler({IllegalArgumentException.class, SecurityException.class})
    public ResponseEntity<String> handleValidationExceptions(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}