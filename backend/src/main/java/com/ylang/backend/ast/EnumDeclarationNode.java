package com.ylang.backend.ast;

/**
 * Node representing an enum declaration (Rust enum or TypeScript enum)
 */
public class EnumDeclarationNode extends BaseASTNode {
    private final String name;
    private final String genericType; // Can be null
    private final java.util.List<EnumVariant> variants;
    
    public static class EnumVariant {
        private final String name;
        private final java.util.List<TypeNode> fieldTypes;
        
        public EnumVariant(String name, java.util.List<TypeNode> fieldTypes) {
            this.name = name;
            this.fieldTypes = new java.util.ArrayList<>(fieldTypes);
        }
        
        public String getName() {
            return name;
        }
        
        public java.util.List<TypeNode> getFieldTypes() {
            return new java.util.ArrayList<>(fieldTypes);
        }
    }
    
    public EnumDeclarationNode(String name, String genericType, java.util.List<EnumVariant> variants) {
        this.name = name;
        this.genericType = genericType;
        this.variants = new java.util.ArrayList<>(variants);
    }
    
    public String getName() {
        return name;
    }
    
    public String getGenericType() {
        return genericType;
    }
    
    public java.util.List<EnumVariant> getVariants() {
        return new java.util.ArrayList<>(variants);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitEnumDeclaration(this);
    }
}