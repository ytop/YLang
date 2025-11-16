package com.ylang.backend.ast;

/**
 * Node representing a unary expression (e.g., not x, -y)
 */
public class UnaryExpressionNode extends ExpressionNode {
    public enum Operator {
        NOT, MINUS
    }
    
    private final Operator operator;
    private final ExpressionNode operand;
    
    public UnaryExpressionNode(Operator operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public ExpressionNode getOperand() {
        return operand;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }
    
    @Override
    public String toString() {
        return "UnaryExpressionNode{operator=" + operator + "}";
    }
}