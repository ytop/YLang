package com.ylang.backend.ast;

/**
 * Node representing a decorator/annotation (TypeScript decorator or Rust attribute)
 */
public class DecoratorNode extends BaseASTNode {
    private final String name;
    private final java.util.List<ExpressionNode> arguments;
    private final ASTNode target; // The decorated node
    
    public DecoratorNode(String name, java.util.List<ExpressionNode> arguments, ASTNode target) {
        this.name = name;
        this.arguments = new java.util.ArrayList<>(arguments);
        this.target = target;
    }
    
    public String getName() {
        return name;
    }
    
    public java.util.List<ExpressionNode> getArguments() {
        return new java.util.ArrayList<>(arguments);
    }
    
    public ASTNode getTarget() {
        return target;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitDecorator(this);
    }
}