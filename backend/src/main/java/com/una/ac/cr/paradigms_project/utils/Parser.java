package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.Token;
import com.una.ac.cr.paradigms_project.types.TokenType;
import com.una.ac.cr.paradigms_project.types.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Set;

public class Parser {
    private List<Token> tokens;
    private int pos;
    private Token currentToken;
    private ASTNode ast;
    private Set<String> definedTypes; 

    public Parser(){
        definedTypes = new HashSet<>(Arrays.asList("int", "float"));
    }

    public void parse(List<Token> tokens){
        this.tokens = tokens;
        this.pos = 0;
        this.currentToken = tokens.get(pos);
        this.ast = new ProgramNode();
        while(currentToken.getType() != TokenType.EOF){
            ((ProgramNode)ast).addStatement(statement());
        }
    }

    public ASTNode getAST(){
        return ast;
    }

    private ASTNode statement(){
        if(currentToken.getType() == TokenType.FUNCTION){
            return functionDeclaration();
        }
        if(currentToken.getType() == TokenType.PUBLIC){
            return classDeclaration();
        }
        if(definedTypes.contains(currentToken.getValue())){
            return variableDeclaration();
        }
        if(currentToken.getType() == TokenType.IDENTIFIER){
            Token next = peek();
            if(next.getType() == TokenType.ASSIGN){
                return assignment();
            } else if(next.getType() == TokenType.DOT){
                return objectFieldAssignment();
            } else {
                throw new RuntimeException("Unexpected token after identifier: " + next.getType());
            }
        }
        if(currentToken.getType() == TokenType.READ){
            return readStatement();
        }
        if(currentToken.getType() == TokenType.PRINT){
            return printStatement();
        }
        if(currentToken.getType() == TokenType.RETURN){
            return returnStatement();
        }
        throw new RuntimeException("Unexpected token: " + currentToken.getValue());
    }

