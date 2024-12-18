package com.una.ac.cr.paradigms_project.types.ast;

import java.util.List;

public class DoWhileNode extends ASTNode {
    private ExpressionNode condition;
    private List<ASTNode> body;

    public DoWhileNode(ExpressionNode condition, List<ASTNode> body) {
        this.condition = condition;
        this.body = body;
    }

    // Getter for the condition
    public ExpressionNode getCondition() {
        return condition;
    }

    // Getter for the body
    public List<ASTNode> getBody() {
        return body;
    }
}