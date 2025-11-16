package com.ylang.backend.translator;

import com.ylang.backend.ast.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Comprehensive test suite for Y language translators
 */
public class TranslatorTestSuite {
    
    private TypeScriptTranslator typescriptTranslator;
    private RustTranslator rustTranslator;
    
    @BeforeEach
    void setUp() {
        typescriptTranslator = new TypeScriptTranslator();
        rustTranslator = new RustTranslator();
    }
    
    @Test
    void testTryCatchTranslation() {
        // Create try-catch AST
        BlockNode tryBlock = new BlockNode(Arrays.asList(
            new ExpressionStatementNode(new LiteralNode("Attempting operation"))
        ));
        
        TryStatementNode.CatchClause catchClause = new TryStatementNode.CatchClause(
            "error", TypeNode.stringType(), 
            new BlockNode(Arrays.asList(
                new ExpressionStatementNode(new LiteralNode("Error occurred"))
            ))
        );
        
        TryStatementNode tryNode = new TryStatementNode(tryBlock, Arrays.asList(catchClause));
        
        String tsResult = typescriptTranslator.visitTryStatement(tryNode);
        String rustResult = rustTranslator.visitTryStatement(tryNode);
        
        assertTrue(tsResult.contains("try {"));
        assertTrue(tsResult.contains("catch (error: string)"));
        assertTrue(rustResult.contains("match std::panic::catch_unwind"));
    }
    
    @Test
    void testMatchStatementTranslation() {
        // Create match statement AST
        ExpressionNode matchExpr = new IdentifierNode("value");
        
        MatchStatementNode.MatchCase case1 = new MatchStatementNode.MatchCase(
            "Ok", "val", TypeNode.stringType(),
            new BlockNode(Arrays.asList(
                new ReturnStatementNode(new IdentifierNode("val"))
            ))
        );
        
        MatchStatementNode.MatchCase case2 = new MatchStatementNode.MatchCase(
            "Err", null, null,
            new BlockNode(Arrays.asList(
                new ReturnStatementNode(new LiteralNode("Error"))
            ))
        );
        
        MatchStatementNode matchNode = new MatchStatementNode(matchExpr, Arrays.asList(case1, case2));
        
        String tsResult = typescriptTranslator.visitMatchStatement(matchNode);
        String rustResult = rustTranslator.visitMatchStatement(matchNode);
        
        assertTrue(tsResult.contains("switch (value)"));
        assertTrue(rustResult.contains("match value"));
    }
    
    @Test
    void testEnumDeclarationTranslation() {
        // Create enum declaration AST
        EnumDeclarationNode.EnumVariant variant1 = new EnumDeclarationNode.EnumVariant(
            "Ok", Arrays.asList(TypeNode.stringType())
        );
        
        EnumDeclarationNode.EnumVariant variant2 = new EnumDeclarationNode.EnumVariant(
            "Err", Arrays.asList(TypeNode.stringType())
        );
        
        EnumDeclarationNode enumNode = new EnumDeclarationNode(
            "Result", "T", Arrays.asList(variant1, variant2)
        );
        
        String tsResult = typescriptTranslator.visitEnumDeclaration(enumNode);
        String rustResult = rustTranslator.visitEnumDeclaration(enumNode);
        
        assertTrue(tsResult.contains("enum Result"));
        assertTrue(rustResult.contains("pub enum Result"));
        assertTrue(rustResult.contains("Ok("));
    }
    
    @Test
    void testInterfaceDeclarationTranslation() {
        // Create interface declaration AST
        InterfaceDeclarationNode.InterfaceMember member = 
            new InterfaceDeclarationNode.InterfaceMember("name", TypeNode.stringType(), false, false);
        
        InterfaceDeclarationNode interfaceNode = new InterfaceDeclarationNode(
            "User", null, Arrays.asList(member)
        );
        
        String tsResult = typescriptTranslator.visitInterfaceDeclaration(interfaceNode);
        String rustResult = rustTranslator.visitInterfaceDeclaration(interfaceNode);
        
        assertTrue(tsResult.contains("interface User"));
        assertTrue(tsResult.contains("name: string"));
        assertTrue(rustResult.contains("pub trait User"));
    }
    
    @Test
    void testTypeCastTranslation() {
        // Create type cast AST
        ExpressionNode expr = new IdentifierNode("value");
        TypeCastNode castNode = new TypeCastNode(expr, TypeCastNode.CastType.TO_STRING);
        
        String tsResult = typescriptTranslator.visitTypeCast(castNode);
        String rustResult = rustTranslator.visitTypeCast(castNode);
        
        assertTrue(tsResult.contains(".toString()"));
        assertTrue(rustResult.contains(".to_string()"));
    }
    
    @Test
    void testListExpressionTranslation() {
        // Create list expression AST
        ListExpressionNode listNode = new ListExpressionNode(Arrays.asList(
            new LiteralNode("1"),
            new LiteralNode("2"),
            new LiteralNode("3")
        ));
        
        String tsResult = typescriptTranslator.visitListExpression(listNode);
        String rustResult = rustTranslator.visitListExpression(listNode);
        
        assertTrue(tsResult.contains("[1, 2, 3]"));
        assertTrue(rustResult.contains("vec![1, 2, 3]"));
    }
    
    @Test
    void testMapExpressionTranslation() {
        // Create map expression AST
        MapEntryNode entry1 = new MapEntryNode(new LiteralNode("\"key1\""), new LiteralNode("\"value1\""));
        MapEntryNode entry2 = new MapEntryNode(new LiteralNode("\"key2\""), new LiteralNode("\"value2\""));
        
        MapExpressionNode mapNode = new MapExpressionNode(Arrays.asList(entry1, entry2));
        
        String tsResult = typescriptTranslator.visitMapExpression(mapNode);
        String rustResult = rustTranslator.visitMapExpression(mapNode);
        
        assertTrue(tsResult.contains("{\"key1\": \"value1\""));
        assertTrue(rustResult.contains("\"key1\": \"value1\""));
    }
}