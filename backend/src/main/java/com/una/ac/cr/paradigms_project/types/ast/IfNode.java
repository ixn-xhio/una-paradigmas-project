package com.una.ac.cr.paradigms_project.types.ast;

import com.una.ac.cr.paradigms_project.utils.ExecutorContext;
import java.util.List;

public class IfNode extends ASTNode {
    private ExpressionNode condition;         // The condition for the 'if' statement
    private List<ASTNode> trueBranch;         // Statements to execute if the condition is true
    private List<IfNode> elseIfBranches;      // List of else-if branches (if any)
    private List<ASTNode> falseBranch;        // Statements to execute if the condition is false (optional)

    public IfNode(ExpressionNode condition, List<ASTNode> trueBranch, List<IfNode> elseIfBranches, List<ASTNode> falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.elseIfBranches = elseIfBranches;
        this.falseBranch = falseBranch;
    }

    public ExpressionNode getCondition() {
        return condition;
    }

    public List<ASTNode> getTrueBranch() {
        return trueBranch;
    }

    public List<IfNode> getElseIfBranches() {
        return elseIfBranches;
    }

    public List<ASTNode> getFalseBranch() {
        return falseBranch;
    }
}
