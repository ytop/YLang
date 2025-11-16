package com.ylang.backend.ast;

import java.util.List;
import java.util.ArrayList;

/**
 * Node representing a trait declaration
 */
public class TraitDeclarationNode extends BaseASTNode {
    private final String name;
    private final List<FunctionSignatureNode> functionSignatures;
    
    public static class FunctionSignatureNode extends BaseASTNode {
        private final String name;
        private final List<ParameterNode> parameters;
        private final TypeNode returnType;
        
        public FunctionSignatureNode(String name, List<ParameterNode> parameters, TypeNode returnType) {
            this.name = name;
            this.parameters = new ArrayList<>(parameters);
            this.returnType = returnType;
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
    }
    
    public TraitDeclarationNode(String name, List<FunctionSignatureNode> functionSignatures) {
        this.name = name;
        this.functionSignatures = new ArrayList<>(functionSignatures);
    }
    
    public String getName() {
        return name;
    }
    
    public List<FunctionSignatureNode> getFunctionSignatures() {
        return new ArrayList<>(functionSignatures);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitTraitDeclaration(this);
    }
    
    @Override
    public String toString() {
        return "TraitDeclarationNode{name='" + name + "'}";
    }
}