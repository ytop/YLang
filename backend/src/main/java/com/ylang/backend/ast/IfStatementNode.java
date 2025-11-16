package com.ylang.backend.ast;

/**
 * Node representing an if statement with optional otherwise (else) block
 */
public class IfStatementNode extends BaseASTNode {
    private final ExpressionNode condition;
    private final BlockNode thenBlock;
    private final BlockNode elseBlock; // Can be null
    
    public IfStatementNode(ExpressionNode condition, BlockNode thenBlock, BlockNode elseBlock) {
        this.condition = condition;
        this.thenBlock = thenBlock;
        this.elseBlock = elseBlock;
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public BlockNode getThenBlock() {
        return thenBlock;
    }
    
    public BlockNode getElseBlock() {
        return elseBlock;
    }
    
    public boolean hasElseBlock() {
        return elseBlock != null;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }
    
    @Override
    public String toString() {
        return "IfStatementNode{hasElse=" + (elseBlock != null) + "}";
    }
}