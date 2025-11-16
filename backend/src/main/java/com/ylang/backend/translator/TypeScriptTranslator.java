package com.ylang.backend.translator;

import com.ylang.backend.ast.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

/**
 * Translator that converts Y language AST to TypeScript code
 */
@Component
public class TypeScriptTranslator implements ASTVisitor<String> {
    
    private int indentLevel = 0;
    private static final String INDENT = "    ";
    
    /**
     * Translate Y language AST to TypeScript code
     * @param ast The Y language AST
     * @return Generated TypeScript code
     */
    public String translate(ProgramNode ast) {
        indentLevel = 0;
        return ast.accept(this);
    }
    
    private String indent() {
        return INDENT.repeat(indentLevel);
    }
    
    private String increaseIndent() {
        indentLevel++;
        return indent();
    }
    
    private String decreaseIndent() {
        indentLevel = Math.max(0, indentLevel - 1);
        return indent();
    }
    
    @Override
    public String visitProgram(ProgramNode node) {
        StringBuilder result = new StringBuilder();
        
        // Add TypeScript header comment
        result.append("// Generated TypeScript code from Y language\n");
        result.append("// Y Language Translator - TypeScript Target\n\n");
        
        // Visit all statements
        for (ASTNode statement : node.getStatements()) {
            result.append(statement.accept(this));
            result.append("\n");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitFunctionDeclaration(FunctionDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        // Add function signature
        result.append(indent());
        if (node.isAsync()) {
            result.append("async ");
        }
        result.append("function ");
        result.append(convertToCamelCase(node.getName()));
        result.append("(");
        
        // Add parameters
        List<ParameterNode> parameters = node.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) result.append(", ");
            ParameterNode param = parameters.get(i);
            result.append(convertToCamelCase(param.getName()));
            result.append(": ");
            result.append(translateType(param.getType()));
            
            // Handle optional parameters based on Yummy comments
            if (param.getYummyComment() != null && param.getYummyComment().contains("optional")) {
                result.append("?");
            }
        }
        
        result.append(")");
        
        // Add return type
        result.append(": ");
        result.append(translateType(node.getReturnType()));
        result.append(" {\n");
        
        // Add function body
        increaseIndent();
        result.append(node.getBody().accept(this));
        decreaseIndent();
        
        result.append(indent()).append("}");
        
        return result.toString();
    }
    
    @Override
    public String visitVariableDeclaration(VariableDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append("const ");
        result.append(convertToCamelCase(node.getName()));
        result.append(": ");
        result.append(translateType(node.getType()));
        result.append(" = ");
        result.append(node.getInitializer().accept(this));
        result.append(";");
        
        return result.toString();
    }
    
    @Override
    public String visitAssignment(AssignmentNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append(convertToCamelCase(node.getVariableName()));
        result.append(" = ");
        result.append(node.getValue().accept(this));
        result.append(";");
        
        return result.toString();
    }
    
