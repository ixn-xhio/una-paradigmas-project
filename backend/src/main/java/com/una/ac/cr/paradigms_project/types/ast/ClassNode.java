// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/ClassNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ClassNode extends ASTNode {
    private String name;
    private List<FieldNode> fields = new ArrayList<>();
    private final Map<String, MethodNode> methods = new HashMap<>();

    public ClassNode(String name){
        this.name = name;
    }

    public void addField(FieldNode field){
        fields.add(field);
    }

    public String getName(){
        return name;
    }

    public List<FieldNode> getFields(){
        return fields;
    }

    public void addMethod(MethodNode method) {
        methods.put(method.getName(), method);
    }

    public MethodNode getMethod(String name) {
        return methods.get(name);
    }
}
