package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.*;

public class ReturnNode extends ExpressionNode {
    private ExpressionNode expression;

    public ReturnNode(ExpressionNode expression){
        this.expression = expression;
    }

    public ExpressionNode getExpression(){
        return expression;
    }

    @Override
    public Object evaluate(ExecutorContext context){
        Object value = expression.evaluate(context);
        throw new ReturnException(value);
    }
}
