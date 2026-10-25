package roz.graph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roz.graph.controllers.MathApiController;
import roz.graph.dto.MathDtos.*;
import roz.graph.services.MathService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MathApiController.class)
class MathApiControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MathService mathService;

    @Test
    void plot2d_shouldReturn200() throws Exception {
        when(mathService.calculate2D(any())).thenReturn(null);

        mockMvc.perform(post("/api/plot/2d")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mathService).calculate2D(any(Plot2DRequest.class));
    }

    @Test
    void plot3d_shouldReturn200() throws Exception {
        when(mathService.calculate3D(any())).thenReturn(null);

        mockMvc.perform(post("/api/plot/3d")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mathService).calculate3D(any(Plot3DRequest.class));
    }

    @Test
    void derivative_shouldReturn200() throws Exception {
        when(mathService.calculateDerivative(any())).thenReturn(null);

        mockMvc.perform(post("/api/math/derivative")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mathService).calculateDerivative(any(DerivativeRequest.class));
    }

    @Test
    void integral_shouldReturn200() throws Exception {
        when(mathService.calculateIntegral(any())).thenReturn(null);

        mockMvc.perform(post("/api/math/integral")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mathService).calculateIntegral(any(IntegralRequest.class));
    }

    @Test
    void roots_shouldReturn200() throws Exception {
        when(mathService.findRootsAndExtrema(any())).thenReturn(null);

        mockMvc.perform(post("/api/math/roots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());

        verify(mathService).findRootsAndExtrema(any(RootsRequest.class));
    }

    @Test
    void whenIllegalArgument_shouldReturn400() throws Exception {
        when(mathService.calculate2D(any()))
                .thenThrow(new IllegalArgumentException("Bad request"));

        mockMvc.perform(post("/api/plot/2d")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Bad request"));
    }
}