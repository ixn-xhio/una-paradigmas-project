// Function.java
package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.ast.*;
import java.util.List;
import java.util.ArrayList;

public class Function {
    private FunctionNode functionNode;
    private Executor executor; // Injected Executor instance

    // Constructor to initialize Function with FunctionNode and Executor
    public Function(FunctionNode functionNode, Executor executor){
        this.functionNode = functionNode;
        this.executor = executor;
    }

    public Object execute(List<Object> args, ExecutorContext context) {
        // Crear un nuevo contexto para la ejecución de la función
        ExecutorContext functionContext = new ExecutorContext();
        functionContext.setStatements(functionNode.getBody());

        // Asignar los parámetros a las variables locales
        List<Parameter> params = functionNode.getParameters();
        if(args.size() != params.size()){
            throw new RuntimeException("Function '" + functionNode.getName() + "' expects " + params.size() + " arguments but got " + args.size());
        }
        for(int i=0; i<params.size(); i++){
            String paramName = params.get(i).getName();
            String paramType = params.get(i).getType();
            Object argValue = args.get(i);

            // Tipo de verificación básica
            if(paramType.equals("int") && !(argValue instanceof Integer)){
                throw new RuntimeException("Function '" + functionNode.getName() + "' expects parameter '" + paramName + "' to be int.");
            }
            if(paramType.equals("float") && !(argValue instanceof Float)){
                throw new RuntimeException("Function '" + functionNode.getName() + "' expects parameter '" + paramName + "' to be float.");
            }
            // Agregar otros tipos según sea necesario

            functionContext.setVariable(paramName, argValue);
        }

        // Ejecutar el cuerpo de la función
        ExecutorState functionState = executor.executeWithContextInternal(functionContext, functionNode.getBody());

        // Obtener el valor de retorno
        Object returnValue = functionState.getReturnValue();
        if(returnValue == null){
            throw new RuntimeException("Function '" + functionNode.getName() + "' did not return a value.");
        }
        return returnValue;
    }
}
