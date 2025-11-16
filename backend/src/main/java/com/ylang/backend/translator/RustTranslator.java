package com.ylang.backend.translator;

import com.ylang.backend.ast.*;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.ArrayList;

/**
 * Translator that converts Y language AST to Rust code
 */
@Component
public class RustTranslator implements ASTVisitor<String> {
    
    private int indentLevel = 0;
    private static final String INDENT = "    ";
    private final List<String> imports = new ArrayList<>();
    
    /**
     * Translate Y language AST to Rust code
     * @param ast The Y language AST
     * @return Generated Rust code
     */
    public String translate(ProgramNode ast) {
        indentLevel = 0;
        imports.clear();
        
        StringBuilder result = new StringBuilder();
        
        // Add Rust header comment
        result.append("// Generated Rust code from Y language\n");
        result.append("// Y Language Translator - Rust Target\n\n");
        
        // Add common imports
        result.append("use std::collections::HashMap;\n");
        result.append("use std::vec::Vec;\n\n");
        
        // Visit all statements
        result.append(ast.accept(this));
        
        return result.toString();
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
        result.append("pub fn ");
        result.append(convertToSnakeCase(node.getName()));
        result.append("(");
        
        // Add parameters
        List<ParameterNode> parameters = node.getParameters();
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) result.append(", ");
            ParameterNode param = parameters.get(i);
            result.append(convertToSnakeCase(param.getName()));
            result.append(": ");
            result.append(translateType(param.getType()));
            
