package com.ylang.backend.ast;

/**
 * Node representing a loop statement (for each or while loop)
 */
public class LoopStatementNode extends BaseASTNode {
    public enum LoopType {
        FOR_EACH, WHILE
    }
    
    private final LoopType loopType;
    private final String variableName; // For for-each loops
    private final ExpressionNode iterable; // For for-each loops
    private final ExpressionNode condition; // For while loops
    private final String incrementVar; // For while loops
    private final BlockNode body;
    
    // For-each loop constructor
    public LoopStatementNode(String variableName, ExpressionNode iterable, BlockNode body) {
        this.loopType = LoopType.FOR_EACH;
        this.variableName = variableName;
        this.iterable = iterable;
        this.condition = null;
        this.incrementVar = null;
        this.body = body;
    }
    
    // While loop constructor
    public LoopStatementNode(ExpressionNode condition, String incrementVar, BlockNode body) {
        this.loopType = LoopType.WHILE;
        this.variableName = null;
        this.iterable = null;
        this.condition = condition;
        this.incrementVar = incrementVar;
        this.body = body;
    }
    
    public LoopType getLoopType() {
        return loopType;
    }
    
    public String getVariableName() {
        return variableName;
    }
    
    public ExpressionNode getIterable() {
        return iterable;
    }
    
    public ExpressionNode getCondition() {
        return condition;
    }
    
    public String getIncrementVar() {
        return incrementVar;
    }
    
    public BlockNode getBody() {
        return body;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLoopStatement(this);
    }
    
    @Override
    public String toString() {
        return "LoopStatementNode{type=" + loopType + "}";
    }
}