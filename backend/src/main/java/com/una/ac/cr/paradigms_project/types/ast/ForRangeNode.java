// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/FieldNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.types.Token;

import java.util.ArrayList;
import java.util.List;

public class ForRangeNode extends ASTNode {
    private ExpressionNode startExpr;
    private ExpressionNode endExpr;
    private Token incrementOperator;
    private ExpressionNode incrementExpr;
    private List<ASTNode> body;

    public ForRangeNode(ExpressionNode startExpr, ExpressionNode endExpr, Token incrementOperator, ExpressionNode incrementExpr, List<ASTNode> body) {
        this.startExpr = startExpr;
        this.endExpr = endExpr;
        this.incrementOperator = incrementOperator;
        this.incrementExpr = incrementExpr;
        this.body = body;
    }

    // Getters y métodos de evaluación...
    public List<ASTNode> getBody() {
        return this.body;
    }

    public ExpressionNode getStartExpr() {
        return this.startExpr;
    }


    public ExpressionNode getEndExpr() {
        return this.endExpr;
    }

    public ExpressionNode getIncrementExpr() {
        return this.incrementExpr;
    } 
}
