package com.una.ac.cr.paradigms_project.types.ast;

public class AssignmentNode extends ASTNode {
    private String varName;
    private ExpressionNode expression;

    public AssignmentNode(String varName, ExpressionNode expression){
        this.varName = varName;
        this.expression = expression;
    }

    public String getVarName(){
        return varName;
    }

    public ExpressionNode getExpression(){
        return expression;
    }
}
