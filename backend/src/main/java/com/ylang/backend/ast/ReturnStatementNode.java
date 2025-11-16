package com.ylang.backend.ast;

/**
 * Node representing a return statement
 */
public class ReturnStatementNode extends BaseASTNode {
    private final ExpressionNode value; // Can be null for void returns
    
    public ReturnStatementNode(ExpressionNode value) {
        this.value = value;
    }
    
    public ExpressionNode getValue() {
        return value;
    }
    
    public boolean hasValue() {
        return value != null;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }
    
    @Override
    public String toString() {
        return "ReturnStatementNode{hasValue=" + (value != null) + "}";
    }
}