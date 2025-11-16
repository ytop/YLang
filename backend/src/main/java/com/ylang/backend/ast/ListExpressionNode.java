package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a list expression
 */
public class ListExpressionNode extends ExpressionNode {
    private final List<ExpressionNode> elements;
    
    public ListExpressionNode(List<ExpressionNode> elements) {
        this.elements = new ArrayList<>(elements);
    }
    
    public List<ExpressionNode> getElements() {
        return new ArrayList<>(elements);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitListExpression(this);
    }
    
    @Override
    public String toString() {
        return "ListExpressionNode{elements=" + elements.size() + "}";
    }
}