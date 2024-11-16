package com.una.ac.cr.paradigms_project.types.ast;

import java.util.ArrayList;
import java.util.List;

public class WhileNode extends ASTNode {
    private ExpressionNode condition;
    private List<ASTNode> body;

    public WhileNode(ExpressionNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }
    // Getters y métodos de evaluación...
}
