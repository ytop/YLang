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

        if (ast == null) {
            return warnings;
        }

        // Unreachable code detection within blocks
        for (var statement : ast.getStatements()) {
            detectUnreachableInNode(statement, warnings);
        }

        // Unused variables per block and function
        for (var statement : ast.getStatements()) {
            detectUnusedVariables(statement, warnings);
        }

        // Simple type issues for constant expressions
        for (var statement : ast.getStatements()) {
            detectLiteralTypeMismatches(statement, warnings);
        }

        return warnings;
    }

    private void detectUnreachableInNode(com.ylang.backend.ast.ASTNode node, List<String> warnings) {
        if (node instanceof com.ylang.backend.ast.BlockNode block) {
            boolean seenReturn = false;
            for (com.ylang.backend.ast.ASTNode stmt : block.getStatements()) {
                if (seenReturn) {
                    String location = formatLocation(stmt);
                    warnings.add("Unreachable code after return" + location);
                }
                if (stmt instanceof com.ylang.backend.ast.ReturnStatementNode) {
                    seenReturn = true;
                }
                detectUnreachableInNode(stmt, warnings);
            }
        } else if (node instanceof com.ylang.backend.ast.IfStatementNode ifStmt) {
            if (ifStmt.getThenBlock() != null) detectUnreachableInNode(ifStmt.getThenBlock(), warnings);
            if (ifStmt.hasElseBlock() && ifStmt.getElseBlock() != null) detectUnreachableInNode(ifStmt.getElseBlock(), warnings);
        } else if (node instanceof com.ylang.backend.ast.LoopStatementNode loop) {
            if (loop.getBody() != null) detectUnreachableInNode(loop.getBody(), warnings);
        } else if (node instanceof com.ylang.backend.ast.FunctionDeclarationNode fn) {
            if (fn.getBody() != null) detectUnreachableInNode(fn.getBody(), warnings);
        }
    }

    private void detectUnusedVariables(com.ylang.backend.ast.ASTNode node, List<String> warnings) {
        if (node instanceof com.ylang.backend.ast.FunctionDeclarationNode fn) {
            var body = fn.getBody();
            if (body != null) {
                for (com.ylang.backend.ast.ASTNode stmt : body.getStatements()) {
                    if (stmt instanceof com.ylang.backend.ast.VariableDeclarationNode varDecl) {
                        String name = varDecl.getName();
                        int uses = countIdentifierUsage(body, name);
                        if (uses == 0) {
                            String location = formatLocation(varDecl);
                            warnings.add("Unused variable '" + name + "'" + location);
                        }
                    }
                }
            }
        } else if (node instanceof com.ylang.backend.ast.BlockNode block) {
            for (com.ylang.backend.ast.ASTNode stmt : block.getStatements()) {
                detectUnusedVariables(stmt, warnings);
            }
        }
    }

    private int countIdentifierUsage(com.ylang.backend.ast.ASTNode node, String name) {
        int count = 0;
        if (node instanceof com.ylang.backend.ast.IdentifierNode id) {
            if (name.equals(id.getName())) count++;
        } else if (node instanceof com.ylang.backend.ast.BlockNode block) {
            for (com.ylang.backend.ast.ASTNode stmt : block.getStatements()) {
                count += countIdentifierUsage(stmt, name);
            }
        } else if (node instanceof com.ylang.backend.ast.AssignmentNode asn) {
            count += countIdentifierUsage(asn.getValue(), name);
        } else if (node instanceof com.ylang.backend.ast.ExpressionStatementNode exprStmt) {
            count += countIdentifierUsage(exprStmt.getExpression(), name);
        } else if (node instanceof com.ylang.backend.ast.FunctionCallNode call) {
            for (com.ylang.backend.ast.ExpressionNode arg : call.getArguments()) {
                count += countIdentifierUsage(arg, name);
            }
        } else if (node instanceof com.ylang.backend.ast.BinaryExpressionNode bin) {
            count += countIdentifierUsage(bin.getLeft(), name);
            count += countIdentifierUsage(bin.getRight(), name);
        } else if (node instanceof com.ylang.backend.ast.UnaryExpressionNode un) {
            count += countIdentifierUsage(un.getOperand(), name);
        } else if (node instanceof com.ylang.backend.ast.IfStatementNode ifStmt) {
            count += countIdentifierUsage(ifStmt.getCondition(), name);
            if (ifStmt.getThenBlock() != null) count += countIdentifierUsage(ifStmt.getThenBlock(), name);
            if (ifStmt.getElseBlock() != null) count += countIdentifierUsage(ifStmt.getElseBlock(), name);
        } else if (node instanceof com.ylang.backend.ast.LoopStatementNode loop) {
            if (loop.getIterable() != null) count += countIdentifierUsage(loop.getIterable(), name);
            if (loop.getCondition() != null) count += countIdentifierUsage(loop.getCondition(), name);
            if (loop.getBody() != null) count += countIdentifierUsage(loop.getBody(), name);
        } else if (node instanceof com.ylang.backend.ast.ParenthesizedExpressionNode p) {
            count += countIdentifierUsage(p.getExpression(), name);
        } else if (node instanceof com.ylang.backend.ast.ListExpressionNode list) {
            for (com.ylang.backend.ast.ExpressionNode el : list.getElements()) {
                count += countIdentifierUsage(el, name);
            }
        } else if (node instanceof com.ylang.backend.ast.MapExpressionNode map) {
            for (com.ylang.backend.ast.MapEntryNode e : map.getEntries()) {
                count += countIdentifierUsage(e.getKey(), name);
                count += countIdentifierUsage(e.getValue(), name);
            }
        } else if (node instanceof com.ylang.backend.ast.MemberAccessNode mem) {
            count += countIdentifierUsage(mem.getObject(), name);
            count += countIdentifierUsage(mem.getMember(), name);
        } else if (node instanceof com.ylang.backend.ast.TypeCastNode cast) {
            count += countIdentifierUsage(cast.getExpression(), name);
        }
        return count;
    }

    private void detectLiteralTypeMismatches(com.ylang.backend.ast.ASTNode node, List<String> warnings) {
        if (node instanceof com.ylang.backend.ast.BinaryExpressionNode bin) {
            var left = bin.getLeft();
            var right = bin.getRight();
            if (bin.getOperator() == com.ylang.backend.ast.BinaryExpressionNode.Operator.PLUS
                    && left instanceof com.ylang.backend.ast.LiteralNode l
                    && right instanceof com.ylang.backend.ast.LiteralNode r) {
                boolean lStr = l.getLiteralType() == com.ylang.backend.ast.LiteralNode.LiteralType.STRING;
                boolean rStr = r.getLiteralType() == com.ylang.backend.ast.LiteralNode.LiteralType.STRING;
                boolean lNum = l.getLiteralType() == com.ylang.backend.ast.LiteralNode.LiteralType.NUMBER;
                boolean rNum = r.getLiteralType() == com.ylang.backend.ast.LiteralNode.LiteralType.NUMBER;
                if ((lStr && rNum) || (lNum && rStr)) {
                    String location = formatLocation(bin);
                    warnings.add("Suspicious string-number concatenation" + location);
                }
            }
        }

        if (node instanceof com.ylang.backend.ast.BlockNode block) {
            for (com.ylang.backend.ast.ASTNode stmt : block.getStatements()) {
                detectLiteralTypeMismatches(stmt, warnings);
            }
        } else if (node instanceof com.ylang.backend.ast.FunctionDeclarationNode fn) {
            if (fn.getBody() != null) detectLiteralTypeMismatches(fn.getBody(), warnings);
        } else if (node instanceof com.ylang.backend.ast.IfStatementNode ifStmt) {
            detectLiteralTypeMismatches(ifStmt.getCondition(), warnings);
            if (ifStmt.getThenBlock() != null) detectLiteralTypeMismatches(ifStmt.getThenBlock(), warnings);
            if (ifStmt.getElseBlock() != null) detectLiteralTypeMismatches(ifStmt.getElseBlock(), warnings);
        } else if (node instanceof com.ylang.backend.ast.LoopStatementNode loop) {
            if (loop.getCondition() != null) detectLiteralTypeMismatches(loop.getCondition(), warnings);
            if (loop.getIterable() != null) detectLiteralTypeMismatches(loop.getIterable(), warnings);
            if (loop.getBody() != null) detectLiteralTypeMismatches(loop.getBody(), warnings);
        } else if (node instanceof com.ylang.backend.ast.ExpressionStatementNode exprStmt) {
            detectLiteralTypeMismatches(exprStmt.getExpression(), warnings);
        } else if (node instanceof com.ylang.backend.ast.AssignmentNode asn) {
            detectLiteralTypeMismatches(asn.getValue(), warnings);
        } else if (node instanceof com.ylang.backend.ast.FunctionCallNode call) {
            for (com.ylang.backend.ast.ExpressionNode arg : call.getArguments()) {
                detectLiteralTypeMismatches(arg, warnings);
            }
        }
    }

    private String formatLocation(com.ylang.backend.ast.ASTNode node) {
        int line = node.getLine();
        int col = node.getColumn();
        if (line >= 0 && col >= 0) {
            return " (Line " + line + ":" + col + ")";
        }
        if (line >= 0) {
            return " (Line " + line + ")";
        }
        return "";
    }
}