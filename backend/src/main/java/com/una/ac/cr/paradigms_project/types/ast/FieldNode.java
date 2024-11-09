// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/FieldNode.java
package com.una.ac.cr.paradigms_project.types.ast;

public class FieldNode extends ASTNode {
    private String type;
    private String name;
    private ExpressionNode expression;

    public FieldNode(String type, String name, ExpressionNode expression){
        this.type = type;
        this.name = name;
        this.expression = expression;
    }

    public String getType(){
        return type;
    }

    public String getName(){
        return name;
    }

    public ExpressionNode getExpression(){
        return expression;
    }
}
