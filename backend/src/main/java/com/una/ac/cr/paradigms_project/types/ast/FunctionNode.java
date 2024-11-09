package com.una.ac.cr.paradigms_project.types.ast;

import java.util.List;

public class FunctionNode extends ASTNode {
    private String name;
    private List<Parameter> parameters;
    private List<ASTNode> body;

    public FunctionNode(String name, List<Parameter> parameters, List<ASTNode> body){
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }

    public String getName(){
        return name;
    }

    public List<Parameter> getParameters(){
        return parameters;
    }

    public List<ASTNode> getBody(){
        return body;
    }
}
