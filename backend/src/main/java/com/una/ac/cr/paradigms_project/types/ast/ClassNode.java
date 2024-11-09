// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/ClassNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import java.util.ArrayList;
import java.util.List;

public class ClassNode extends ASTNode {
    private String name;
    private List<FieldNode> fields = new ArrayList<>();

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
}
