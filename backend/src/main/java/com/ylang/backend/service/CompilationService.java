package com.ylang.backend.service;

import com.ylang.backend.ast.ProgramNode;
import com.ylang.backend.dto.CompileResponse;
import com.ylang.backend.exception.YLanguageParseException;
import com.ylang.backend.model.ParseResult;
import com.ylang.backend.translator.RustTranslator;
import com.ylang.backend.translator.TypeScriptTranslator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Main compilation service that orchestrates parsing and translation
 */
@Service
public class CompilationService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompilationService.class);
    
    @Autowired
    private YLanguageParserService parserService;
    
    @Autowired
    private TypeScriptTranslator typeScriptTranslator;
    
    @Autowired
    private RustTranslator rustTranslator;
    
    /**
     * Compile Y language code to the specified target language
     * @param code The Y language source code
     * @param targetLanguage The target language (rust or typescript)
     * @param projectId Optional project ID for tracking
     * @return CompileResponse with the compiled code or error information
     */
    public CompileResponse compile(String code, String targetLanguage, String projectId) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.info("Starting compilation of Y language code to {}", targetLanguage);
            
            // Step 1: Parse the Y language code
            logger.debug("Parsing Y language code");
            ParseResult parseResult = parserService.parse(code);
            
            if (!parseResult.isSuccess()) {
                logger.error("Parsing failed with {} errors", parseResult.getErrors().size());
                long executionTime = System.currentTimeMillis() - startTime;
                CompileResponse response = CompileResponse.failure(parseResult.getErrors());
                response.setExecutionTimeMs(executionTime);
                return response;
            }
            
            ProgramNode ast = parseResult.getAst();
            logger.debug("Successfully parsed Y language code into AST");
            
            // Step 2: Generate warnings (semantic analysis would go here)
            List<String> warnings = generateWarnings(ast);
            
            // Step 3: Translate to target language
            logger.debug("Translating AST to {}", targetLanguage);
            String compiledCode;
            
            switch (targetLanguage.toLowerCase()) {
                case "typescript":
                    compiledCode = typeScriptTranslator.translate(ast);
                    break;
                case "rust":
                    compiledCode = rustTranslator.translate(ast);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported target language: " + targetLanguage);
            }
            
            logger.info("Successfully compiled Y language code to {}", targetLanguage);
            
            long executionTime = System.currentTimeMillis() - startTime;
            CompileResponse response = CompileResponse.success(compiledCode, ast);
            response.setWarnings(warnings);
            response.setExecutionTimeMs(executionTime);
            
            return response;
            
        } catch (YLanguageParseException e) {
            logger.error("Parse exception during compilation", e);
            List<String> errors = new ArrayList<>();
            errors.add("Parse error: " + e.getMessage());
            long executionTime = System.currentTimeMillis() - startTime;
            CompileResponse response = CompileResponse.failure(errors);
            response.setExecutionTimeMs(executionTime);
            return response;
            
        } catch (Exception e) {
            logger.error("Unexpected error during compilation", e);
            List<String> errors = new ArrayList<>();
            errors.add("Unexpected error: " + e.getMessage());
            long executionTime = System.currentTimeMillis() - startTime;
            CompileResponse response = CompileResponse.failure(errors);
            response.setExecutionTimeMs(executionTime);
            return response;
        }
    }
    
    /**
     * Validate Y language code without compiling
     * @param code The Y language source code
     * @return Validation result with errors and warnings
     */
    public ValidateResponse validate(String code) {
        long startTime = System.currentTimeMillis();
        
        try {
            logger.debug("Validating Y language code");
            ParseResult parseResult = parserService.parse(code);
            
            long executionTime = System.currentTimeMillis() - startTime;
            
            if (parseResult.isSuccess()) {
                List<String> warnings = generateWarnings(parseResult.getAst());
                ValidateResponse response = ValidateResponse.success(warnings);
                response.setValidationTimeMs(executionTime);
                return response;
            } else {
                ValidateResponse response = ValidateResponse.failure(parseResult.getErrors());
                response.setValidationTimeMs(executionTime);
                return response;
            }
            
        } catch (Exception e) {
            logger.error("Error during validation", e);
            List<String> errors = new ArrayList<>();
            errors.add("Validation error: " + e.getMessage());
            long executionTime = System.currentTimeMillis() - startTime;
            ValidateResponse response = ValidateResponse.failure(errors);
            response.setValidationTimeMs(executionTime);
            return response;
        }
    }
    
    /**
     * Generate warnings from the AST (semantic analysis)
     * @param ast The parsed AST
     * @return List of warning messages
     */
    private List<String> generateWarnings(ProgramNode ast) {
        List<String> warnings = new ArrayList<>();
        
        // TODO: Implement semantic analysis to generate warnings
        // Examples:
        // - Unused variables
        // - Potential type issues
        // - Performance suggestions
        // - Style recommendations
        
        return warnings;
    }
}