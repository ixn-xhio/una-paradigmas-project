package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class ArrayVariableDeclarationNode extends ASTNode {
    private String name;
    private String type;
    private ArrayLiteralNode arrayExpression;

    public ArrayVariableDeclarationNode(String name, String type, ArrayLiteralNode arrayExpression) {
        this.name = name;
        this.type = type;
        this.arrayExpression = arrayExpression;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public ArrayLiteralNode getArrayExpression() {
        return arrayExpression;
    }

    public void evaluate(ExecutorContext context) {
        // Handle evaluation of the array declaration here (e.g., store the array in a variable map)
        context.setVariable(name, arrayExpression.evaluate(context)); // Store the evaluated array in the context
    }
}
