package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class PrintNode extends ASTNode {
    private ExpressionNode expression;

    public PrintNode(ExpressionNode expression){
        this.expression = expression;
    }

    public ExpressionNode getExpression(){
        return expression;
    }

    // No necesita un método evaluate
}
