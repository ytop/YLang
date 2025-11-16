package com.ylang.backend.dto;

import java.util.List;
import java.util.ArrayList;

/**
 * Response DTO for validation operations
 */
public class ValidateResponse {
    
    private boolean valid;
    private List<String> errors;
    private List<String> warnings;
    private long validationTimeMs;
    
    public ValidateResponse() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    public ValidateResponse(boolean valid, List<String> errors, List<String> warnings) {
        this.valid = valid;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
    }
    
    public static ValidateResponse success(List<String> warnings) {
        return new ValidateResponse(true, new ArrayList<>(), warnings);
    }
    
    public static ValidateResponse failure(List<String> errors) {
        return new ValidateResponse(false, errors, new ArrayList<>());
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public void setErrors(List<String> errors) {
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
    }
    
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
    
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
    }
    
    public long getValidationTimeMs() {
        return validationTimeMs;
    }
    
    public void setValidationTimeMs(long validationTimeMs) {
        this.validationTimeMs = validationTimeMs;
    }
}