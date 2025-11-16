package com.ylang.backend.ast;

/**
 * Node representing an identifier (variable name, function name, etc.)
 */
public class IdentifierNode extends ExpressionNode {
    private final String name;
    
    public IdentifierNode(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIdentifier(this);
    }
    
    @Override
    public String toString() {
        return "IdentifierNode{name='" + name + "'}";
    }
}