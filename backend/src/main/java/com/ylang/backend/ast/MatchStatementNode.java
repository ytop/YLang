package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a match statement (pattern matching)
 */
public class MatchStatementNode extends BaseASTNode {
    private final ExpressionNode expression;
    private final List<MatchCase> cases;
    
    public static class MatchCase {
        private final String pattern;
        private final String variableName;
        private final TypeNode type;
        private final BlockNode block;
        
        public MatchCase(String pattern, String variableName, TypeNode type, BlockNode block) {
            this.pattern = pattern;
            this.variableName = variableName;
            this.type = type;
            this.block = block;
        }
        
        public String getPattern() {
            return pattern;
        }
        
        public String getVariableName() {
            return variableName;
        }
        
        public TypeNode getType() {
            return type;
        }
        
        public BlockNode getBlock() {
            return block;
        }
    }
    
    public MatchStatementNode(ExpressionNode expression, List<MatchCase> cases) {
        this.expression = expression;
        this.cases = new ArrayList<>(cases);
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
    
    public List<MatchCase> getCases() {
        return new ArrayList<>(cases);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMatchStatement(this);
    }
    
    @Override
    public String toString() {
        return "MatchStatementNode{cases=" + cases.size() + "}";
    }
}