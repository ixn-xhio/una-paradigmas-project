package com.una.ac.cr.paradigms_project.types.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends ASTNode {
    private List<ASTNode> statements;

    public ProgramNode(){
        this.statements = new ArrayList<>();
    }

    public void addStatement(ASTNode node){
        this.statements.add(node);
    }

    public List<ASTNode> getStatements(){
        return statements;
    }
}
