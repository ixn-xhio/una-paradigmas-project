package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class BoolCastExpressionNode extends ExpressionNode {
    private final ExpressionNode expression;

    public BoolCastExpressionNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public ExpressionNode getExpression() {
        return expression;
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        Object value = expression.evaluate(context);

        // Cast value to boolean
        if (value instanceof Boolean) {
            return value;
        } else if (value instanceof Integer) {
            return (Integer) value != 0; // Non-zero integers are true
        } else if (value instanceof Float) {
            return (Float) value != 0.0f; // Non-zero floats are true
        } else if (value instanceof String) {
            return !((String) value).isEmpty(); // Non-empty strings are true
        } else {
            throw new RuntimeException("Cannot cast type to bool: " + value.getClass().getName());
        }
    }

    @Override
    public String toString() {
        return "BoolCastExpressionNode{" +
                "expression=" + expression +
                '}';
    }
}
