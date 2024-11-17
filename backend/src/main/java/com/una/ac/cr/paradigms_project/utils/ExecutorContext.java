// File: src/main/java/com/una/ac/cr/paradigms_project/executor/ExecutorContext.java
package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.*;
import com.una.ac.cr.paradigms_project.types.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;

public class ExecutorContext {
    // Variables stored in the current context
    Map<String, Object> variables = new HashMap<>();

    // Functions stored in the current context
    private Map<String, Function> functions = new HashMap<>();

    // Classes stored in the current context
    private Map<String, ClassDef> classes = new HashMap<>();

    // Variable waiting for input
    private String waitingForInputVar = null;

    // Execution pointer to track current statement index
    private int executionPointer = 0;

    // List of statements to execute
    private List<ASTNode> statements;

    public ExecutorContext() {
        // Initialize if needed
    }

    public void setStatements(List<ASTNode> statements) {
        this.statements = statements;
    }

    public List<ASTNode> getStatements() {
        return statements;
    }

    public int getExecutionPointer() {
        return executionPointer;
    }

    public void incrementExecutionPointer() {
        this.executionPointer++;
    }

    public void setExecutionPointer(int executionPointer) {
        this.executionPointer = executionPointer;
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariableValue(String varName, Object value) {
        if(varName.contains(".")) {
            String[] parts = varName.split("\\.");
            Object obj = variables.get(parts[0]);
            if(obj instanceof Map) {
                ((Map<String, Object>) obj).put(parts[1], value);
            } else {
                throw new RuntimeException("Variable " + parts[0] + " is not an object.");
            }
        } else {
            variables.put(varName, value);
        }
    }
    
    public Object getVariableValue(String varName) {
        System.out.println("Resolving variable: " + varName);
        if (varName.contains(".")) {
            String[] parts = varName.split("\\.");
            Object current = variables.get(parts[0]); // Get base variable (e.g., 'obj')
            if (current == null) {
                throw new RuntimeException("Base variable not found: " + parts[0]);
            }
    
            System.out.println(current);
            // Traverse the dot notation
            for (int i = 1; i < parts.length; i++) {
                String fieldName = parts[i];
                System.out.println("Accessing field: " + fieldName + " of object: " + current);
                current = getFieldValue(current, fieldName);
                System.out.println(current);
                if (current == null) {
                    System.out.println(current);
                    throw new RuntimeException("Field '" + fieldName + "' not found over " + varName);
                }
            }
    
            return current;
        } else {
            return variables.get(varName);
        }
    }
    
        

    public boolean isWaitingForInput() {
        return waitingForInputVar != null;
    }

    public String getWaitingForInputVar() {
        return waitingForInputVar;
    }

    public void setWaitingForInputVar(String varName) {
        this.waitingForInputVar = varName;
    }

    public void addFunction(String name, Function function) {
        functions.put(name, function);
    }

    public Function getFunction(String name) {
        return functions.get(name);
    }

    public void addClass(String name, ClassDef cls) {
        classes.put(name, cls);
    }

    public ClassDef getClass(String name) {
        return classes.get(name);
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(fieldName); // Get the field using reflection
            field.setAccessible(true); // Allow access to private/protected fields
            return field.get(obj); // Retrieve the field's value
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(
                "Field '" + fieldName + "' not found in class: " + obj.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(
                "Cannot access field '" + fieldName + "' in class: " + obj.getClass().getName(), e);
        }
    }    
}
