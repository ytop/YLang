package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a function declaration in Y language
 */
public class FunctionDeclarationNode extends BaseASTNode {
    private final String name;
    private final List<ParameterNode> parameters;
    private final TypeNode returnType;
    private final BlockNode body;
    private final boolean isAsync;
    private final String yummyComment;
    
    public FunctionDeclarationNode(String name, List<ParameterNode> parameters, TypeNode returnType, 
                                 BlockNode body, boolean isAsync, String yummyComment) {
        this.name = name;
        this.parameters = new ArrayList<>(parameters);
        this.returnType = returnType;
        this.body = body;
        this.isAsync = isAsync;
        this.yummyComment = yummyComment;
    }
    
    public String getName() {
        return name;
    }
    
    public List<ParameterNode> getParameters() {
        return new ArrayList<>(parameters);
    }
    
    public TypeNode getReturnType() {
        return returnType;
    }
    
    public BlockNode getBody() {
        return body;
    }
    
    public boolean isAsync() {
        return isAsync;
    }
    
    public String getYummyComment() {
        return yummyComment;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitFunctionDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "FunctionDeclarationNode{name='" + name + "', async=" + isAsync + "}";
    }
}