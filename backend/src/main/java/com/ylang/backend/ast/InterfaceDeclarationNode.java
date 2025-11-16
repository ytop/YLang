package com.ylang.backend.ast;

/**
 * Node representing a TypeScript interface declaration
 */
public class InterfaceDeclarationNode extends BaseASTNode {
    private final String name;
    private final String genericType; // Can be null
    private final java.util.List<InterfaceMember> members;
    
    public static class InterfaceMember {
        private final String name;
        private final TypeNode type;
        private final boolean optional;
        private final boolean readonly;
        
        public InterfaceMember(String name, TypeNode type, boolean optional, boolean readonly) {
            this.name = name;
            this.type = type;
            this.optional = optional;
            this.readonly = readonly;
        }
        
        public String getName() {
            return name;
        }
        
        public TypeNode getType() {
            return type;
        }
        
        public boolean isOptional() {
            return optional;
        }
        
        public boolean isReadonly() {
            return readonly;
        }
    }
    
    public InterfaceDeclarationNode(String name, String genericType, java.util.List<InterfaceMember> members) {
        this.name = name;
        this.genericType = genericType;
        this.members = new java.util.ArrayList<>(members);
    }
    
    public String getName() {
        return name;
    }
    
    public String getGenericType() {
        return genericType;
    }
    
    public java.util.List<InterfaceMember> getMembers() {
        return new java.util.ArrayList<>(members);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitInterfaceDeclaration(this);
    }
}