            // Handle optional parameters based on Yummy comments
            if (param.getYummyComment() != null && param.getYummyComment().contains("optional")) {
                result.append(" = None");
            }
        }
        
        result.append(")");
        
        // Add return type
        result.append(" -> ");
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
        result.append("let ");
        result.append(convertToSnakeCase(node.getName()));
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
        result.append(convertToSnakeCase(node.getVariableName()));
        result.append(" = ");
        result.append(node.getValue().accept(this));
        result.append(";");
        
        return result.toString();
    }
    
    @Override
    public String visitIfStatement(IfStatementNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(indent());
        result.append("if ");
        result.append(node.getCondition().accept(this));
        result.append(" {\n");
        
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
            result.append("for ");
            result.append(convertToSnakeCase(node.getVariableName()));
            result.append(" in ");
            result.append(node.getIterable().accept(this));
            result.append(" {\n");
            
            increaseIndent();
            result.append(node.getBody().accept(this));
            decreaseIndent();
            
            result.append(indent()).append("}");
        } else {
            // While loop
            result.append(indent());
            result.append("while ");
            result.append(node.getCondition().accept(this));
            result.append(" {\n");
            
            increaseIndent();
            result.append(node.getBody().accept(this));
            
            // Add increment statement if specified
            if (node.getIncrementVar() != null) {
                result.append(indent()).append(convertToSnakeCase(node.getIncrementVar())).append(" += 1;\n");
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
                return "String::from(\"" + escapeString((String) node.getValue()) + "\")";
            case NUMBER:
                if (node.getValue() instanceof Double) {
                    return node.getValue().toString();
                } else {
                    return node.getValue().toString() + "i64";
                }
            case BOOLEAN:
                return node.getValue().toString();
            case EMPTY_LIST:
                return "Vec::new()";
            case EMPTY_MAP:
                return "HashMap::new()";
            default:
                return "()";
        }
    }
    
    @Override
    public String visitIdentifier(IdentifierNode node) {
        return convertToSnakeCase(node.getName());
    }
    
    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        StringBuilder result = new StringBuilder();
        
        result.append(convertToSnakeCase(node.getFunctionName()));
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
        // Rust doesn't have ternary operator, use if expression
        StringBuilder result = new StringBuilder();
        
        result.append("if ");
        result.append(node.getCondition().accept(this));
        result.append(" { ");
        result.append(node.getThenExpression().accept(this));
        result.append(" } else { ");
        result.append(node.getElseExpression().accept(this));
        result.append(" }");
        
        return result.toString();
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
    
    private String convertToSnakeCase(String name) {
        // Convert camelCase or kebab-case to snake_case
        return name.replaceAll("([a-z])([A-Z])", "$1_$2")
                   .replaceAll("-", "_")
                   .toLowerCase();
    }
    
    private String translateType(TypeNode type) {
        if (type == null) return "()";
        
        switch (type.getKind()) {
            case STRING:
                return "String";
            case NUMBER:
                return "i64";
            case BOOLEAN:
                return "bool";
            case NOTHING:
                return "()";
            case ANY:
                return "Box<dyn std::any::Any>";
            case LIST:
                return "Vec<" + translateType(type.getElementType()) + ">";
            case MAP:
                return "HashMap<" + translateType(type.getKeyType()) + ", " + translateType(type.getValueType()) + ">";
            case EITHER:
                return "Result<" + translateType(type.getLeftType()) + ", " + translateType(type.getRightType()) + ">";
            case FUNCTION:
                return "fn(" + translateType(type.getParamType()) + ") -> " + translateType(type.getReturnType());
            case REFERENCE:
                return "&" + translateType(type.getElementType());
            case BOX:
                return "Box<" + translateType(type.getElementType()) + ">";
            case IDENTIFIER:
                return convertToSnakeCase(type.getName());
            default:
                return "()";
        }
    }
    
    private String translateOperator(BinaryExpressionNode.Operator op) {
        switch (op) {
            case PLUS: return "+";
            case MINUS: return "-";
            case TIMES: return "*";
            case DIVIDED_BY: return "/";
            case EQUALS: return "==";
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
        result.append(indent()).append("match std::panic::catch_unwind(|| {\n");
        
        // Increase indent for try block
        increaseIndent();
        result.append(node.getTryBlock().accept(this));
        decreaseIndent();
        
        result.append(indent()).append("}) {\n");
        result.append(indent()).append("    Ok(result) => result,\n");
        result.append(indent()).append("    Err(_) => {\n");
        
        // Handle catch blocks
        for (TryStatementNode.CatchClause catchClause : node.getCatchClauses()) {
            increaseIndent();
            result.append(indent()).append("// Error handling: ").append(catchClause.getVariableName()).append("\n");
            result.append(catchClause.getCatchBlock().accept(this));
            decreaseIndent();
        }
        
        result.append(indent()).append("    }\n");
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitMatchStatement(MatchStatementNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("match ").append(node.getExpression().accept(this)).append(" {\n");
        
        for (MatchStatementNode.MatchCase matchCase : node.getCases()) {
            increaseIndent();
            
            // Handle different pattern types
            String pattern = matchCase.getPattern();
            if (matchCase.getType() != null) {
                if (matchCase.getVariableName() != null) {
                    // Pattern with variable binding
                    result.append(indent()).append(pattern).append(" { ").append(matchCase.getVariableName()).append(" } => {\n");
                } else {
                    // Simple type pattern
                    result.append(indent()).append(pattern).append(" => {\n");
                }
            } else {
                // Simple identifier pattern
                result.append(indent()).append(pattern).append(" => {\n");
            }
            
            // Add case block
            increaseIndent();
            result.append(matchCase.getBlock().accept(this));
            decreaseIndent();
            result.append(indent()).append("},\n");
            decreaseIndent();
        }
        
        result.append(indent()).append("}\n");
        return result.toString();
    }
    
    
    
    @Override
    public String visitModuleDeclaration(ModuleDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("pub mod ").append(node.getName()).append(" {\n");
        
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
        result.append(indent()).append("pub trait ").append(node.getName()).append(" {\n");
        
        // Increase indent for trait contents
        increaseIndent();
        
        // Add function signatures
        for (TraitDeclarationNode.FunctionSignatureNode signature : node.getFunctionSignatures()) {
            result.append(indent()).append("fn ").append(signature.getName()).append("(");
            
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
                result.append(" -> ").append(translateType(signature.getReturnType()));
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
        
        result.append(indent()).append("pub struct ").append(node.getName()).append(genericParam);
        
        // Handle trait implementation
        if (node.getImplementsTrait() != null) {
            result.append(" /* implements ").append(node.getImplementsTrait()).append(" */");
        }
        
        result.append(" {\n");
        
        // Increase indent for structure contents
        increaseIndent();
        
        // Add structure members
        for (ASTNode member : node.getMembers()) {
            result.append(member.accept(this));
            result.append("\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitImportStatement(ImportStatementNode node) {
        return "use " + node.getModuleName().replace("::", "::") + ";\n";
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
        result.append("vec![");
        
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
        result.append(indent()).append(INDENT).append("let mut m = HashMap::new();\n");
        
        List<MapExpressionNode.KeyValuePair> entries = node.getPairs();
        for (int i = 0; i < entries.size(); i++) {
            result.append(indent()).append(INDENT)
                  .append("m.insert(")
                  .append(entries.get(i).getKey().accept(this))
                  .append(", ")
                  .append(entries.get(i).getValue().accept(this))
                  .append(");\n");
        }
        
        result.append(indent()).append(INDENT).append("m\n");
        result.append(indent()).append("}");
        return result.toString();
    }
    
    @Override
    public String visitTypeCast(TypeCastNode node) {
        String expression = node.getExpression().accept(this);
        
        switch (node.getCastType()) {
            case TO_STRING:
                return expression + ".to_string()";
            case TO_NUMBER:
                return expression + ".parse::<f64>().unwrap()";
            case TO_BOOLEAN:
                return "!" + expression + ".is_empty()";
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
        
        // Handle generic type parameter
        String genericParam = "";
        if (node.getGenericType() != null) {
            genericParam = "<" + node.getGenericType() + ">";
        }
        
        result.append(indent()).append("pub enum ").append(node.getName()).append(genericParam).append(" {\n");
        
        // Increase indent for enum variants
        increaseIndent();
        
        // Add enum variants
        for (EnumDeclarationNode.EnumVariant variant : node.getVariants()) {
            result.append(indent()).append(variant.getName());
            
            // Add variant fields if any
            java.util.List<TypeNode> fieldTypes = variant.getFieldTypes();
            if (!fieldTypes.isEmpty()) {
                result.append("(");
                for (int i = 0; i < fieldTypes.size(); i++) {
                    if (i > 0) result.append(", ");
                    result.append(translateType(fieldTypes.get(i)));
                }
                result.append(")");
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
        
        result.append(indent()).append("impl");
        
        // Handle trait implementation
        if (node.getTraitName() != null) {
            result.append(" ").append(node.getTraitName()).append(" for");
        }
        
        result.append(" ").append(node.getTargetType()).append(" {\n");
        
        // Increase indent for implementation methods
        increaseIndent();
        
        // Add methods
        for (ASTNode method : node.getMethods()) {
            result.append(method.accept(this));
            result.append("\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitLifetime(LifetimeNode node) {
        return "'" + node.getName();
    }
    
    @Override
    public String visitInterfaceDeclaration(InterfaceDeclarationNode node) {
        // Convert TypeScript interface to Rust trait
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("// TypeScript interface converted to Rust trait\n");
        result.append(indent()).append("pub trait ").append(node.getName());
        
        // Handle generic type parameter
        if (node.getGenericType() != null) {
            result.append("<").append(node.getGenericType()).append(">");
        }
        
        result.append(" {\n");
        
        // Increase indent for trait contents
        increaseIndent();
        
        // Convert interface members to trait methods
        for (InterfaceDeclarationNode.InterfaceMember member : node.getMembers()) {
            result.append(indent()).append("fn ").append(member.getName()).append("(&self)");
            if (member.isOptional()) {
                result.append(" -> Option<").append(translateType(member.getType())).append(">");
            } else {
                result.append(" -> ").append(translateType(member.getType()));
            }
            result.append(";\n");
        }
        
        decreaseIndent();
        result.append(indent()).append("}\n");
        
        return result.toString();
    }
    
    @Override
    public String visitTypeAliasDeclaration(TypeAliasDeclarationNode node) {
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("type ").append(node.getName());
        
        // Handle generic type parameter
        if (node.getGenericType() != null) {
            result.append("<").append(node.getGenericType()).append(">");
        }
        
        result.append(" = ").append(translateType(node.getAliasedType())).append(";\n");
        
        return result.toString();
    }
    
    @Override
    public String visitDecorator(DecoratorNode node) {
        // Rust attributes are similar to TypeScript decorators
        StringBuilder result = new StringBuilder();
        result.append(indent()).append("#[").append(node.getName());
        
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
        
        result.append("]\n");
        
        // Add the decorated target
        result.append(node.getTarget().accept(this));
        
        return result.toString();
    }
}