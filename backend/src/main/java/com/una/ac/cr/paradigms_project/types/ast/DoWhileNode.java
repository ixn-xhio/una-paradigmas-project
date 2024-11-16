// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/FieldNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import java.util.ArrayList;
import java.util.List;

public class DoWhileNode extends ASTNode {
    private ExpressionNode condition;
    private List<ASTNode> body;

    public DoWhileNode(ExpressionNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }
    // Getters y métodos de evaluación...
}
