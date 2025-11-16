package com.ylang.backend.ast;

/**
 * Node representing a variable declaration
 */
public class VariableDeclarationNode extends BaseASTNode {
    private final String name;
    private final TypeNode type;
    private final ExpressionNode initializer;
    
    public VariableDeclarationNode(String name, TypeNode type, ExpressionNode initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }
    
    public String getName() {
        return name;
    }
    
    public TypeNode getType() {
        return type;
    }
    
    public ExpressionNode getInitializer() {
        return initializer;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "VariableDeclarationNode{name='" + name + "', type=" + type + "}";
    }
}