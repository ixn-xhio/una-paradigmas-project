package com.una.ac.cr.paradigms_project.types.ast;

public class Parameter {
    private String name;
    private String type;

    public Parameter(String name, String type){
        this.name = name;
        this.type = type;
    }

    public String getName(){
        return name;
    }

    public String getType(){
        return type;
    }
}
