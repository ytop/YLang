package com.ylang.backend.ast;

/**
 * Node representing an implementation block (Rust impl or TypeScript class implementation)
 */
public class ImplementationNode extends BaseASTNode {
    private final String targetType;
    private final String traitName; // Can be null for inherent implementations
    private final java.util.List<ASTNode> methods;
    
    public ImplementationNode(String targetType, String traitName, java.util.List<ASTNode> methods) {
        this.targetType = targetType;
        this.traitName = traitName;
        this.methods = new java.util.ArrayList<>(methods);
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public String getTraitName() {
        return traitName;
    }
    
    public java.util.List<ASTNode> getMethods() {
        return new java.util.ArrayList<>(methods);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImplementation(this);
    }
}