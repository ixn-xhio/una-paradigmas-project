package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.Token;
import com.una.ac.cr.paradigms_project.types.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int pos;
    private char currentChar;

    public Lexer(String input) {
        this.input = input;
        this.pos = 0;
        this.currentChar = input.length() > 0 ? input.charAt(0) : '\0';
    }

    private void advance() {
        pos++;
        if (pos >= input.length()) {
            currentChar = '\0';
        } else {
            currentChar = input.charAt(pos);
        }
    }

    private void skipWhitespace() {
        while (currentChar != '\0' && Character.isWhitespace(currentChar)) {
            advance();
        }
    }

    private String identifier() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isLetterOrDigit(currentChar) || currentChar == '_')) {
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    private String number() {
        StringBuilder result = new StringBuilder();
        while (currentChar != '\0' && (Character.isDigit(currentChar) || currentChar == '.')) {
            result.append(currentChar);
            advance();
        }
        return result.toString();
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while (currentChar != '\0') {
            if (Character.isWhitespace(currentChar)) {
                skipWhitespace();
                continue;
            }

            // Handling for Array types (e.g., "Array<Class>")
            if (currentChar == 'A' && peekNextChars(5).equals("rray<")) {
                advance(); // 'A'
                advance(); // 'r'
                advance(); // 'r'
                advance(); // 'y'
                advance(); // '<'
                String className = identifier();
                tokens.add(new Token(TokenType.ARRAY_TYPE, "Array<" + className + ">"));
                advance(); // '>'
                continue;
            }

            // Handling for array literals like ["hola", "pepe"]
            if (currentChar == '[') {
                tokens.add(new Token(TokenType.LBRACKET, "["));
                advance();
                // Tokenize elements within the array literal
                while (currentChar != '\0' && currentChar != ']') {
                    if (currentChar == ',') {
                        tokens.add(new Token(TokenType.COMMA, ","));
                        advance();
                    } else if (currentChar == '\'') {  // Assuming string literals are enclosed in single quotes
                        tokens.add(new Token(TokenType.STRING, String.valueOf(currentChar)));
                        advance();
                    } else if (Character.isLetterOrDigit(currentChar)) {
                        String element = identifier();
                        tokens.add(new Token(TokenType.ARRAY_LITERAL, element));
                    } else {
                        throw new RuntimeException("Unexpected character in array literal: " + currentChar);
                    }
                }
                if (currentChar == ']') {
                    tokens.add(new Token(TokenType.RBRACKET, "]"));
                    advance();
                }
                continue;
            }

            // Handle normal identifiers, numbers, and other symbols
            if (Character.isLetter(currentChar)) {
                String id = identifier();
                tokens.add(new Token(TokenType.IDENTIFIER, id));
                continue;
            }

            if (Character.isDigit(currentChar)) {
                String num = number();
                tokens.add(new Token(TokenType.NUMBER, num));
                continue;
            }

            switch (currentChar) {
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
                default:
                    throw new RuntimeException("Unknown character: " + currentChar);
            }
        }
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    private String peekNextChars(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (pos + i < input.length()) {
                sb.append(input.charAt(pos + i));
            } else {
                break;
            }
        }
        return sb.toString();
    }
}
