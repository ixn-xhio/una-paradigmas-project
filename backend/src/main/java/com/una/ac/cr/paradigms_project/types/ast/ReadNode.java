package com.una.ac.cr.paradigms_project.types.ast;

public class ReadNode extends ASTNode {
    private String variableName;

    public ReadNode(String variableName){
        this.variableName = variableName;
    }

    public String getVariableName(){
        return variableName;
    }
}
