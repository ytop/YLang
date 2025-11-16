package com.ylang.backend.ast;

/**
 * Node representing a binary expression (e.g., a plus b, x equals y)
 */
public class BinaryExpressionNode extends ExpressionNode {
    public enum Operator {
        PLUS, MINUS, TIMES, DIVIDED_BY, EQUALS, GREATER_THAN, LESS_THAN, 
        AND, OR, MODULO
    }
    
    private final ExpressionNode left;
    private final Operator operator;
    private final ExpressionNode right;
    
    public BinaryExpressionNode(ExpressionNode left, Operator operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
    
    public ExpressionNode getLeft() {
        return left;
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public ExpressionNode getRight() {
        return right;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
    
    @Override
    public String toString() {
        return "BinaryExpressionNode{operator=" + operator + "}";
    }
}