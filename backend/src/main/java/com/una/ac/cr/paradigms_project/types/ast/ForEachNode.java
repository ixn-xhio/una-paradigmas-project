// File: src/main/java/com/una/ac/cr/paradigms_project/types/ast/FieldNode.java
package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.types.Token;

import java.util.ArrayList;
import java.util.List;

public class ForEachNode extends ASTNode {
    private Token elementName;
    private ExpressionNode arrayExpression;
    private List<ASTNode> body;

    public ForEachNode(Token elementName, ExpressionNode arrayExpression, List<ASTNode> body) {
        this.elementName = elementName;
        this.arrayExpression = arrayExpression;
        this.body = body;
    }
    // Getters y métodos de evaluación...
}
