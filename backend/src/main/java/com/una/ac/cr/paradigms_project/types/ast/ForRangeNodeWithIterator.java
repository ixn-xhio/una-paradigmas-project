package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;

import com.una.ac.cr.paradigms_project.types.Token;
import java.util.List;

public class ForRangeNodeWithIterator extends ASTNode {
    private String iterator;
    private ExpressionNode startExpr;
    private ExpressionNode endExpr;
    private Token incrementOperator;
    private ExpressionNode incrementExpr;
    private List<ASTNode> body;

    public ForRangeNodeWithIterator(String iterator, ExpressionNode startExpr, ExpressionNode endExpr, Token incrementOperator, ExpressionNode incrementExpr, List<ASTNode> body) {
        this.iterator = iterator;
        this.startExpr = startExpr;
        this.endExpr = endExpr;
        this.incrementOperator = incrementOperator;
        this.incrementExpr = incrementExpr;
        this.body = body;
    }

    public String getIterator() {
        return iterator;
    }

    public ExpressionNode getStartExpr() {
        return startExpr;
    }

    public ExpressionNode getEndExpr() {
        return endExpr;
    }

    public Token getIncrementOperator() {
        return incrementOperator;
    }

    public ExpressionNode getIncrementExpr() {
        return incrementExpr;
    }

    public List<ASTNode> getBody() {
        return body;
    }
}
