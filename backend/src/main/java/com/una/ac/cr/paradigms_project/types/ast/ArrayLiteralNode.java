package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;
import java.util.List;

public class ArrayLiteralNode extends ExpressionNode {
    private String elementType; // Optional, e.g., "int" for Array<int>
    private List<ExpressionNode> elements;

    // Existing constructor
    public ArrayLiteralNode(List<ExpressionNode> elements) {
        this.elements = elements;
    }

    // New constructor to support type
    public ArrayLiteralNode(String elementType, List<ExpressionNode> elements) {
        this.elementType = elementType;
        this.elements = elements;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public List<ExpressionNode> getElements() {
        return elements;
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        // Evaluate each element in the array
        return elements.stream()
                .map(element -> element.evaluate(context))
                .toList();
    }

    @Override
    public String toString() {
        return "ArrayLiteralNode{" +
               "elementType='" + elementType + '\'' +
               ", elements=" + elements +
               '}';
    }
}
