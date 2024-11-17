package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

import java.util.List;

public class ArrayLiteralNode extends ExpressionNode {
    private List<ExpressionNode> elements;

    public ArrayLiteralNode(List<ExpressionNode> elements) {
        this.elements = elements;
    }

    public List<ExpressionNode> getElements() {
        return elements;
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        // Create an array to hold evaluated elements
        Object[] evaluatedElements = new Object[elements.size()];
        
        // Evaluate each element of the array and store it in evaluatedElements
        for (int i = 0; i < elements.size(); i++) {
            evaluatedElements[i] = elements.get(i).evaluate(context);  // Evaluate and store result
        }

        return evaluatedElements;  // Return the evaluated array
    }
}
