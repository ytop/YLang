package com.ylang.backend.model;

import com.ylang.backend.ast.ProgramNode;
import java.util.List;
import java.util.ArrayList;

/**
 * Result of parsing Y language source code
 */
public class ParseResult {
    private final boolean success;
    private final ProgramNode ast;
    private final List<String> errors;
    
    private ParseResult(boolean success, ProgramNode ast, List<String> errors) {
        this.success = success;
        this.ast = ast;
        this.errors = new ArrayList<>(errors);
    }
    
    public static ParseResult success(ProgramNode ast) {
        return new ParseResult(true, ast, new ArrayList<>());
    }
    
    public static ParseResult failure(List<String> errors) {
        return new ParseResult(false, null, errors);
    }
    
    public static ParseResult failure(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ParseResult(false, null, errors);
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public ProgramNode getAst() {
        return ast;
    }
    
    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}