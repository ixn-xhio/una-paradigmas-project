package com.una.ac.cr.paradigms_project.service;

import org.springframework.stereotype.Service;
import com.una.ac.cr.paradigms_project.utils.*;
import com.una.ac.cr.paradigms_project.types.*;
import com.una.ac.cr.paradigms_project.types.ast.*;
import com.una.ac.cr.paradigms_project.model.CodeResponse;

import java.util.List;
import java.util.UUID;

@Service
public class ExecutorService {
    private Executor executor = new Executor();
    private Parser parser = new Parser();

    public CodeResponse executeCode(String code) {
        CodeResponse response = new CodeResponse();
        try {
            Lexer lexer = new Lexer(code);
            List<Token> tokens = lexer.tokenize();
            parser.parse(tokens);
            ASTNode ast = parser.getAST();
            ExecutorState state = executor.execute(ast);
            response.setOutputs(state.getOutputs());
            response.setRequiresInput(state.isRequiresInput());
            response.setSessionId(UUID.randomUUID().toString());
        } catch (Exception e){
            response.setOutputs(List.of("Error: " + e.getMessage()));
            response.setRequiresInput(false);
            response.setSessionId(null);
        }
        return response;
    }

    public CodeResponse provideInput(String sessionId, String input){
        CodeResponse response = new CodeResponse();
        try {
            ExecutorState state = executor.provideInput(sessionId, input);
            response.setOutputs(state.getOutputs());
            response.setRequiresInput(state.isRequiresInput());
        } catch (Exception e){
            response.setOutputs(List.of("Error: " + e.getMessage()));
            response.setRequiresInput(false);
        }
        return response;
    }
}
