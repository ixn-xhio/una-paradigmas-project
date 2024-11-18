package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArrayAccessNode extends ExpressionNode {
    private String arrayName; // Name of the array or object field containing the array
    private ExpressionNode index; // The index expression

    public ArrayAccessNode(String arrayName, ExpressionNode index) {
        this.arrayName = arrayName;
        this.index = index;
    }

    public String getArrayName() {
        return arrayName;
    }

    public ExpressionNode getIndex() {
        return index;
    }
    @Override
    public Object evaluate(ExecutorContext context) {
        // Split the array name to handle dot notation (e.g., obj.ax)
        String[] parts = arrayName.split("\\.");
        Object current = context.getVariable(parts[0]); // Get the base variable (e.g., obj)
    
        if (current == null) {
            throw new RuntimeException("Variable " + parts[0] + " not found in context.");
        }
    
        // Traverse the dot notation (e.g., obj.ax)
        for (int i = 1; i < parts.length; i++) {
            if (current instanceof Map) {
                current = ((Map<?, ?>) current).get(parts[i]); // Access property in a Map (e.g., obj fields)
            } else {
                throw new RuntimeException("Unable to access property '" + parts[i] + "' on " + current);
            }
    
            if (current == null) {
                throw new RuntimeException("Property " + parts[i] + " not found on object.");
            }
        }
    
        // Current should now be the array (e.g., obj.ax)
        if (!(current instanceof List<?>)) {
            throw new RuntimeException("Variable " + arrayName + " is not an array. Actual value: " + current);
        }
    
        // Evaluate the index
        Object indexVal = index.evaluate(context);
        if (!(indexVal instanceof Integer)) {
            throw new RuntimeException("Index for array access must be an integer, but got: " + indexVal.getClass().getName());
        }
    
        // Access the array element
        int idx = (Integer) indexVal;
        List<?> arrayList = (List<?>) current;
    
        if (idx < 0 || idx >= arrayList.size()) {
            throw new RuntimeException("Array index out of bounds: " + idx);
        }
    
        return arrayList.get(idx);
    }    
    
}
