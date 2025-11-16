package com.ylang.backend.ast;

/**
 * Node representing an expression statement
 */
public class ExpressionStatementNode extends BaseASTNode {
    private final ExpressionNode expression;
    
    public ExpressionStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
    
    @Override
    public String toString() {
        return "ExpressionStatementNode{}";
    }
}