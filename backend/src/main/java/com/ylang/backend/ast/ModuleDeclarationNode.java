package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a module declaration
 */
public class ModuleDeclarationNode extends BaseASTNode {
    private final String name;
    private final List<ASTNode> statements;
    
    public ModuleDeclarationNode(String name, List<ASTNode> statements) {
        this.name = name;
        this.statements = new ArrayList<>(statements);
    }
    
    public String getName() {
        return name;
    }
    
    public List<ASTNode> getStatements() {
        return new ArrayList<>(statements);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitModuleDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "ModuleDeclarationNode{name='" + name + "'}";
    }
}