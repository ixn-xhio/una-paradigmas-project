package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.Token;
import com.una.ac.cr.paradigms_project.types.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int pos;
    private char currentChar;

    public Lexer(String input){
        this.input = input;
        this.pos = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }

    private void advance(){
        pos++;
        if(pos >= input.length()){
            currentChar = '\0';
        } else {
            currentChar = input.charAt(pos);
        }
    }

    private void skipWhitespace(){
        while(currentChar != '\0' && Character.isWhitespace(currentChar)){
            advance();
        }
    }

    private String identifier(){
        StringBuilder result = new StringBuilder();
        while(currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')){
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    private String number(){
        StringBuilder result = new StringBuilder();
        while(currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')){
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    public List<Token> tokenize(){
        List<Token> tokens = new ArrayList<>();
        while(currentChar != '\0'){
            if(Character.isWhitespace(currentChar)){
                skipWhitespace();
                continue;
            }
            if(Character.isLetter(currentChar)){
                String id = identifier();
                switch(id){
                    case "function":
                        tokens.add(new Token(TokenType.FUNCTION, id));
                        break;
                    case "public":
                        tokens.add(new Token(TokenType.PUBLIC, id));
                        break;
                    case "class":
                        tokens.add(new Token(TokenType.CLASS, id));
                        break;
                    case "int":
                        tokens.add(new Token(TokenType.INT, id));
                        break;
                    case "float":
                        tokens.add(new Token(TokenType.FLOAT, id));
                        break;
                    case "integer": // Added case for 'integer'
                        tokens.add(new Token(TokenType.INTEGER, id));
                        break;
                    case "read":
                        tokens.add(new Token(TokenType.READ, id));
                        break;
                    case "print":
                        tokens.add(new Token(TokenType.PRINT, id));
                        break;
                    case "new":
                        tokens.add(new Token(TokenType.NEW, id));
                        break;
                    case "return":
                        tokens.add(new Token(TokenType.RETURN, id));
                        break;
                    default:
                        tokens.add(new Token(TokenType.IDENTIFIER, id));
                        break;
                }
                continue;
            }
            if(Character.isDigit(currentChar)){
                String num = number();
                tokens.add(new Token(TokenType.NUMBER, num));
                continue;
            }
            switch(currentChar){
                case '=':
                    tokens.add(new Token(TokenType.ASSIGN, "="));
                    advance();
                    break;
                case ';':
                    tokens.add(new Token(TokenType.SEMICOLON, ";"));
                    advance();
                    break;
                case ',':
                    tokens.add(new Token(TokenType.COMMA, ","));
                    advance();
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "("));
                    advance();
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")"));
                    advance();
                    break;
                case '{':
                    tokens.add(new Token(TokenType.LBRACE, "{"));
                    advance();
                    break;
                case '}':
                    tokens.add(new Token(TokenType.RBRACE, "}"));
                    advance();
                    break;
                case ':':
                    tokens.add(new Token(TokenType.COLON, ":"));
                    advance();
                    break;
                case '.':
                    tokens.add(new Token(TokenType.DOT, "."));
                    advance();
                    break;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+"));
                    advance();
                    break;
                case '-':
                    tokens.add(new Token(TokenType.MINUS, "-"));
                    advance();
                    break;
                case '*':
                    tokens.add(new Token(TokenType.MULTIPLY, "*"));
                    advance();
                    break;
                case '/':
                    tokens.add(new Token(TokenType.DIVIDE, "/"));
                    advance();
                    break;
                case '\'':
                    tokens.add(new Token(TokenType.STRING, "'"));
                    advance();
                    break;
                default:
                    throw new RuntimeException("Unknown character: " + currentChar);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }
}
