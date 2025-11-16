package com.ylang.backend.service;

import com.ylang.backend.ast.*;
import org.antlr.v4.runtime.tree.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that converts ANTLR parse tree to our custom AST
 */
public class ASTBuilderVisitor extends YLanguageBaseVisitor<ASTNode> {
    
    @Override
    public ProgramNode visitProgram(YLanguageParser.ProgramContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        
        for (YLanguageParser.StatementContext stmtCtx : ctx.statement()) {
            ASTNode statement = visit(stmtCtx);
            if (statement != null) {
                statements.add(statement);
            }
        }
        
        return new ProgramNode(statements);
    }
    
    @Override
    public ASTNode visitStatement(YLanguageParser.StatementContext ctx) {
        // Handle different types of statements
        if (ctx.functionDeclaration() != null) {
            return visit(ctx.functionDeclaration());
        } else if (ctx.variableDeclaration() != null) {
            return visit(ctx.variableDeclaration());
        } else if (ctx.assignment() != null) {
            return visit(ctx.assignment());
        } else if (ctx.controlFlow() != null) {
            return visit(ctx.controlFlow());
        } else if (ctx.loopStatement() != null) {
            return visit(ctx.loopStatement());
        } else if (ctx.returnStatement() != null) {
            return visit(ctx.returnStatement());
        } else if (ctx.expressionStatement() != null) {
            return visit(ctx.expressionStatement());
        }
        // Add more statement types as needed
        
        return null;
    }
    
