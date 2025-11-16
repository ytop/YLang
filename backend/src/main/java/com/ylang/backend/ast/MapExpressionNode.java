package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a map expression (key-value pairs)
 */
public class MapExpressionNode extends ExpressionNode {
    public static class KeyValuePair {
        private final ExpressionNode key;
        private final ExpressionNode value;
        
        public KeyValuePair(ExpressionNode key, ExpressionNode value) {
            this.key = key;
            this.value = value;
        }
        
        public ExpressionNode getKey() {
            return key;
        }
        
        public ExpressionNode getValue() {
            return value;
        }
    }
    
    private final List<KeyValuePair> pairs;
    
    public MapExpressionNode(List<KeyValuePair> pairs) {
        this.pairs = new ArrayList<>(pairs);
    }
    
    public List<KeyValuePair> getPairs() {
        return new ArrayList<>(pairs);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitMapExpression(this);
    }
    
    @Override
    public String toString() {
        return "MapExpressionNode{pairs=" + pairs.size() + "}";
    }
}