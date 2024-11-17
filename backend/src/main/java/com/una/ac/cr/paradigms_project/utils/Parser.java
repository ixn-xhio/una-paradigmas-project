//This file serialize the Expressions for the AST from the tokens created in the Lexer file
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
            System.out.println(currentToken.getValue());
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
        if(currentToken.getType() == TokenType.IF){
            return ifStatement();
        }
        // Nueva verificación para forStatement
        if(currentToken.getType() == TokenType.FOR){
            return forStatement();
        }
        // Nueva verificación para forStatement
        if(currentToken.getType() == TokenType.DO){
            return whileStatement();
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
    
    private FieldNode fieldDeclaration() {
        Token typeToken = currentToken;
    
        // Handle primitive types like int, float, or integer
        if (typeToken.getType() == TokenType.INT || typeToken.getType() == TokenType.FLOAT || typeToken.getType() == TokenType.INTEGER || typeToken.getType() == TokenType.BOOL) {
            consume(typeToken.getType());
            String name = consume(TokenType.IDENTIFIER).getValue(); // Variable name
            consume(TokenType.ASSIGN); // Consume '='
            ExpressionNode expr = expression(); // Parse the assigned value
            consume(TokenType.SEMICOLON); // Consume ';'
            return new FieldNode(typeToken.getValue(), name, expr);
        }
    
        // Handle array types like Array<int>
        if (typeToken.getType() == TokenType.IDENTIFIER && typeToken.getValue().equals("Array")) {
            consume(TokenType.IDENTIFIER); // Consume 'Array'
            consume(TokenType.LESS);       // Consume '<'
    
            // Accept primitive types (int, float, etc.) or custom identifiers
            String elementType;
            if (currentToken.getType() == TokenType.INT || currentToken.getType() == TokenType.FLOAT || typeToken.getType() == TokenType.BOOL) {
                elementType = consume(currentToken.getType()).getValue(); // Handle primitives like int
            } else if (currentToken.getType() == TokenType.IDENTIFIER) {
                elementType = consume(TokenType.IDENTIFIER).getValue(); // Handle identifiers like T
            } else {
                throw new RuntimeException("Expected primitive type or identifier in generic type, but found: " + currentToken.getType());
            }
    
            consume(TokenType.GREATER);   // Consume '>'
    
            String name = consume(TokenType.IDENTIFIER).getValue(); // Variable name
            consume(TokenType.ASSIGN); // Consume '='
            ExpressionNode expr = expression(); // Parse the array literal or assignment
            consume(TokenType.SEMICOLON); // Consume ';'
            
            if (expr instanceof ArrayLiteralNode) {
                ((ArrayLiteralNode) expr).setElementType(elementType);
            }
            System.out.println("creating FieldNode " + elementType + " " +expr);
            return new FieldNode("Array<" + elementType + ">", name, expr);
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

    private ExpressionNode expression() {
        return comparisonExpression();
    }

    private ExpressionNode comparisonExpression() {
        ExpressionNode node = additiveExpression();
        while (currentToken.getType() == TokenType.GREATER || currentToken.getType() == TokenType.LESS || currentToken.getType() == TokenType.ASSIGN || currentToken.getType() == TokenType.EQUALS) {
            String operator = currentToken.getValue();
            consume(currentToken.getType());
            ExpressionNode right = additiveExpression();
            node = new BinaryExpressionNode(node, operator, right);
        }
        return node;
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

    private ASTNode ifStatement() {
        // Debugging information for token flow
        System.out.println("Parsing if statement, current token: " + currentToken.getType());
    
        consume(TokenType.IF);
        consume(TokenType.LPAREN);
        ExpressionNode condition = expression(); // Parse the initial if condition
        consume(TokenType.RPAREN);
        consume(TokenType.LBRACE);
        List<ASTNode> ifBody = new ArrayList<>();
    
        // Parse statements inside the initial if block
        while (currentToken.getType() != TokenType.RBRACE) {
            ifBody.add(statement());
        }
        consume(TokenType.RBRACE);
    
        // List to store else-if branches
        List<IfNode> elseIfBranches = new ArrayList<>();
    
        // Parse any number of else-if or else blocks
        while (currentToken.getType() == TokenType.ELSE) {
            System.out.println("Parsing else block, current token: " + currentToken.getType());
    
            consume(TokenType.ELSE);
    
            // Check if it is an "else if" block
            if (currentToken.getType() == TokenType.IF) {
                consume(TokenType.IF);
                consume(TokenType.LPAREN);
                ExpressionNode elseIfCondition = expression();
                consume(TokenType.RPAREN);
                consume(TokenType.LBRACE);
    
                List<ASTNode> elseIfBody = new ArrayList<>();
                while (currentToken.getType() != TokenType.RBRACE) {
                    elseIfBody.add(statement());
                }
                consume(TokenType.RBRACE);
    
                // Add the else-if branch to the list
                elseIfBranches.add(new IfNode(elseIfCondition, elseIfBody, null, null));
            } else if (currentToken.getType() == TokenType.LBRACE) { 
                // Handle a standalone "else" block
                consume(TokenType.LBRACE);
                List<ASTNode> elseBody = new ArrayList<>();
    
                while (currentToken.getType() != TokenType.RBRACE) {
                    elseBody.add(statement());
                }
                consume(TokenType.RBRACE);
    
                // Return an IfNode with the initial condition, else-if branches, and else body
                return new IfNode(condition, ifBody, elseIfBranches, elseBody);
            } else {
                // If the token does not match the expected patterns, output an error message
                throw new RuntimeException("Unexpected token in else block: " + currentToken.getType());
            }
        }
    
        // Return an IfNode without an else block
        return new IfNode(condition, ifBody, elseIfBranches, null);
    }     
    
    private ExpressionNode primaryExpression() {
        Token token = currentToken;
    
        if (token.getType() == TokenType.NEW) {
            consume(TokenType.NEW);
            String className = consume(TokenType.IDENTIFIER).getValue();
            consume(TokenType.LPAREN);
            consume(TokenType.RPAREN);
            return new ObjectInstantiationExpressionNode(className);
        }
    
        if (token.getType() == TokenType.NUMBER) {
            consume(TokenType.NUMBER);
            if (token.getValue().contains(".")) {
                return new LiteralNode(Float.parseFloat(token.getValue()));
            } else {
                return new LiteralNode(Integer.parseInt(token.getValue()));
            }
        }
    
        if (token.getType() == TokenType.STRING) {
            consume(TokenType.STRING);
            String value = new String();
            while (currentToken.getType() == TokenType.IDENTIFIER) {
                String appendingValue = consume(TokenType.IDENTIFIER).getValue();
                value += " " + appendingValue;
            }
            consume(TokenType.STRING);
            return new LiteralNode(value);
        }
    
        if (token.getType() == TokenType.IDENTIFIER) {
            String varName = consume(TokenType.IDENTIFIER).getValue();
    
            // Check for dot notation
            if (currentToken.getType() == TokenType.DOT) {
                consume(TokenType.DOT);
                String fieldName = consume(TokenType.IDENTIFIER).getValue();
                return new VariableReferenceNode(varName + "." + fieldName);
            }
    
            // Check for function calls
            if (currentToken.getType() == TokenType.LPAREN) {
                consume(TokenType.LPAREN);
                List<ExpressionNode> args = argumentList();
                consume(TokenType.RPAREN);
                return new FunctionCallExpressionNode(varName, args);
            }
    
            return new VariableReferenceNode(varName);
        }

        if (token.getType() == TokenType.TRUE || token.getType() == TokenType.FALSE) {
            consume(token.getType());
            return new LiteralNode(token.getType() == TokenType.TRUE); // `true` maps to `true`, `false` maps to `false`
        }

        // Detect `bool(expression)` syntax
        if (token.getType() == TokenType.BOOL) {
            consume(TokenType.BOOL);
            consume(TokenType.LPAREN); // Consume '('
            ExpressionNode innerExpression = expression(); // Parse inner expression
            consume(TokenType.RPAREN); // Consume ')'
            return new BoolCastExpressionNode(innerExpression); // Return a bool cast node
        }

        if (token.getType() == TokenType.TRUE || token.getType() == TokenType.FALSE) {
            consume(token.getType());
            return new LiteralNode(token.getType() == TokenType.TRUE); // `true` maps to `true`, `false` maps to `false`
        }        

        // Check for array literal
        if (token.getType() == TokenType.LBRACKET) {
            consume(TokenType.LBRACKET);
            List<ExpressionNode> elements = new ArrayList<>();
    
            if (currentToken.getType() != TokenType.RBRACKET) {
                elements.add(expression());
                while (currentToken.getType() == TokenType.COMMA) {
                    consume(TokenType.COMMA);
                    elements.add(expression());
                }
            }
    
            consume(TokenType.RBRACKET);
            return new ArrayLiteralNode(elements);
        }
    
        // Check for generic array type (e.g., Array<int>)
        if (token.getType() == TokenType.IDENTIFIER && token.getValue().equals("Array")) {
            consume(TokenType.IDENTIFIER); // Consume 'Array'
            consume(TokenType.LESS);       // Consume '<'
            String elementType = consume(TokenType.IDENTIFIER).getValue(); // Element type (e.g., 'int')
            consume(TokenType.GREATER);   // Consume '>'
            consume(TokenType.LPAREN);
        
            List<ExpressionNode> elements = new ArrayList<>();
            if (currentToken.getType() != TokenType.RPAREN) {
                elements.add(expression());
                while (currentToken.getType() == TokenType.COMMA) {
                    consume(TokenType.COMMA);
                    elements.add(expression());
                }
            }
        
            consume(TokenType.RPAREN);
            return new ArrayLiteralNode(elementType, elements); // Use ArrayLiteralNode with type
        }        
    
        throw new RuntimeException("Unexpected token in expression: " + token.getValue());
    }
    

    private ASTNode forStatement() {
        consume(TokenType.FOR);
        consume(TokenType.LPAREN);
    
        // Parse range-based for loop (e.g., for (0 -> 5, += 1))
        if (currentToken.getType() == TokenType.NUMBER) {
            ExpressionNode startExpr = expression(); // Start index (e.g., 0)
            consume(TokenType.LEFT_ARROW);          // Consume '->'
            ExpressionNode endExpr = expression();  // End index (e.g., array.length or 5)
            consume(TokenType.COMMA);               // Consume ','
            Token incrementOperator = currentToken;
            consume(TokenType.INCREMENT_OPERATOR);  // Consume '+='
            ExpressionNode incrementExpr = expression(); // Step value (e.g., 1)
            consume(TokenType.RPAREN);              // Consume ')'
            consume(TokenType.LBRACE);              // Consume '{'
    
            List<ASTNode> body = new ArrayList<>();
            while (currentToken.getType() != TokenType.RBRACE) {
                body.add(statement());
            }
            consume(TokenType.RBRACE);
    
            return new ForRangeNode(startExpr, endExpr, incrementOperator, incrementExpr, body);
        }
    
        // Parse array-based for loop (e.g., for (i : array))
        if (currentToken.getType() == TokenType.IDENTIFIER) {
            String indexVar = consume(TokenType.IDENTIFIER).getValue(); // Index variable (e.g., i)
            consume(TokenType.COLON);                                   // Consume ':'
            ExpressionNode arrayExpr = expression();                   // Array expression (e.g., array)
            if (arrayExpr == null) {
                throw new RuntimeException("Failed to parse array expression in for loop");
            }
            consume(TokenType.RPAREN);                                 // Consume ')'
            consume(TokenType.LBRACE);                                 // Consume '{'
            
            List<ASTNode> body = new ArrayList<>();
            while (currentToken.getType() != TokenType.RBRACE) {
                body.add(statement());
            }
            consume(TokenType.RBRACE);
    
            return new ForArrayNode(indexVar, arrayExpr, body);
        }
    
        throw new RuntimeException("Invalid for loop syntax");
    }    
    
    private ASTNode whileStatement() {
        if (currentToken.getType() == TokenType.WHILE) {
            consume(TokenType.WHILE);
            consume(TokenType.LPAREN);
            ExpressionNode condition = expression(); // Parse the condition (e.g., `obj.b == true`)
            consume(TokenType.RPAREN);
            consume(TokenType.LBRACE);
    
            List<ASTNode> body = new ArrayList<>();
            while (currentToken.getType() != TokenType.RBRACE) {
                body.add(statement());
            }
            consume(TokenType.RBRACE);
    
            return new WhileNode(condition, body);
        } else if (currentToken.getType() == TokenType.DO) {
            consume(TokenType.DO);
            consume(TokenType.LBRACE);
    
            List<ASTNode> body = new ArrayList<>();
            while (currentToken.getType() != TokenType.RBRACE) {
                body.add(statement());
            }
            consume(TokenType.RBRACE);
    
            consume(TokenType.WHILE);
            consume(TokenType.LPAREN);
            ExpressionNode condition = expression(); // Parse the condition (e.g., `obj.b == true`)
            consume(TokenType.RPAREN);
            consume(TokenType.SEMICOLON); // Consume the ending semicolon of `do-while`
    
            return new DoWhileNode(condition, body);
        } else {
            throw new RuntimeException("Expected 'while' or 'do' statement");
        }
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
