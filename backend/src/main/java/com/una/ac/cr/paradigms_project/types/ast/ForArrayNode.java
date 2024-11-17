package com.una.ac.cr.paradigms_project.types.ast;

import java.util.List;

public class ForArrayNode extends ASTNode {
    private String indexVar; // Index variable (e.g., "i")
    private ExpressionNode arrayExpr; // Array expression (e.g., "array")
    private List<ASTNode> body; // Loop body

    public ForArrayNode(String indexVar, ExpressionNode arrayExpr, List<ASTNode> body) {
        this.indexVar = indexVar;
        this.arrayExpr = arrayExpr;
        this.body = body;
    }

    public String getIndexVar() {
        return indexVar;
    }

    public ExpressionNode getArrayExpr() {
        return arrayExpr;
    }

    public List<ASTNode> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "ForArrayNode{" +
               "indexVar='" + indexVar + '\'' +
               ", arrayExpr=" + arrayExpr +
               ", body=" + body +
               '}';
    }
}
