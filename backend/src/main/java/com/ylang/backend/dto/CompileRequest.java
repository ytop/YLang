package com.ylang.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Request DTO for compilation operations
 */
public class CompileRequest {
    
    @NotBlank(message = "Y language code is required")
    private String code;
    
    @NotNull(message = "Target language is required")
    @Pattern(regexp = "^(rust|typescript)$", message = "Target language must be 'rust' or 'typescript'")
    private String targetLanguage;
    
    private String projectId;
    
    public CompileRequest() {}
    
    public CompileRequest(String code, String targetLanguage, String projectId) {
        this.code = code;
        this.targetLanguage = targetLanguage;
        this.projectId = projectId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getTargetLanguage() {
        return targetLanguage;
    }
    
    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}