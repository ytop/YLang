package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a function call expression
 */
public class FunctionCallNode extends ExpressionNode {
    private final String functionName;
    private final List<ExpressionNode> arguments;
    
    public FunctionCallNode(String functionName, List<ExpressionNode> arguments) {
        this.functionName = functionName;
        this.arguments = new ArrayList<>(arguments);
    }
    
    public String getFunctionName() {
        return functionName;
    }
    
    public List<ExpressionNode> getArguments() {
        return new ArrayList<>(arguments);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionCall(this);
    }
    
    @Override
    public String toString() {
        return "FunctionCallNode{functionName='" + functionName + "', args=" + arguments.size() + "}";
    }
}