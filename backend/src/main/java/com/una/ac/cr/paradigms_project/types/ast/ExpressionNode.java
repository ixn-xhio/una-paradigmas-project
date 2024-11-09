// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/ExpressionNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

public abstract class ExpressionNode extends ASTNode {
    public abstract Object evaluate(ExecutorContext context);
}
