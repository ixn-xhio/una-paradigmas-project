package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;
import java.util.Map;
import java.lang.reflect.Field;

public class VariableReferenceNode extends ExpressionNode {
    private String varName;

    public VariableReferenceNode(String varName){
        this.varName = varName;
    }

    public String getVarName(){
        return varName;
    }

    @Override
    public Object evaluate(ExecutorContext context) {
        // Split the variable name for dot notation
        String[] parts = varName.split("\\.");

        // Get the base variable (e.g., 'obj')
        Object current = context.getVariable(parts[0]);
        if (current == null) {
            throw new RuntimeException("Variable not found: " + parts[0]);
        }

        // Traverse the dot notation (e.g., obj.ax)
        for (int i = 1; i < parts.length; i++) {
            String fieldName = parts[i];

            System.out.println("Current object: " + current + ", accessing field: " + fieldName);
            // If current is a Map, access its field
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(fieldName);
            }
            // If current is an object, use reflection to access its field
            else {
                current = getFieldValue(current, fieldName);
            }

            if (current == null) {
                throw new RuntimeException("Field not found: " + fieldName + " in " + varName);
            }
        }

        return current;
    }


    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Class<?> clazz = obj.getClass();
            Field field = clazz.getDeclaredField(fieldName); // Retrieve the field
            field.setAccessible(true); // Allow access to private/protected fields
            return field.get(obj); // Return the field's value
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Field '" + fieldName + "' not found in class: " + obj.getClass().getName(), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field '" + fieldName + "' in class: " + obj.getClass().getName(), e);
        }
    }
    
}
