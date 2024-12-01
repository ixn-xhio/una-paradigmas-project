package com.una.ac.cr.paradigms_project.types.ast;

import java.util.List;

public class MethodNode extends ASTNode {
    private final String name;
    private final List<Parameter> parameters;
    private final String returnType;
    private final List<ASTNode> body;

    public MethodNode(String name, List<Parameter> parameters, String returnType, List<ASTNode> body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "MethodNode{" +
                "name='" + name + '\'' +
                ", parameters=" + parameters +
                ", returnType='" + returnType + '\'' +
                ", body=" + body +
                '}';
    }
}