package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class ObjectInstantiationExpressionNode extends ExpressionNode {
    private String className;

    public ObjectInstantiationExpressionNode(String className){
        this.className = className;
    }

    public String getClassName(){
        return className;
    }

    @Override
    public Object evaluate(ExecutorContext context){
        return null;
    }
}
