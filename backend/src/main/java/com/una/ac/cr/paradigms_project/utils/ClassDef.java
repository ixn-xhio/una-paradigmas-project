// File: src/main/java/com/una/ac/cr/paradigms_project/executor/ClassDef.java
package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.ast.FieldNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDef {
    private String name;
    private Map<String, Object> fields = new HashMap<>();

    public ClassDef(String name, List<FieldNode> fieldNodes){
        this.name = name;
        for(FieldNode field : fieldNodes){
            Object value = field.getExpression().evaluate(new ExecutorContext());
            fields.put(field.getName(), value);
        }
    }

    public String getName(){
        return name;
    }

    public Map<String, Object> getFields(){
        return fields;
    }

    public Object instantiate(){
        return new HashMap<>(fields);
    }
}
