package com.ylang.backend.ast;

/**
 * Base abstract class for AST nodes providing common functionality
 */
public abstract class BaseASTNode implements ASTNode {
    private int line = -1;
    private int column = -1;
    
    @Override
    public int getLine() {
        return line;
    }
    
    @Override
    public int getColumn() {
        return column;
    }
    
    @Override
    public void setLocation(int line, int column) {
        this.line = line;
        this.column = column;
    }
    
    /**
     * Utility method to safely accept a visitor
     * @param visitor The visitor to accept
     * @param <T> The return type
     * @return The visitor result
     */
    protected <T> T safeAccept(ASTVisitor<T> visitor) {
        if (visitor == null) {
            throw new IllegalArgumentException("Visitor cannot be null");
        }
        return accept(visitor);
    }
}