// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/LiteralNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.*;

public class LiteralNode extends ExpressionNode {
    private Object value;

    public LiteralNode(Object value){
        this.value = value;
    }

    public Object getValue(){
        return value;
    }

    @Override
    public Object evaluate(ExecutorContext context){
        return value;
    }
}