    private ASTNode functionDeclaration(){
        consume(TokenType.FUNCTION);
        String name = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.LPAREN);
        List<Parameter> params = parameterList();
        consume(TokenType.RPAREN);
        consume(TokenType.LBRACE);
        List<ASTNode> body = new ArrayList<>();
        while(currentToken.getType() != TokenType.RBRACE){
            body.add(statement());
        }
        consume(TokenType.RBRACE);
        return new FunctionNode(name, params, body);
    }

    private ASTNode classDeclaration(){
        consume(TokenType.PUBLIC);
        consume(TokenType.CLASS);
        String name = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.LBRACE);
        ClassNode classNode = new ClassNode(name);
        while(currentToken.getType() != TokenType.RBRACE){
            classNode.addField(fieldDeclaration());
        }
        consume(TokenType.RBRACE);
        consume(TokenType.SEMICOLON);
        definedTypes.add(name); // Add class name to known types
        return classNode;
    }

    private FieldNode fieldDeclaration(){
        Token typeToken = currentToken;
        if(typeToken.getType() == TokenType.INT || typeToken.getType() == TokenType.FLOAT || typeToken.getType() == TokenType.INTEGER){
            consume(typeToken.getType());
            String name = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.ASSIGN);
            ExpressionNode expr = expression();
            consume(TokenType.SEMICOLON);
            return new FieldNode(typeToken.getValue(), name, expr);
        }
        throw new RuntimeException("Invalid field declaration");
    }

    private ASTNode variableDeclaration(){
        String type = consume(currentToken.getType()).getValue(); // Consumir el tipo actual
        String varName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.ASSIGN);
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON);
        
        if(isClassType(type)){
            if(!(expr instanceof ObjectInstantiationExpressionNode)){
                throw new RuntimeException("Expected object instantiation expression for type " + type);
            }
            ObjectInstantiationExpressionNode objExpr = (ObjectInstantiationExpressionNode) expr;
            if(!objExpr.getClassName().equals(type)){
                throw new RuntimeException("Mismatched class name in object instantiation");
            }
            return new ObjectInstantiationNode(varName, type);
        } else {
            return new VariableDeclarationNode(varName, expr);
        }
    }    

    private ASTNode assignment(){
        String varName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.ASSIGN);
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON);
        return new VariableDeclarationNode(varName, expr);
    }

    private ASTNode objectFieldAssignment(){
        String objectName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.DOT);
        String fieldName = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.ASSIGN);
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON);
        return new ObjectFieldAssignmentNode(objectName, fieldName, expr);
    }

    private ASTNode readStatement(){
        consume(TokenType.READ);
        consume(TokenType.LPAREN);
        String var = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.DOT);
        String field = consume(TokenType.IDENTIFIER).getValue();
        consume(TokenType.RPAREN);
        consume(TokenType.SEMICOLON);
        return new ReadNode(var + "." + field);
    }

    private ASTNode printStatement(){
        consume(TokenType.PRINT);
        consume(TokenType.LPAREN);
        ExpressionNode expr = expression(); // Parse the expression inside print()
        consume(TokenType.RPAREN);
        consume(TokenType.SEMICOLON);
        return new PrintNode(expr);
    }

    private ASTNode returnStatement(){
        consume(TokenType.RETURN);
        ExpressionNode expr = expression();
        consume(TokenType.SEMICOLON);
        return new ReturnNode(expr);
    }

    private List<Parameter> parameterList(){
        List<Parameter> params = new ArrayList<>();
        if(currentToken.getType() == TokenType.IDENTIFIER){
            String paramName = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.COLON);
            String paramType;
            if(currentToken.getType() == TokenType.INT || currentToken.getType() == TokenType.FLOAT || currentToken.getType() == TokenType.INTEGER){
                paramType = consume(currentToken.getType()).getValue(); 
            } else {
                paramType = consume(TokenType.IDENTIFIER).getValue();
            }
            params.add(new Parameter(paramName, paramType));
            while(currentToken.getType() == TokenType.COMMA){
                consume(TokenType.COMMA);
                paramName = consume(TokenType.IDENTIFIER).getValue();
                consume(TokenType.COLON);
                if(currentToken.getType() == TokenType.INT || currentToken.getType() == TokenType.FLOAT || currentToken.getType() == TokenType.INTEGER){
                    paramType = consume(currentToken.getType()).getValue(); 
                } else {
                    paramType = consume(TokenType.IDENTIFIER).getValue(); 
                }
                params.add(new Parameter(paramName, paramType));
            }
        }
        return params;
    }

    private ExpressionNode expression(){
        return additiveExpression();
    }

    private ExpressionNode additiveExpression(){
        ExpressionNode node = multiplicativeExpression();
        while(currentToken.getType() == TokenType.PLUS || currentToken.getType() == TokenType.MINUS){
            String operator = currentToken.getValue();
            consume(currentToken.getType());
            ExpressionNode right = multiplicativeExpression();
            node = new BinaryExpressionNode(node, operator, right);
        }
        return node;
    }

    private ExpressionNode multiplicativeExpression(){
        ExpressionNode node = primaryExpression();
        while(currentToken.getType() == TokenType.MULTIPLY || currentToken.getType() == TokenType.DIVIDE){
            String operator = currentToken.getValue();
            consume(currentToken.getType());
            ExpressionNode right = primaryExpression();
            node = new BinaryExpressionNode(node, operator, right);
        }
        return node;
    }

    private ExpressionNode primaryExpression(){
        Token token = currentToken;
        if(token.getType() == TokenType.NEW){
            consume(TokenType.NEW);
            String className = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.LPAREN);
            consume(TokenType.RPAREN);
            return new ObjectInstantiationExpressionNode(className);
        }
        if(token.getType() == TokenType.NUMBER){
            consume(TokenType.NUMBER);
            if(token.getValue().contains(".")){
                return new LiteralNode(Float.parseFloat(token.getValue()));
            } else {
                return new LiteralNode(Integer.parseInt(token.getValue()));
            }
        }
        if(token.getType() == TokenType.STRING){
            consume(TokenType.STRING);
            String value = new String();
            while(currentToken.getType() == TokenType.IDENTIFIER){
                String appendingValue = consume(TokenType.IDENTIFIER).getValue();
                value += " " + appendingValue;
            }
            consume(TokenType.STRING);
            return new LiteralNode(value);
        }
        if(token.getType() == TokenType.IDENTIFIER){
            String varName = consume(TokenType.IDENTIFIER).getValue();
            if(currentToken.getType() == TokenType.DOT){
                consume(TokenType.DOT);
                String fieldName = consume(TokenType.IDENTIFIER).getValue();
                return new VariableReferenceNode(varName + "." + fieldName);
            }
            if(currentToken.getType() == TokenType.LPAREN){
                consume(TokenType.LPAREN);
                List<ExpressionNode> args = argumentList();
                consume(TokenType.RPAREN);
                return new FunctionCallExpressionNode(varName, args);
            }
            return new VariableReferenceNode(varName);
        }
        throw new RuntimeException("Unexpected token in expression: " + token.getValue());
    }

    private List<ExpressionNode> argumentList(){
        List<ExpressionNode> args = new ArrayList<>();
        if(currentToken.getType() != TokenType.RPAREN){
            args.add(expression());
            while(currentToken.getType() == TokenType.COMMA){
                consume(TokenType.COMMA);
                args.add(expression());
            }
        }
        return args;
    }

    private Token consume(TokenType type){
        if(currentToken.getType() == type){
            Token token = currentToken;
            pos++;
            if(pos < tokens.size()){
                currentToken = tokens.get(pos);
            }
            return token;
        }
        throw new RuntimeException("Expected token " + type + " but found " + currentToken.getType() + " (" + currentToken.getValue() + ")");
    }

    private Token peek(){
        if(pos + 1 < tokens.size()){
            return tokens.get(pos + 1);
        }
        return new Token(TokenType.EOF, "");
    }

    private boolean isClassType(String type){
        return definedTypes.contains(type) && !(type.equals("int") || type.equals("float"));
    }
}
