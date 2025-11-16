package com.ylang.backend.ast;

/**
 * Node representing a TypeScript type alias declaration
 */
public class TypeAliasDeclarationNode extends BaseASTNode {
    private final String name;
    private final String genericType; // Can be null
    private final TypeNode aliasedType;
    
    public TypeAliasDeclarationNode(String name, String genericType, TypeNode aliasedType) {
        this.name = name;
        this.genericType = genericType;
        this.aliasedType = aliasedType;
    }
    
    public String getName() {
        return name;
    }
    
    public String getGenericType() {
        return genericType;
    }
    
    public TypeNode getAliasedType() {
        return aliasedType;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTypeAliasDeclaration(this);
    }
}