package com.tractorstore.exception;

import com.tractorstore.controller.CatalogController;
import com.tractorstore.service.CatalogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogController.class)
class GlobalExceptionHandlerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean  private CatalogService catalogService;

    @Test
    void illegalArgumentException_returns400WithProblemDetail() throws Exception {
        when(catalogService.getRecommendations(any()))
                .thenThrow(new IllegalArgumentException("Invalid hex color"));

        mockMvc.perform(get("/api/catalog/recommendations").param("skus", "BAD"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid hex color"));
    }

    @Test
    void genericException_returns500WithProblemDetail() throws Exception {
        when(catalogService.getHome()).thenThrow(new RuntimeException("DB connection lost"));

        mockMvc.perform(get("/api/catalog/home"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Internal Server Error"));
    }
}
