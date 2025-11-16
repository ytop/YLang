package com.ylang.backend.ast;

/**
 * Node representing member access (e.g., object.field or array[index])
 */
public class MemberAccessNode extends ExpressionNode {
    public enum AccessType {
        DOT, AT
    }
    
    private final ExpressionNode object;
    private final AccessType accessType;
    private final ExpressionNode member;
    
    public MemberAccessNode(ExpressionNode object, AccessType accessType, ExpressionNode member) {
        this.object = object;
        this.accessType = accessType;
        this.member = member;
    }
    
    public ExpressionNode getObject() {
        return object;
    }
    
    public AccessType getAccessType() {
        return accessType;
    }
    
    public ExpressionNode getMember() {
        return member;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMemberAccess(this);
    }
    
    @Override
    public String toString() {
        return "MemberAccessNode{type=" + accessType + "}";
    }
}