    @Override
    public FunctionDeclarationNode visitFunctionDeclaration(YLanguageParser.FunctionDeclarationContext ctx) {
        String name = ctx.identifier().getText();
        boolean isAsync = ctx.ASYNC() != null;
        
        // Parse parameters
        List<ParameterNode> parameters = new ArrayList<>();
        if (ctx.parameterList() != null && !ctx.parameterList().getText().equals("nothing")) {
            for (YLanguageParser.ParameterContext paramCtx : ctx.parameterList().parameter()) {
                parameters.add(visitParameter(paramCtx));
            }
        }
        
        TypeNode returnType = visitType(ctx.type());
        BlockNode body = visitBlock(ctx.block());
        
        String yummyComment = null;
        if (ctx.YUMMY() != null) {
            yummyComment = ctx.description().getText();
        }
        
        FunctionDeclarationNode node = new FunctionDeclarationNode(name, parameters, returnType, body, isAsync, yummyComment);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public ParameterNode visitParameter(YLanguageParser.ParameterContext ctx) {
        String name = ctx.identifier().getText();
        TypeNode type = visitType(ctx.type());
        
        String yummyComment = null;
        if (ctx.YUMMY() != null) {
            yummyComment = ctx.description().getText();
        }
        
        ParameterNode node = new ParameterNode(name, type, yummyComment);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public VariableDeclarationNode visitVariableDeclaration(YLanguageParser.VariableDeclarationContext ctx) {
        String name = ctx.identifier().getText();
        TypeNode type = visitType(ctx.type());
        ExpressionNode initializer = visitExpression(ctx.expression());
        VariableDeclarationNode node = new VariableDeclarationNode(name, type, initializer);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public AssignmentNode visitAssignment(YLanguageParser.AssignmentContext ctx) {
        String variableName = ctx.identifier().getText();
        ExpressionNode value = visitExpression(ctx.expression());
        AssignmentNode node = new AssignmentNode(variableName, value);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public IfStatementNode visitControlFlow(YLanguageParser.ControlFlowContext ctx) {
        ExpressionNode condition = visitExpression(ctx.expression());
        BlockNode thenBlock = visitBlock(ctx.block(0));
        BlockNode elseBlock = ctx.block().size() > 1 ? visitBlock(ctx.block(1)) : null;
        IfStatementNode node = new IfStatementNode(condition, thenBlock, elseBlock);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public ReturnStatementNode visitReturnStatement(YLanguageParser.ReturnStatementContext ctx) {
        ExpressionNode value = ctx.expression() != null ? visitExpression(ctx.expression()) : null;
        ReturnStatementNode node = new ReturnStatementNode(value);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public ExpressionStatementNode visitExpressionStatement(YLanguageParser.ExpressionStatementContext ctx) {
        ExpressionNode expression = visitExpression(ctx.expression());
        ExpressionStatementNode node = new ExpressionStatementNode(expression);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public ExpressionNode visitExpression(YLanguageParser.ExpressionContext ctx) {
        // Handle different types of expressions
        if (ctx.literal() != null) {
            return visitLiteral(ctx.literal());
        } else if (ctx.identifier() != null) {
            return new IdentifierNode(ctx.identifier().getText());
        } else if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        } else if (ctx.binaryExpression() != null) {
            return visitBinaryExpression(ctx.binaryExpression());
        } else if (ctx.unaryExpression() != null) {
            return visitUnaryExpression(ctx.unaryExpression());
        } else if (ctx.conditionalExpression() != null) {
            return visitConditionalExpression(ctx.conditionalExpression());
        } else if (ctx.parenthesizedExpression() != null) {
            return visitParenthesizedExpression(ctx.parenthesizedExpression());
        }
        // Add more expression types as needed
        
        return null;
    }
    
    @Override
    public LiteralNode visitLiteral(YLanguageParser.LiteralContext ctx) {
        if (ctx.STRING() != null) {
            String value = ctx.STRING().getText();
            // Remove quotes
            value = value.substring(1, value.length() - 1);
            LiteralNode node = new LiteralNode(LiteralNode.LiteralType.STRING, value);
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return node;
        } else if (ctx.NUMBER() != null) {
            String numberText = ctx.NUMBER().getText();
            Number value = numberText.contains(".") ? Double.parseDouble(numberText) : Long.parseLong(numberText);
            LiteralNode node = new LiteralNode(LiteralNode.LiteralType.NUMBER, value);
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return node;
        } else if (ctx.BOOLEAN() != null) {
            Boolean value = Boolean.parseBoolean(ctx.BOOLEAN().getText());
            LiteralNode node = new LiteralNode(LiteralNode.LiteralType.BOOLEAN, value);
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return node;
        } else if (ctx.EMPTY() != null && ctx.LIST() != null) {
            LiteralNode node = new LiteralNode(LiteralNode.LiteralType.EMPTY_LIST, null);
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return node;
        } else if (ctx.EMPTY() != null && ctx.MAP() != null) {
            LiteralNode node = new LiteralNode(LiteralNode.LiteralType.EMPTY_MAP, null);
            node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
            return node;
        }
        
        return null;
    }
    
    @Override
    public FunctionCallNode visitFunctionCall(YLanguageParser.FunctionCallContext ctx) {
        String functionName = ctx.identifier().getText();
        List<ExpressionNode> arguments = new ArrayList<>();
        
        if (ctx.argumentList() != null && !ctx.argumentList().getText().equals("nothing")) {
            for (YLanguageParser.ExpressionContext exprCtx : ctx.argumentList().expression()) {
                arguments.add(visitExpression(exprCtx));
            }
        }
        
        return new FunctionCallNode(functionName, arguments);
    }
    
    @Override
    public BinaryExpressionNode visitBinaryExpression(YLanguageParser.BinaryExpressionContext ctx) {
        ExpressionNode left = visitExpression(ctx.expression(0));
        ExpressionNode right = visitExpression(ctx.expression(1));
        
        BinaryExpressionNode.Operator operator = parseOperator(ctx.operator());
        
        return new BinaryExpressionNode(left, operator, right);
    }
    
    @Override
    public UnaryExpressionNode visitUnaryExpression(YLanguageParser.UnaryExpressionContext ctx) {
        UnaryExpressionNode.Operator operator = ctx.NOT() != null ? 
            UnaryExpressionNode.Operator.NOT : UnaryExpressionNode.Operator.MINUS;
        
        ExpressionNode operand = visitExpression(ctx.expression());
        return new UnaryExpressionNode(operator, operand);
    }
    
    @Override
    public ConditionalExpressionNode visitConditionalExpression(YLanguageParser.ConditionalExpressionContext ctx) {
        ExpressionNode condition = visitExpression(ctx.expression(0));
        ExpressionNode thenExpr = visitExpression(ctx.expression(1));
        ExpressionNode elseExpr = visitExpression(ctx.expression(2));
        
        return new ConditionalExpressionNode(condition, thenExpr, elseExpr);
    }
    
    @Override
    public ParenthesizedExpressionNode visitParenthesizedExpression(YLanguageParser.ParenthesizedExpressionContext ctx) {
        ExpressionNode expression = visitExpression(ctx.expression());
        ParenthesizedExpressionNode node = new ParenthesizedExpressionNode(expression);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    @Override
    public BlockNode visitBlock(YLanguageParser.BlockContext ctx) {
        List<ASTNode> statements = new ArrayList<>();
        
        for (YLanguageParser.StatementContext stmtCtx : ctx.statement()) {
            ASTNode statement = visit(stmtCtx);
            if (statement != null) {
                statements.add(statement);
            }
        }
        
        BlockNode node = new BlockNode(statements);
        node.setLocation(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
        return node;
    }
    
    // Helper methods
    
    private TypeNode visitType(YLanguageParser.TypeContext ctx) {
        if (ctx == null) return TypeNode.nothingType();
        
        // Basic types
        if (ctx.STRING_TYPE() != null) return TypeNode.stringType();
        if (ctx.NUMBER_TYPE() != null) return TypeNode.numberType();
        if (ctx.BOOLEAN_TYPE() != null) return TypeNode.booleanType();
        if (ctx.NOTHING_TYPE() != null) return TypeNode.nothingType();
        if (ctx.ANY_TYPE() != null) return TypeNode.anyType();
        
        // Complex types
        if (ctx.LIST() != null) {
            TypeNode elementType = visitType(ctx.type(0));
            return TypeNode.listType(elementType);
        }
        
        if (ctx.MAP() != null) {
            TypeNode keyType = visitType(ctx.type(0));
            TypeNode valueType = visitType(ctx.type(1));
            return TypeNode.mapType(keyType, valueType);
        }
        
        if (ctx.EITHER() != null) {
            TypeNode leftType = visitType(ctx.type(0));
            TypeNode rightType = visitType(ctx.type(1));
            return TypeNode.eitherType(leftType, rightType);
        }
        
        // Identifier types
        if (ctx.IDENTIFIER() != null) {
            return new TypeNode(ctx.IDENTIFIER().getText());
        }
        
        return TypeNode.anyType(); // Default fallback
    }
    
    private BinaryExpressionNode.Operator parseOperator(YLanguageParser.OperatorContext ctx) {
        if (ctx.PLUS() != null) return BinaryExpressionNode.Operator.PLUS;
        if (ctx.MINUS() != null) return BinaryExpressionNode.Operator.MINUS;
        if (ctx.TIMES() != null) return BinaryExpressionNode.Operator.TIMES;
        if (ctx.DIVIDED() != null) return BinaryExpressionNode.Operator.DIVIDED_BY;
        if (ctx.EQUALS() != null) return BinaryExpressionNode.Operator.EQUALS;
        if (ctx.IS() != null && ctx.GREATER() != null) return BinaryExpressionNode.Operator.GREATER_THAN;
        if (ctx.IS() != null && ctx.LESS() != null) return BinaryExpressionNode.Operator.LESS_THAN;
        if (ctx.AND() != null) return BinaryExpressionNode.Operator.AND;
        if (ctx.OR() != null) return BinaryExpressionNode.Operator.OR;
        if (ctx.MODULO() != null) return BinaryExpressionNode.Operator.MODULO;
        
        return BinaryExpressionNode.Operator.PLUS; // Default fallback
    }
}