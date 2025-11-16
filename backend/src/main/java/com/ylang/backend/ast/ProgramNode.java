package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Root node representing a complete Y language program
 */
public class ProgramNode extends BaseASTNode {
    private final List<ASTNode> statements;
    
    public ProgramNode(List<ASTNode> statements) {
        this.statements = new ArrayList<>(statements);
    }
    
    public List<ASTNode> getStatements() {
        return new ArrayList<>(statements);
    }
    
    public void addStatement(ASTNode statement) {
        statements.add(statement);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitProgram(this);
    }
    
    @Override
    public String toString() {
        return "ProgramNode{statements=" + statements.size() + "}";
    }
}