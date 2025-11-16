package com.ylang.backend.ast;

/**
 * Visitor interface for traversing AST nodes
 * @param <T> The return type of the visitor
 */
public interface ASTVisitor<T> {
    T visitProgram(ProgramNode node);
    T visitFunctionDeclaration(FunctionDeclarationNode node);
    T visitVariableDeclaration(VariableDeclarationNode node);
    T visitAssignment(AssignmentNode node);
    T visitIfStatement(IfStatementNode node);
    T visitLoopStatement(LoopStatementNode node);
    T visitReturnStatement(ReturnStatementNode node);
    T visitTryStatement(TryStatementNode node);
    T visitMatchStatement(MatchStatementNode node);
    T visitModuleDeclaration(ModuleDeclarationNode node);
    T visitTraitDeclaration(TraitDeclarationNode node);
    T visitStructureDeclaration(StructureDeclarationNode node);
    T visitImportStatement(ImportStatementNode node);
    T visitExpressionStatement(ExpressionStatementNode node);
    
    // Expression visitors
    T visitLiteral(LiteralNode node);
    T visitIdentifier(IdentifierNode node);
    T visitFunctionCall(FunctionCallNode node);
    T visitBinaryExpression(BinaryExpressionNode node);
    T visitUnaryExpression(UnaryExpressionNode node);
    T visitConditionalExpression(ConditionalExpressionNode node);
    T visitMemberAccess(MemberAccessNode node);
    T visitListExpression(ListExpressionNode node);
    T visitMapExpression(MapExpressionNode node);
    T visitTypeCast(TypeCastNode node);
    T visitParenthesizedExpression(ParenthesizedExpressionNode node);
}