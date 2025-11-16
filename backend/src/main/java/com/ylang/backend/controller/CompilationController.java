package com.ylang.backend.controller;

import com.ylang.backend.dto.*;
import com.ylang.backend.service.CompilationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for Y language compilation and translation operations
 */
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CompilationController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompilationController.class);
    
    @Autowired
    private CompilationService compilationService;
    
    /**
     * Compile Y language code to target language
     * @param request The compilation request
     * @return Compilation response with generated code or errors
     */
    @PostMapping("/compile")
    public ResponseEntity<CompileResponse> compile(@Valid @RequestBody CompileRequest request) {
        logger.info("Received compilation request for target language: {}", request.getTargetLanguage());
        
        try {
            CompileResponse response = compilationService.compile(
                request.getCode(),
                request.getTargetLanguage(),
                request.getProjectId()
            );
            
            if (response.isSuccess()) {
                logger.info("Compilation successful, returning {} characters of generated code", 
                           response.getCompiledCode().length());
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Compilation failed with {} errors", response.getErrors().size());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during compilation", e);
            CompileResponse errorResponse = CompileResponse.failure(
                java.util.List.of("Internal server error: " + e.getMessage())
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Validate Y language syntax without compilation
     * @param request The validation request
     * @return Validation response with errors and warnings
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validate(@Valid @RequestBody ValidateRequest request) {
        logger.info("Received validation request");
        
        try {
            ValidateResponse response = compilationService.validate(request.getCode());
            
            if (response.isValid()) {
                logger.info("Validation successful");
                return ResponseEntity.ok(response);
            } else {
                logger.warn("Validation failed with {} errors", response.getErrors().size());
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            logger.error("Unexpected error during validation", e);
            ValidateResponse errorResponse = ValidateResponse.failure(
                java.util.List.of("Internal server error: " + e.getMessage())
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * Translate Y language code to TypeScript
     * Convenience endpoint for TypeScript translation
     * @param request The compilation request with targetLanguage = "typescript"
     * @return Compilation response with TypeScript code
     */
    @PostMapping("/translate/typescript")
    public ResponseEntity<CompileResponse> translateToTypeScript(@Valid @RequestBody CompileRequest request) {
        request.setTargetLanguage("typescript");
        return compile(request);
    }
    
    /**
     * Translate Y language code to Rust
     * Convenience endpoint for Rust translation
     * @param request The compilation request with targetLanguage = "rust"
     * @return Compilation response with Rust code
     */
    @PostMapping("/translate/rust")
    public ResponseEntity<CompileResponse> translateToRust(@Valid @RequestBody CompileRequest request) {
        request.setTargetLanguage("rust");
        return compile(request);
    }
    
    /**
     * Health check endpoint
     * @return Simple health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Y Language Backend is running");
    }
    
    /**
     * Get API information
     * @return API version and information
     */
    @GetMapping("/info")
    public ResponseEntity<ApiInfoResponse> info() {
        ApiInfoResponse info = new ApiInfoResponse(
            "Y Language Backend API",
            "1.0.0",
            "Backend service for Y language compiler and translator",
            java.util.List.of("compile", "validate", "translate")
        );
        return ResponseEntity.ok(info);
    }
}