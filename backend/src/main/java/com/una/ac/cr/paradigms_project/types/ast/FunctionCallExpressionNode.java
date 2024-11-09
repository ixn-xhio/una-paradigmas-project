package com.una.ac.cr.paradigms_project.types.ast;

import java.util.List;
import java.util.ArrayList;

import com.una.ac.cr.paradigms_project.utils.*;

public class FunctionCallExpressionNode extends ExpressionNode {
    private String functionName;
    private List<ExpressionNode> arguments;

    public FunctionCallExpressionNode(String functionName, List<ExpressionNode> arguments){
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName(){
        return functionName;
    }

    public List<ExpressionNode> getArguments(){
        return arguments;
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        Function function = context.getFunction(functionName);
        if(function == null){
            throw new RuntimeException("Function '" + functionName + "' is not defined.");
        }
        List<Object> evaluatedArgs = new ArrayList<>();
        for(ExpressionNode arg : arguments){
            evaluatedArgs.add(arg.evaluate(context));
        }
        return function.execute(evaluatedArgs, context);
    }
}
