package com.ylang.backend.ast;

/**
 * Node representing an assignment statement
 */
public class AssignmentNode extends BaseASTNode {
    private final String variableName;
    private final ExpressionNode value;
    
    public AssignmentNode(String variableName, ExpressionNode value) {
        this.variableName = variableName;
        this.value = value;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public ExpressionNode getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }
    
    @Override
    public String toString() {
        return "AssignmentNode{variable='" + variableName + "'}";
    }
}