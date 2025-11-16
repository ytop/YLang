package com.ylang.backend.ast;

/**
 * Node representing a literal value (string, number, boolean, etc.)
 */
public class LiteralNode extends ExpressionNode {
    public enum LiteralType {
        STRING, NUMBER, BOOLEAN, EMPTY_LIST, EMPTY_MAP
    }
    
    private final LiteralType type;
    private final Object value;
    
    public LiteralNode(LiteralType type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    public LiteralType getLiteralType() {
        return type;
    }
    
    public Object getValue() {
        return value;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
    
    @Override
    public String toString() {
        return "LiteralNode{type=" + type + ", value=" + value + "}";
    }
}