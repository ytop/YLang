package com.ylang.backend.controller;

import com.ylang.backend.dto.CompileRequest;
import com.ylang.backend.dto.CompileResponse;
import com.ylang.backend.dto.ValidateRequest;
import com.ylang.backend.dto.ValidateResponse;
import com.ylang.backend.service.CompilationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CompilationController.class)
class CompilationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompilationService compilationService;

    @Test
    @DisplayName("POST /v1/compile returns 200 on success")
    void compile_success_returns_200() throws Exception {
        when(compilationService.compile(eq("code"), eq("typescript"), eq(null)))
                .thenReturn(CompileResponse.success("generated", null));

        String body = "{\n" +
                "  \"code\": \"code\",\n" +
                "  \"targetLanguage\": \"typescript\"\n" +
                "}";

        mockMvc.perform(post("/v1/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.compiledCode").value("generated"));
    }

    @Test
    @DisplayName("POST /v1/compile returns 400 on failure")
    void compile_failure_returns_400() throws Exception {
        when(compilationService.compile(eq("bad"), eq("rust"), eq(null)))
                .thenReturn(CompileResponse.failure(java.util.List.of("error")));

        String body = "{\n" +
                "  \"code\": \"bad\",\n" +
                "  \"targetLanguage\": \"rust\"\n" +
                "}";

        mockMvc.perform(post("/v1/compile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errors[0]").value("error"));
    }

    @Test
    @DisplayName("POST /v1/validate returns 200 on valid code")
    void validate_success_returns_200() throws Exception {
        when(compilationService.validate(eq("ok")))
                .thenReturn(ValidateResponse.success(java.util.List.of()));

        String body = "{\n" +
                "  \"code\": \"ok\"\n" +
                "}";

        mockMvc.perform(post("/v1/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    @DisplayName("POST /v1/validate returns 400 on invalid code")
    void validate_failure_returns_400() throws Exception {
        when(compilationService.validate(eq("oops")))
                .thenReturn(ValidateResponse.failure(java.util.List.of("invalid")));

        String body = "{\n" +
                "  \"code\": \"oops\"\n" +
                "}";

        mockMvc.perform(post("/v1/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.errors[0]").value("invalid"));
    }

    @Test
    @DisplayName("GET /v1/health returns 200")
    void health_returns_200() throws Exception {
        mockMvc.perform(get("/v1/health"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Backend is running")));
    }

    @Test
    @DisplayName("GET /v1/info returns API info")
    void info_returns_200_and_fields() throws Exception {
        mockMvc.perform(get("/v1/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Y Language Backend API"))
                .andExpect(jsonPath("$.version").exists())
                .andExpect(jsonPath("$.availableEndpoints").isArray());
    }
}