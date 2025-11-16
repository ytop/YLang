package com.ylang.backend.ast;

/**
 * Node representing a parenthesized expression
 */
public class ParenthesizedExpressionNode extends ExpressionNode {
    private final ExpressionNode expression;
    
    public ParenthesizedExpressionNode(ExpressionNode expression) {
        this.expression = expression;
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitParenthesizedExpression(this);
    }
    
    @Override
    public String toString() {
        return "ParenthesizedExpressionNode{}";
    }
}