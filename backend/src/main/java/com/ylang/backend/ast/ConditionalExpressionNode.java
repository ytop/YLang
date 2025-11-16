package com.ylang.backend.ast;

/**
 * Node representing a conditional expression (e.g., x if condition otherwise y)
 */
public class ConditionalExpressionNode extends ExpressionNode {
    private final ExpressionNode condition;
    private final ExpressionNode thenExpression;
    private final ExpressionNode elseExpression;
    
    public ConditionalExpressionNode(ExpressionNode condition, ExpressionNode thenExpression, ExpressionNode elseExpression) {
        this.condition = condition;
        this.thenExpression = thenExpression;
        this.elseExpression = elseExpression;
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public ExpressionNode getThenExpression() {
        return thenExpression;
    }
    
    public ExpressionNode getElseExpression() {
        return elseExpression;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConditionalExpression(this);
    }
    
    @Override
    public String toString() {
        return "ConditionalExpressionNode{}";
    }
}