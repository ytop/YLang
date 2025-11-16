package com.ylang.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for validation operations
 */
public class ValidateRequest {
    
    @NotBlank(message = "Y language code is required")
    private String code;
    
    public ValidateRequest() {}
    
    public ValidateRequest(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
}