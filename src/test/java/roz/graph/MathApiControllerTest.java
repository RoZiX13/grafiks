package roz.graph;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import roz.graph.controllers.MathApiController;
import roz.graph.dto.MathDtos.*;
import roz.graph.services.MathService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MathApiControllerTest {

    @Mock
    private MathService mathService;

    @InjectMocks
    private MathApiController controller;

    @Test
    void plot2d_shouldReturnOkAndDelegateToService() {
        Plot2DRequest request = mock(Plot2DRequest.class);
        Plot2DResponse expected = mock(Plot2DResponse.class);
        when(mathService.calculate2D(request)).thenReturn(expected);

        ResponseEntity<Plot2DResponse> response = controller.plot2d(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(mathService).calculate2D(request);
    }

    @Test
    void plot3d_shouldReturnOkAndDelegateToService() {
        Plot3DRequest request = mock(Plot3DRequest.class);
        Plot3DResponse expected = mock(Plot3DResponse.class);
        when(mathService.calculate3D(request)).thenReturn(expected);

        ResponseEntity<Plot3DResponse> response = controller.plot3d(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(mathService).calculate3D(request);
    }

    @Test
    void derivative_shouldReturnOkAndDelegateToService() {
        DerivativeRequest request = mock(DerivativeRequest.class);
        DerivativeResponse expected = mock(DerivativeResponse.class);
        when(mathService.calculateDerivative(request)).thenReturn(expected);

        ResponseEntity<DerivativeResponse> response = controller.derivative(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(mathService).calculateDerivative(request);
    }

    @Test
    void integral_shouldReturnOkAndDelegateToService() {
        IntegralRequest request = mock(IntegralRequest.class);
        IntegralResponse expected = mock(IntegralResponse.class);
        when(mathService.calculateIntegral(request)).thenReturn(expected);

        ResponseEntity<IntegralResponse> response = controller.integral(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(mathService).calculateIntegral(request);
    }

    @Test
    void roots_shouldReturnOkAndDelegateToService() {
        RootsRequest request = mock(RootsRequest.class);
        RootsResponse expected = mock(RootsResponse.class);
        when(mathService.findRootsAndExtrema(request)).thenReturn(expected);

        ResponseEntity<RootsResponse> response = controller.roots(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(expected, response.getBody());
        verify(mathService).findRootsAndExtrema(request);
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithMessage() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid expression");

        ResponseEntity<String> response = controller.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid expression", response.getBody());
    }
}