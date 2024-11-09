package com.una.ac.cr.paradigms_project.types.ast;

public class ObjectInstantiationNode extends ASTNode {
    private String variableName;
    private String className;

    public ObjectInstantiationNode(String variableName, String className){
        this.variableName = variableName;
        this.className = className;
    }

    public String getVariableName(){
        return variableName;
    }

    public String getClassName(){
        return className;
    }
}