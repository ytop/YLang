package com.ylang.backend.dto;

import com.ylang.backend.ast.ProgramNode;
import java.util.List;
import java.util.ArrayList;

/**
 * Response DTO for compilation operations
 */
public class CompileResponse {
    
    private boolean success;
    private String compiledCode;
    private List<String> errors;
    private List<String> warnings;
    private ProgramNode ast;
    private long executionTimeMs;
    
    public CompileResponse() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    public CompileResponse(boolean success, String compiledCode, List<String> errors, List<String> warnings, ProgramNode ast) {
        this.success = success;
        this.compiledCode = compiledCode;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
        this.ast = ast;
    }
    
    public static CompileResponse success(String compiledCode, ProgramNode ast) {
        return new CompileResponse(true, compiledCode, new ArrayList<>(), new ArrayList<>(), ast);
    }
    
    public static CompileResponse failure(List<String> errors) {
        return new CompileResponse(false, null, errors, new ArrayList<>(), null);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getCompiledCode() {
        return compiledCode;
    }
    
    public void setCompiledCode(String compiledCode) {
        this.compiledCode = compiledCode;
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
    
    public ProgramNode getAst() {
        return ast;
    }
    
    public void setAst(ProgramNode ast) {
        this.ast = ast;
    }
    
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}