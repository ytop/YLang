package com.ylang.backend.ast;

/**
 * Node representing an import statement
 */
public class ImportStatementNode extends BaseASTNode {
    private final String moduleName;
    
    public ImportStatementNode(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitImportStatement(this);
    }
    
    @Override
    public String toString() {
        return "ImportStatementNode{moduleName='" + moduleName + "'}";
    }
}