package com.ylang.backend.ast;

/**
 * Node representing a lifetime annotation in Rust
 */
public class LifetimeNode extends BaseASTNode {
    private final String name;
    
    public LifetimeNode(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLifetime(this);
    }
    
    @Override
    public String toString() {
        return "'" + name;
    }
}