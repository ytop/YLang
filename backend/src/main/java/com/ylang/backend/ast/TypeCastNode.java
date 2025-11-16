package com.ylang.backend.ast;

/**
 * Node representing a type cast expression
 */
public class TypeCastNode extends ExpressionNode {
    public enum CastType {
        TO_STRING, TO_NUMBER, TO_BOOLEAN
    }
    
    private final ExpressionNode expression;
    private final CastType castType;
    
    public TypeCastNode(ExpressionNode expression, CastType castType) {
        this.expression = expression;
        this.castType = castType;
    }
    
    public ExpressionNode getExpression() {
        return expression;
    }
    
    public CastType getCastType() {
        return castType;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTypeCast(this);
    }
    
    @Override
    public String toString() {
        return "TypeCastNode{castType=" + castType + "}";
    }
}