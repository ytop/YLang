package com.ylang.backend.ast;

/**
 * Node representing a type in the Y language
 */
public class TypeNode extends BaseASTNode {
    private final TypeKind kind;
    private final String name;
    private final TypeNode elementType;
    private final TypeNode keyType;
    private final TypeNode valueType;
    private final TypeNode leftType;
    private final TypeNode rightType;
    private final TypeNode paramType;
    private final TypeNode returnType;
    
    public enum TypeKind {
        STRING, NUMBER, BOOLEAN, NOTHING, ANY, LIST, MAP, EITHER, 
        FUNCTION, REFERENCE, BOX, IDENTIFIER, GENERIC
    }
    
    // Basic types
    public TypeNode(TypeKind kind) {
        this(kind, null, null, null, null, null, null, null, null);
    }
    
    // Named types (identifiers)
    public TypeNode(String name) {
        this(TypeKind.IDENTIFIER, name, null, null, null, null, null, null, null);
    }
    
    // Container types (List, Map, Either, Reference, Box)
    public TypeNode(TypeKind kind, TypeNode elementType) {
        this(kind, null, elementType, null, null, null, null, null, null);
    }
    
    // Map type
    public TypeNode(TypeKind kind, TypeNode keyType, TypeNode valueType) {
        this(kind, null, null, keyType, valueType, null, null, null, null);
    }
    
    // Either type
    public TypeNode(TypeKind kind, TypeNode leftType, TypeNode rightType, boolean isEither) {
        this(kind, null, null, null, null, leftType, rightType, null, null);
    }
    
    // Function type
    public TypeNode(TypeKind kind, TypeNode paramType, TypeNode returnType, boolean isFunction) {
        this(kind, null, null, null, null, null, null, paramType, returnType);
    }
    
    private TypeNode(TypeKind kind, String name, TypeNode elementType, TypeNode keyType, 
                    TypeNode valueType, TypeNode leftType, TypeNode rightType, 
                    TypeNode paramType, TypeNode returnType) {
        this.kind = kind;
        this.name = name;
        this.elementType = elementType;
        this.keyType = keyType;
        this.valueType = valueType;
        this.leftType = leftType;
        this.rightType = rightType;
        this.paramType = paramType;
        this.returnType = returnType;
    }
    
    public TypeKind getKind() {
        return kind;
    }
    
    public String getName() {
        return name;
    }
    
    public TypeNode getElementType() {
        return elementType;
    }
    
    public TypeNode getKeyType() {
        return keyType;
    }
    
    public TypeNode getValueType() {
        return valueType;
    }
    
    public TypeNode getLeftType() {
        return leftType;
    }
    
    public TypeNode getRightType() {
        return rightType;
    }
    
    public TypeNode getParamType() {
        return paramType;
    }
    
    public TypeNode getReturnType() {
        return returnType;
    }
    
    // Factory methods for common types
    public static TypeNode stringType() {
        return new TypeNode(TypeKind.STRING);
    }
    
    public static TypeNode numberType() {
        return new TypeNode(TypeKind.NUMBER);
    }
    
    public static TypeNode booleanType() {
        return new TypeNode(TypeKind.BOOLEAN);
    }
    
    public static TypeNode nothingType() {
        return new TypeNode(TypeKind.NOTHING);
    }
    
    public static TypeNode anyType() {
        return new TypeNode(TypeKind.ANY);
    }
    
    public static TypeNode listType(TypeNode elementType) {
        return new TypeNode(TypeKind.LIST, elementType);
    }
    
    public static TypeNode mapType(TypeNode keyType, TypeNode valueType) {
        return new TypeNode(TypeKind.MAP, keyType, valueType);
    }
    
    public static TypeNode eitherType(TypeNode leftType, TypeNode rightType) {
        return new TypeNode(TypeKind.EITHER, leftType, rightType, true);
    }
    
    public static TypeNode functionType(TypeNode paramType, TypeNode returnType) {
        return new TypeNode(TypeKind.FUNCTION, paramType, returnType, true);
    }
    
    public static TypeNode referenceType(TypeNode elementType) {
        return new TypeNode(TypeKind.REFERENCE, elementType);
    }
    
    public static TypeNode boxType(TypeNode elementType) {
        return new TypeNode(TypeKind.BOX, elementType);
    }
    
    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        // Type nodes are typically visited as part of other nodes
        return null;
    }
    
    @Override
    public String toString() {
        switch (kind) {
            case STRING: return "string";
            case NUMBER: return "number";
            case BOOLEAN: return "boolean";
            case NOTHING: return "nothing";
            case ANY: return "any";
            case LIST: return "list of " + elementType;
            case MAP: return "map of " + keyType + " to " + valueType;
            case EITHER: return "either " + leftType + " or " + rightType;
            case FUNCTION: return "function that takes " + paramType + " and returns " + returnType;
            case REFERENCE: return "reference to " + elementType;
            case BOX: return "box of " + elementType;
            case IDENTIFIER: return name;
            case GENERIC: return name + " (generic)";
            default: return "unknown";
        }
    }
}