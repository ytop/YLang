package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a structure declaration
 */
public class StructureDeclarationNode extends BaseASTNode {
    private final String name;
    private final String genericType; // Can be null
    private final String implementsTrait; // Can be null
    private final List<ASTNode> members;
    
    public StructureDeclarationNode(String name, String genericType, String implementsTrait, List<ASTNode> members) {
        this.name = name;
        this.genericType = genericType;
        this.implementsTrait = implementsTrait;
        this.members = new ArrayList<>(members);
    }
    
    public String getName() {
        return name;
    }
    
    public String getGenericType() {
        return genericType;
    }
    
    public boolean hasGenericType() {
        return genericType != null;
    }
    
    public String getImplementsTrait() {
        return implementsTrait;
    }
    
    public boolean hasImplementsTrait() {
        return implementsTrait != null;
    }
    
    public List<ASTNode> getMembers() {
        return new ArrayList<>(members);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitStructureDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "StructureDeclarationNode{name='" + name + "'}";
    }
}