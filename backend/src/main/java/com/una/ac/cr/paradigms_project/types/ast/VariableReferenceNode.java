package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class VariableReferenceNode extends ExpressionNode {
    private String varName;

    public VariableReferenceNode(String varName){
        this.varName = varName;
    }

    public String getVarName(){
        return varName;
    }

    @Override
    public Object evaluate(ExecutorContext context){
        return context.getVariableValue(varName);
    }
}
