package com.ylang.backend.ast;

/**
 * Base interface for all AST nodes in the Y language
 */
public interface ASTNode {
    /**
     * Accept a visitor to perform operations on this node
     * @param visitor The visitor to accept
     * @param <T> The return type of the visitor
     * @return The result of the visitor operation
     */
    <T> T accept(ASTVisitor<T> visitor);
    
    /**
     * Get the line number where this node appears in the source code
     * @return The line number, or -1 if not available
     */
    int getLine();
    
    /**
     * Get the column number where this node appears in the source code
     * @return The column number, or -1 if not available
     */
    int getColumn();
    
    /**
     * Set the source location for this node
     * @param line The line number
     * @param column The column number
     */
    void setLocation(int line, int column);
}