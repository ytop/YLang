package com.ylang.backend.ast;

/**
 * Node representing a try-catch statement
 */
public class TryStatementNode extends BaseASTNode {
    private final BlockNode tryBlock;
    private final java.util.List<CatchClause> catchClauses;
    
    public static class CatchClause {
        private final String variableName;
        private final TypeNode exceptionType;
        private final BlockNode catchBlock;
        
        public CatchClause(String variableName, TypeNode exceptionType, BlockNode catchBlock) {
            this.variableName = variableName;
            this.exceptionType = exceptionType;
            this.catchBlock = catchBlock;
        }
        
        public String getVariableName() {
            return variableName;
        }
        
        public TypeNode getExceptionType() {
            return exceptionType;
        }
        
        public BlockNode getCatchBlock() {
            return catchBlock;
        }
    }
    
    public TryStatementNode(BlockNode tryBlock, java.util.List<CatchClause> catchClauses) {
        this.tryBlock = tryBlock;
        this.catchClauses = new java.util.ArrayList<>(catchClauses);
    }
    
    public BlockNode getTryBlock() {
        return tryBlock;
    }
    
    public java.util.List<CatchClause> getCatchClauses() {
        return new java.util.ArrayList<>(catchClauses);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTryStatement(this);
    }
    
    @Override
    public String toString() {
        return "TryStatementNode{catchClauses=" + catchClauses.size() + "}";
    }
}