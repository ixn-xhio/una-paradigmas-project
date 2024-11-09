package com.una.ac.cr.paradigms_project.types.ast;

public class ObjectFieldAssignmentNode extends ASTNode {
    private String objectName;
    private String fieldName;
    private ExpressionNode expression;

    public ObjectFieldAssignmentNode(String objectName, String fieldName, ExpressionNode expression){
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.expression = expression;
    }

    public String getObjectName(){
        return objectName;
    }

    public String getFieldName(){
        return fieldName;
    }

    public ExpressionNode getExpression(){
        return expression;
    }
}
