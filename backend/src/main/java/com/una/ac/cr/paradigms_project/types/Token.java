package com.una.ac.cr.paradigms_project.types;

public class Token {
    private TokenType type;
    private String value;

    public Token(TokenType type, String value){
        this.type = type;
        this.value = value;
    }

    public TokenType getType(){
        return type;
    }

    public String getValue(){
        return value;
    }
}
