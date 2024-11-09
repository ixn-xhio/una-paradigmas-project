package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public class BinaryExpressionNode extends ExpressionNode {
    private ExpressionNode left;
    private ExpressionNode right;
    private String operator;

    public BinaryExpressionNode(ExpressionNode left, String operator, ExpressionNode right){
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public ExpressionNode getLeft(){
        return left;
    }

    public ExpressionNode getRight(){
        return right;
    }

    public String getOperator(){
        return operator;
    }

    @Override
    public Object evaluate(ExecutorContext context){
        Object leftVal = left.evaluate(context);
        Object rightVal = right.evaluate(context);

        if(leftVal instanceof Integer && rightVal instanceof Integer){
            int l = (Integer) leftVal;
            int r = (Integer) rightVal;
            switch(operator){
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    return l / r;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        }

        if(leftVal instanceof Float && rightVal instanceof Float){
            float l = (Float) leftVal;
            float r = (Float) rightVal;
            switch(operator){
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    return l / r;
                default:
                    throw new RuntimeException("Unknown operator: " + operator);
            }
        }

        if(leftVal instanceof String && rightVal instanceof String){
            String l = (String) leftVal;
            String r = (String) rightVal;
            switch(operator){
                case "+":
                    return l + r;
                default:
                    throw new RuntimeException("Unknown operator: " + operator + " for String operations");
            }
        }

        throw new RuntimeException("Unsupported operand types for operator " + operator);
    }
}