    @Override
    public String visitIfStatement(IfStatementNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append("if (");
        result.append(node.getCondition().accept(this));
        result.append(") {\n");
        
        increaseIndent();
        result.append(node.getThenBlock().accept(this));
        decreaseIndent();
        
        result.append(indent()).append("}");
        
        if (node.hasElseBlock()) {
            result.append(" else {\n");
            increaseIndent();
            result.append(node.getElseBlock().accept(this));
            decreaseIndent();
            result.append(indent()).append("}");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitLoopStatement(LoopStatementNode node) {
        StringBuilder result = new StringBuilder();
        
        if (node.getLoopType() == LoopStatementNode.LoopType.FOR_EACH) {
            result.append(indent());
            result.append("for (const ");
            result.append(convertToCamelCase(node.getVariableName()));
            result.append(" of ");
            result.append(node.getIterable().accept(this));
            result.append(") {\n");
            
            increaseIndent();
            result.append(node.getBody().accept(this));
            decreaseIndent();
            
            result.append(indent()).append("}");
        } else {
            // While loop - need to handle the increment logic
            result.append(indent());
            result.append("while (");
            result.append(node.getCondition().accept(this));
            result.append(") {\n");
            
            increaseIndent();
            result.append(node.getBody().accept(this));
            
            // Add increment statement if specified
            if (node.getIncrementVar() != null) {
                result.append(indent()).append(node.getIncrementVar()).append("++;\n");
            }
            
            decreaseIndent();
            result.append(indent()).append("}");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitReturnStatement(ReturnStatementNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append("return");
        
        if (node.hasValue()) {
            result.append(" ");
            result.append(node.getValue().accept(this));
        }
        
        result.append(";");
        
        return result.toString();
    }
    
    @Override
    public String visitBlock(BlockNode node) {
        StringBuilder result = new StringBuilder();
        
        for (ASTNode statement : node.getStatements()) {
            result.append(statement.accept(this));
            result.append("\n");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitLiteral(LiteralNode node) {
        switch (node.getLiteralType()) {
            case STRING:
                return "\"" + escapeString((String) node.getValue()) + "\"";
            case NUMBER:
                return node.getValue().toString();
            case BOOLEAN:
                return node.getValue().toString();
            case EMPTY_LIST:
                return "[]";
            case EMPTY_MAP:
                return "{}";
            default:
                return "null";
        }
    }
    
    @Override
    public String visitIdentifier(IdentifierNode node) {
        return convertToCamelCase(node.getName());
    }
    
    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(convertToCamelCase(node.getFunctionName()));
        result.append("(");
        
        List<ExpressionNode> arguments = node.getArguments();
        for (int i = 0; i < arguments.size(); i++) {
            if (i > 0) result.append(", ");
            result.append(arguments.get(i).accept(this));
        }
        
        result.append(")");
        
        return result.toString();
    }
    
    @Override
    public String visitBinaryExpression(BinaryExpressionNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append("(");
        result.append(node.getLeft().accept(this));
        result.append(" ");
        result.append(translateOperator(node.getOperator()));
        result.append(" ");
        result.append(node.getRight().accept(this));
        result.append(")");
        
        return result.toString();
    }
    
    @Override
    public String visitUnaryExpression(UnaryExpressionNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(translateUnaryOperator(node.getOperator()));
        result.append(node.getOperand().accept(this));
        
        return result.toString();
    }
    
    @Override
    public String visitConditionalExpression(ConditionalExpressionNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append("(");
        result.append(node.getCondition().accept(this));
        result.append(" ? ");
        result.append(node.getThenExpression().accept(this));
        result.append(" : ");
        result.append(node.getElseExpression().accept(this));
        result.append(")");
        
        return result.toString();
    }
    
    @Override
    public String visitBlock(BlockNode node) {
        // This is handled in visitFunctionDeclaration and other places
        return "";
    }
    
    @Override
    public String visitExpressionStatement(ExpressionStatementNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append(node.getExpression().accept(this));
        result.append(";");
        
        return result.toString();
    }
    
    // Helper methods
    
    private String convertToCamelCase(String name) {
        // Convert snake_case or kebab-case to camelCase
        String[] parts = name.split("[_-]");
        StringBuilder result = new StringBuilder(parts[0]);
        
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].isEmpty()) {
                result.append(Character.toUpperCase(parts[i].charAt(0)));
                if (parts[i].length() > 1) {
                    result.append(parts[i].substring(1));
                }
            }
        }
        
        return result.toString();
    }
    
    private String translateType(TypeNode type) {
        if (type == null) return "any";
        
        switch (type.getKind()) {
            case STRING:
                return "string";
            case NUMBER:
                return "number";
            case BOOLEAN:
                return "boolean";
            case NOTHING:
                return "void";
            case ANY:
                return "any";
            case LIST:
                return translateType(type.getElementType()) + "[]";
            case MAP:
                return "Record<" + translateType(type.getKeyType()) + ", " + translateType(type.getValueType()) + ">";
            case EITHER:
                return translateType(type.getLeftType()) + " | " + translateType(type.getRightType());
            case FUNCTION:
                return "(" + translateType(type.getParamType()) + ") => " + translateType(type.getReturnType());
            case REFERENCE:
                return translateType(type.getElementType());
            case BOX:
                return translateType(type.getElementType());
            case IDENTIFIER:
                return convertToCamelCase(type.getName());
            default:
                return "any";
        }
    }
    
    private String translateOperator(BinaryExpressionNode.Operator op) {
        switch (op) {
            case PLUS: return "+";
            case MINUS: return "-";
            case TIMES: return "*";
            case DIVIDED_BY: return "/";
            case EQUALS: return "===";
            case GREATER_THAN: return ">";
            case LESS_THAN: return "<";
            case AND: return "&&";
            case OR: return "||";
            case MODULO: return "%";
            default: return "+";
        }
    }
    
    private String translateUnaryOperator(UnaryExpressionNode.Operator op) {
        switch (op) {
            case NOT: return "!";
            case MINUS: return "-";
            default: return "";
        }
    }
    
    private String escapeString(String str) {
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
    
    // Unimplemented visitor methods - these would be implemented as needed
    
    @Override
    public String visitTryStatement(TryStatementNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("try {\n");
        
        // Increase indent for try block
        increaseIndent();
        result.append(node.getTryBlock().accept(this));
        decreaseIndent();
        
        // Handle catch blocks
        for (TryStatementNode.CatchClause catchClause : node.getCatchClauses()) {
            result.append(indent()).append("catch (").append(catchClause.getVariableName());
            if (catchClause.getExceptionType() != null) {
                result.append(": ").append(translateType(catchClause.getExceptionType()));
            }
            result.append(") {\n");
            
            increaseIndent();
            result.append(catchClause.getCatchBlock().accept(this));
            decreaseIndent();
            result.append(indent()).append("}\n");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitMatchStatement(MatchStatementNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("switch (").append(node.getExpression().accept(this)).append(") {\n");
        
        for (MatchStatementNode.MatchCase matchCase : node.getCases()) {
            increaseIndent();
            
            // Handle different pattern types
            String pattern = matchCase.getPattern();
            if (matchCase.getType() != null) {
                if (matchCase.getVariableName() != null) {
                    // Pattern with variable binding - use case with destructuring
                    result.append(indent()).append("case ").append(pattern).append(": {\n");
                    increaseIndent();
                    result.append(indent()).append("const ").append(matchCase.getVariableName()).append(" = ").append(pattern).append(";\n");
                } else {
                    // Simple type pattern
                    result.append(indent()).append("case ").append(pattern).append(": {\n");
                    increaseIndent();
                }
            } else {
                // Simple identifier pattern
                result.append(indent()).append("case ").append(pattern).append(": {\n");
                increaseIndent();
            }
            
            // Add case block
            result.append(matchCase.getBlock().accept(this));
            result.append(indent()).append("break;\n");
            
            decreaseIndent();
            result.append(indent()).append("}\n");
            decreaseIndent();
        }
        
        result.append(indent()).append("}\n");
        return result.toString();
    }
    
    @Override
    public String visitLoopStatement(LoopStatementNode node) {
        // This is already implemented above, but needed for interface
        return "";
    }
    
    @Override
    public String visitModuleDeclaration(ModuleDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("namespace ").append(node.getName()).append(" {\n");
        
        // Increase indent for module contents
        increaseIndent();
        
        // Add module statements
        for (ASTNode statement : node.getStatements()) {
            result.append(statement.accept(this));
            result.append("\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitTraitDeclaration(TraitDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("interface ").append(node.getName()).append(" {\n");
        
        // Increase indent for interface contents
        increaseIndent();
        
        // Add function signatures
        for (FunctionSignatureNode signature : node.getFunctionSignatures()) {
            result.append(indent()).append(signature.getName()).append("(");
            
            // Add parameters
            List<ParameterNode> parameters = signature.getParameters();
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) result.append(", ");
                ParameterNode param = parameters.get(i);
                result.append(param.getName()).append(": ").append(translateType(param.getType()));
            }
            
            result.append(")");
            
            // Add return type
            if (signature.getReturnType() != null && !signature.getReturnType().isNothingType()) {
                result.append(": ").append(translateType(signature.getReturnType()));
            }
            
            result.append(";\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitStructureDeclaration(StructureDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        // Handle generic type parameter
        String genericParam = "";
        if (node.getGenericType() != null) {
            genericParam = "<" + node.getGenericType() + ">";
        }
        
        result.append(indent()).append("class ").append(node.getName()).append(genericParam);
        
        // Handle interface implementation
        if (node.getImplementsTrait() != null) {
            result.append(" implements ").append(node.getImplementsTrait());
        }
        
        result.append(" {\n");
        
        // Increase indent for class contents
        increaseIndent();
        
        // Add structure members
        for (ASTNode member : node.getMembers()) {
            result.append(statement.accept(this));
            result.append("\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitImportStatement(ImportStatementNode node) {
        return "import { " + node.getModuleName() + " } from './" + node.getModuleName() + "';\n";
    }
    
    @Override
    public String visitAssignment(AssignmentNode node) {
        // This is already implemented above, but needed for interface
        return "";
    }
    
    @Override
    public String visitIfStatement(IfStatementNode node) {
        // This is already implemented above, but needed for interface
        return "";
    }
    
    @Override
    public String visitReturnStatement(ReturnStatementNode node) {
        // This is already implemented above, but needed for interface
        return "";
    }
    
    @Override
    public String visitMemberAccess(MemberAccessNode node) {
        String object = node.getObject().accept(this);
        String member = node.getMember().accept(this);
        
        if (node.isArrayAccess()) {
            return object + "[" + member + "]";
        } else {
            return object + "." + member;
        }
    }
    
    @Override
    public String visitListExpression(ListExpressionNode node) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        
        List<ExpressionNode> elements = node.getElements();
        for (int i = 0; i < elements.size(); i++) {
            if (i > 0) result.append(", ");
            result.append(elements.get(i).accept(this));
        }
        
        result.append("]");
        return result.toString();
    }
    
    @Override
    public String visitMapExpression(MapExpressionNode node) {
        StringBuilder result = new StringBuilder();
        result.append("{\n");
        
        List<MapEntryNode> entries = node.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            if (i > 0) result.append(",\n");
            result.append(indent()).append(INDENT);
            
            // Handle key - if it's a string, don't quote it again
            String key = entries.get(i).getKey().accept(this);
            if (key.startsWith("\"") && key.endsWith("\"")) {
                result.append(key);
            } else {
                result.append("[").append(key).append("]");
            }
            
            result.append(": ").append(entries.get(i).getValue().accept(this));
        }
        
        result.append("\n").append(indent()).append("}");
        return result.toString();
    }
    
    @Override
    public String visitTypeCast(TypeCastNode node) {
        String expression = node.getExpression().accept(this);
        
        switch (node.getCastType()) {
            case TO_STRING:
                return expression + ".toString()";
            case TO_NUMBER:
                return "Number(" + expression + ")";
            case TO_BOOLEAN:
                return "Boolean(" + expression + ")";
            default:
                return expression;
        }
    }
    
    @Override
    public String visitParenthesizedExpression(ParenthesizedExpressionNode node) {
        return "(" + node.getExpression().accept(this) + ")";
    }
    
    @Override
    public String visitEnumDeclaration(EnumDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent()).append("enum ").append(node.getName()).append(" {\n");
        
        // Increase indent for enum variants
        increaseIndent();
        
        // Add enum variants
        for (EnumDeclarationNode.EnumVariant variant : node.getVariants()) {
            result.append(indent()).append(variant.getName());
            
            // Add variant fields if any
            java.util.List<TypeNode> fieldTypes = variant.getFieldTypes();
            if (!fieldTypes.isEmpty()) {
                result.append(" = ");
                // For TypeScript, we'll use numeric enums for simple variants
                result.append("\"").append(variant.getName()).append("\"");
            }
            
            result.append(",\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitImplementation(ImplementationNode node) {
        StringBuilder result = new StringBuilder();
        
        // For TypeScript, we'll create a class or add methods to existing class
        result.append(indent()).append("// Implementation for ").append(node.getTargetType());
        if (node.getTraitName() != null) {
            result.append(" implementing ").append(node.getTraitName());
        }
        result.append("\n");
        
        // Add methods as standalone functions or class methods
        for (ASTNode method : node.getMethods()) {
            result.append(method.accept(this));
            result.append("\n");
        }
        
        return result.toString();
    }
    
    @Override
    public String visitLifetime(LifetimeNode node) {
        // Lifetimes are not used in TypeScript, return empty string
        return "";
    }
    
    @Override
    public String visitInterfaceDeclaration(InterfaceDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        // Handle generic type parameter
        String genericParam = "";
        if (node.getGenericType() != null) {
            genericParam = "<" + node.getGenericType() + ">";
        }
        
        result.append(indent()).append("interface ").append(node.getName()).append(genericParam).append(" {\n");
        
        // Increase indent for interface contents
        increaseIndent();
        
        // Add interface members
        for (InterfaceDeclarationNode.InterfaceMember member : node.getMembers()) {
            result.append(indent());
            
            if (member.isReadonly()) {
                result.append("readonly ");
            }
            
            result.append(member.getName());
            
            if (member.isOptional()) {
                result.append("?");
            }
            
            result.append(": ").append(translateType(member.getType())).append(";\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitTypeAliasDeclaration(TypeAliasDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        
        // Handle generic type parameter
        String genericParam = "";
        if (node.getGenericType() != null) {
            genericParam = "<" + node.getGenericType() + ">";
        }
        
        result.append(indent()).append("type ").append(node.getName()).append(genericParam);
        result.append(" = ").append(translateType(node.getAliasedType())).append(";\n");
        
        return result.toString();
    }
    
    @Override
    public String visitDecorator(DecoratorNode node) {
        StringBuilder result = new StringBuilder();
        
        // Add decorator
        result.append("@").append(node.getName());
        
        // Add decorator arguments if any
        if (!node.getArguments().isEmpty()) {
            result.append("(");
            java.util.List<ExpressionNode> args = node.getArguments();
            for (int i = 0; i < args.size(); i++) {
                if (i > 0) result.append(", ");
                result.append(args.get(i).accept(this));
            }
            result.append(")");
        }
        
        result.append("\n");
        
        // Add the decorated target
        result.append(node.getTarget().accept(this));
        
        return result.toString();
    }
}