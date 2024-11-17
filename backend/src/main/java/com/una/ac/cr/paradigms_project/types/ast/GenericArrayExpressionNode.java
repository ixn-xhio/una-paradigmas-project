package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

import java.util.List;
import java.util.stream.Collectors;

public class GenericArrayExpressionNode extends ExpressionNode {
    private String arrayType; // E.g., "Array<int>"
    private List<ExpressionNode> elements; // Elements of the array

    public GenericArrayExpressionNode(String arrayType, List<ExpressionNode> elements) {
        this.arrayType = arrayType;
        this.elements = elements;
    }

    public String getArrayType() {
        return arrayType;
    }

    public List<ExpressionNode> getElements() {
        return elements;
    }

    @Override
    public String toString() {
        return "GenericArrayExpressionNode{" +
               "arrayType='" + arrayType + '\'' +
               ", elements=" + elements +
               '}';
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        // Evaluate each element in the array
        return elements.stream()
                .map(element -> element.evaluate(context))
                .collect(Collectors.toList());
    }
}
