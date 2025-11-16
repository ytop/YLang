package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a block of statements
 */
public class BlockNode extends BaseASTNode {
    private final List<ASTNode> statements;
    
    public BlockNode(List<ASTNode> statements) {
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
        // Block nodes are typically visited as part of other nodes
        return null;
    }
    
    @Override
    public String toString() {
        return "BlockNode{statements=" + statements.size() + "}";
    }
}