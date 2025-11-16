package com.ylang.backend.service;

import com.ylang.backend.ast.ASTNode;
import com.ylang.backend.ast.ProgramNode;
import com.ylang.backend.exception.YLanguageParseException;
import com.ylang.backend.model.ParseResult;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.tree.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for parsing Y language source code into AST
 */
@Service
public class YLanguageParserService {
    
    private static final Logger logger = LoggerFactory.getLogger(YLanguageParserService.class);
    
    /**
     * Parse Y language source code into an AST
     * @param sourceCode The Y language source code
     * @return ParseResult containing the AST or error information
     */
    public ParseResult parse(String sourceCode) {
        try {
            logger.debug("Starting to parse Y language source code");
            
            // Create ANTLR input stream
            CharStream input = CharStreams.fromString(sourceCode);
            
            // Create lexer
            YLanguageLexer lexer = new YLanguageLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            // Create parser
            YLanguageParser parser = new YLanguageParser(tokens);
            
            // Set up error handling
            parser.removeErrorListeners();
            YLanguageErrorListener errorListener = new YLanguageErrorListener();
            parser.addErrorListener(errorListener);
            
            // Parse the program
            YLanguageParser.ProgramContext programContext = parser.program();
            
            // Check for syntax errors
            if (errorListener.hasErrors()) {
                logger.error("Syntax errors found during parsing");
                return ParseResult.failure(errorListener.getErrors());
            }
            
            // Build AST from parse tree
            ASTBuilderVisitor astBuilder = new ASTBuilderVisitor();
            ProgramNode ast = astBuilder.visit(programContext);
            
            logger.info("Successfully parsed Y language source code");
            return ParseResult.success(ast);
            
        } catch (Exception e) {
            logger.error("Unexpected error during parsing", e);
            List<String> errors = new ArrayList<>();
            errors.add("Unexpected error: " + e.getMessage());
            return ParseResult.failure(errors);
        }
    }
    
    /**
     * Custom error listener for ANTLR parsing
     */
    private static class YLanguageErrorListener extends BaseErrorListener {
        private final List<String> errors = new ArrayList<>();
        
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                               int line, int charPositionInLine, String msg, RecognitionException e) {
            String error = String.format("Line %d:%d - %s", line, charPositionInLine, msg);
            errors.add(error);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public List<String> getErrors() {
            return new ArrayList<>(errors);
        }
    }
}