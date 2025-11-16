package com.ylang.backend.ast;

/**
 * Node representing a function parameter
 */
public class ParameterNode extends BaseASTNode {
    private final String name;
    private final TypeNode type;
    private final String yummyComment;
    
    public ParameterNode(String name, TypeNode type, String yummyComment) {
        this.name = name;
        this.type = type;
        this.yummyComment = yummyComment;
    }
    
    public String getName() {
        return name;
    }
    
    public TypeNode getType() {
        return type;
    }
    
    public String getYummyComment() {
        return yummyComment;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // Parameters are typically visited as part of function declarations
        return null; // This would be handled by specific visitors
    }
    
    @Override
    public String toString() {
        return "ParameterNode{name='" + name + "', type=" + type + "}";
    }
}