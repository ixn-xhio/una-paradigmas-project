// File: src/main/java/com/una/ac/cr/paradigms_project/controller/CodeExecutionController.java
package com.una.ac.cr.paradigms_project.controller;

import com.una.ac.cr.paradigms_project.model.*;
import com.una.ac.cr.paradigms_project.utils.*;
import com.una.ac.cr.paradigms_project.types.*;
import com.una.ac.cr.paradigms_project.types.ast.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/code")
public class CodeController {
    private static final Logger logger = Logger.getLogger(CodeController.class.getName());

    @Autowired
    private Executor executor;

    /**
     * Endpoint to execute code.
     *
     * @param request The CodeExecutionRequest containing the code to execute.
     * @return ResponseEntity with ExecutorState.
     */
    @PostMapping("/execute")
    public ResponseEntity<ExecutorState> executeCode(@RequestBody CodeRequest request) {
        try {
            logger.info("Received code execution request.");
            // Tokenize the input code
            Lexer lexer = new Lexer(request.getCode());
            List<Token> tokens = lexer.tokenize();
            logger.info("Tokenization completed.");
            Parser parser = new Parser();
            // Parse the tokens into an AST
            parser.parse(tokens);
            ASTNode ast = parser.getAST();
            logger.info("Parsing completed.");

            // Execute the AST
            ExecutorState state = executor.execute(ast);
            logger.info("Execution completed.");

            return ResponseEntity.ok(state);
        } catch (Exception e) {
            logger.severe("Execution failed: " + e.getMessage());
            ExecutorState errorState = new ExecutorState(
                List.of("Error: " + e.getMessage()),
                false,
                null
            );
            return ResponseEntity.badRequest().body(errorState);
        }
    }

    /**
     * Endpoint to provide input for a pending session.
     *
     * @param request The InputRequest containing sessionId and input.
     * @return ResponseEntity with ExecutorState.
     */
    @PostMapping("/input")
    public ResponseEntity<ExecutorState> provideInput(@RequestBody InputRequest request) {
        try {
            logger.info("Received input provision request for sessionId: " + request.getSessionId());
            ExecutorState state = executor.provideInput(request.getSessionId(), request.getInput());
            logger.info("Input provided and execution resumed.");
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            logger.severe("Input provision failed: " + e.getMessage());
            ExecutorState errorState = new ExecutorState(
                List.of("Error: " + e.getMessage()),
                false,
                null
            );
            return ResponseEntity.badRequest().body(errorState);
        }
    }
}
