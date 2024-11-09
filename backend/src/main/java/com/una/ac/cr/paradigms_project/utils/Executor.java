// File: src/main/java/com/una/ac/cr/paradigms_project/utils/Executor.java
package com.una.ac.cr.paradigms_project.utils;

import com.una.ac.cr.paradigms_project.types.*;
import com.una.ac.cr.paradigms_project.types.ast.*;
import com.una.ac.cr.paradigms_project.model.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class Executor {
    private static final Logger logger = Logger.getLogger(Executor.class.getName());

    private final ConcurrentMap<String, ExecutorContext> sessions = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, List<String>> sessionOutputs = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Boolean> sessionPartialFlags = new ConcurrentHashMap<>();

    /**
     * Executes the provided ASTNode.
     * If execution requires input, a sessionId is generated and returned.
     *
     * @param ast The Abstract Syntax Tree node to execute.
     * @return ExecutorState containing outputs, input requirement, and sessionId.
     */
    public ExecutorState execute(ASTNode ast) {
        ExecutorContext context = new ExecutorContext();
        List<String> outputs = new ArrayList<>();
        boolean partialExecution = false;
        String sessionId = null;

        try {
            if (ast instanceof ProgramNode) {
                ProgramNode program = (ProgramNode) ast;
                context.setStatements(program.getStatements());

                while (context.getExecutionPointer() < context.getStatements().size()) {
                    ASTNode statement = context.getStatements().get(context.getExecutionPointer());

                    executeStatement(statement, context, outputs);

                    if (context.isWaitingForInput()) {
                        partialExecution = true;
                        sessionId = UUID.randomUUID().toString();
                        logger.info("Generating sessionId: " + sessionId);

                        sessions.put(sessionId, context);
                        sessionOutputs.put(sessionId, new ArrayList<>(outputs));
                        sessionPartialFlags.put(sessionId, partialExecution);

                        logger.info("Session " + sessionId + " created and waiting for input.");
                        return new ExecutorState(new ArrayList<>(outputs), partialExecution, sessionId);
                    }

                    context.incrementExecutionPointer();
                }
            }
        } catch (Exception e) {
            String errorMsg = "Execution error: " + e.getMessage();
            outputs.add("Error: " + e.getMessage());
            logger.log(Level.SEVERE, errorMsg, e);
            return new ExecutorState(outputs, false, null);
        }

        logger.info("Execution completed without requiring input.");
        return new ExecutorState(new ArrayList<>(outputs), partialExecution, null);
    }

    /**
     * Ejecuta un ASTNode
     *
     * @param statement The ASTNode statement to execute.
     * @param context   The current execution context.
     * @param outputs   The list to accumulate output strings.
     */
    public void executeStatement(ASTNode statement, ExecutorContext context, List<String> outputs) {
        String statementType = statement.getClass().getSimpleName();
        logger.info("Executing statement at pointer " + context.getExecutionPointer() + ": " + statementType);
    
        if (statement instanceof VariableDeclarationNode) {
            VariableDeclarationNode varDecl = (VariableDeclarationNode) statement;
            Object value = varDecl.getExpression().evaluate(context);
            context.setVariable(varDecl.getVarName(), value);
            logger.info("Variable '" + varDecl.getVarName() + "' set to: " + value);
        }
        else if (statement instanceof ObjectInstantiationNode) {
            ObjectInstantiationNode objInst = (ObjectInstantiationNode) statement;
            // Instantiate a new object as a HashMap
            context.setVariable(objInst.getVariableName(), new HashMap<String, Object>());
            logger.info("Object '" + objInst.getVariableName() + "' instantiated.");
        }
        else if (statement instanceof ObjectFieldAssignmentNode) {
            ObjectFieldAssignmentNode fieldAssign = (ObjectFieldAssignmentNode) statement;
            Object value = fieldAssign.getExpression().evaluate(context);
            context.setVariableValue(fieldAssign.getObjectName() + "." + fieldAssign.getFieldName(), value);
            logger.info("Field '" + fieldAssign.getObjectName() + "." + fieldAssign.getFieldName() + "' set to: " + value);
        }
        else if (statement instanceof ReadNode) {
            ReadNode read = (ReadNode) statement;
            String varName = read.getVariableName();
            context.setWaitingForInputVar(varName);
            logger.info("Read operation initiated for variable: " + varName);
        }
        else if (statement instanceof PrintNode) {
            PrintNode print = (PrintNode) statement;
            Object value = print.getExpression().evaluate(context);
            outputs.add(value.toString());
            logger.info("Print operation: " + value.toString());
        }
        else if (statement instanceof FunctionNode) {
            FunctionNode func = (FunctionNode) statement;
            context.addFunction(func.getName(), new Function(func, this));
            logger.info("Function '" + func.getName() + "' defined.");
        }
        else if (statement instanceof ClassNode) {
            ClassNode cls = (ClassNode) statement;
            context.addClass(cls.getName(), new ClassDef(cls.getName(), cls.getFields()));
            logger.info("Class '" + cls.getName() + "' defined.");
        }
        else if (statement instanceof ReturnNode) {
            ReturnNode returnNode = (ReturnNode) statement;
            Object returnValue = returnNode.getExpression().evaluate(context);
            throw new ReturnException(returnValue);
        }
        else {
            String errorMsg = "Unknown statement type: " + statementType;
            logger.severe(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    
        logger.info("Executed statement. Current pointer: " + context.getExecutionPointer());
    }
    

    /**
     * Provides input for a given sessionId and resumes execution.
     *
     * @param sessionId The unique identifier for the session.
     * @param input     The input string provided by the user.
     * @return ExecutorState containing outputs and the updated state.
     */
    public ExecutorState provideInput(String sessionId, String input) {
        ExecutorContext context = sessions.get(sessionId);
        List<String> outputs = sessionOutputs.getOrDefault(sessionId, new ArrayList<>());
        boolean partialExecution = sessionPartialFlags.getOrDefault(sessionId, false);

        if (context == null) {
            logger.warning("Invalid sessionId received: " + sessionId);
            return new ExecutorState(
                Arrays.asList("Error: Invalid sessionId."),
                false,
                null
            );
        }

        try {
            Object value = parseInput(input);
            context.setVariableValue(context.getWaitingForInputVar(), value);
            logger.info("Input provided for variable '" + context.getWaitingForInputVar() + "': " + value);
            context.setWaitingForInputVar(null);
            context.incrementExecutionPointer();
            partialExecution = false;

            ExecutorState state = executeWithContext(sessionId, context, outputs, partialExecution);
            return state;
        } catch (Exception e) {
            String errorMsg = "ProvideInput error: " + e.getMessage();
            outputs.add("Error: " + e.getMessage());
            logger.log(Level.SEVERE, errorMsg, e);
            // Clean up the session on error
            sessions.remove(sessionId);
            sessionOutputs.remove(sessionId);
            sessionPartialFlags.remove(sessionId);
            return new ExecutorState(outputs, false, null);
        }
    }

    /**
     * Continues execution with an existing context.
     *
     * @param sessionId        The unique identifier for the session.
     * @param context          The current execution context.
     * @param outputs          The list to accumulate output strings.
     * @param partialExecution The flag indicating if partial execution is ongoing.
     * @return ExecutorState containing outputs and the updated state.
     */
    private ExecutorState executeWithContext(String sessionId, ExecutorContext context, List<String> outputs, boolean partialExecution) {
        try {
            logger.info("Resuming execution for sessionId: " + sessionId + " from pointer " + context.getExecutionPointer());
    
            while (context.getExecutionPointer() < context.getStatements().size()) {
                ASTNode statement = context.getStatements().get(context.getExecutionPointer());
    
                executeStatement(statement, context, outputs);
    
                if (context.isWaitingForInput()) {
                    partialExecution = true;
                    context.incrementExecutionPointer();
                    logger.info("Incremented execution pointer to " + context.getExecutionPointer());
    
                    sessionPartialFlags.put(sessionId, partialExecution);
                    sessionOutputs.put(sessionId, outputs);
                    logger.info("Session " + sessionId + " is waiting for input.");
    
                    return new ExecutorState(new ArrayList<>(outputs), partialExecution, sessionId);
                }
    
                context.incrementExecutionPointer();
                logger.info("Execution pointer incremented to " + context.getExecutionPointer());
            }
    
            sessions.remove(sessionId);
            sessionOutputs.remove(sessionId);
            sessionPartialFlags.remove(sessionId);
            logger.info("Session " + sessionId + " completed execution.");
    
            return new ExecutorState(new ArrayList<>(outputs), partialExecution, null);
        } catch (ReturnException re) {
            Object returnValue = re.getReturnValue();
            return new ExecutorState(new ArrayList<>(outputs), partialExecution, null, returnValue);
        } catch (Exception e) {
            String errorMsg = "ExecuteWithContext error: " + e.getMessage();
            outputs.add("Error: " + e.getMessage());
            logger.log(Level.SEVERE, errorMsg, e);
            sessions.remove(sessionId);
            sessionOutputs.remove(sessionId);
            sessionPartialFlags.remove(sessionId);
            return new ExecutorState(outputs, false, null);
        }
    }
    
    
    public ExecutorState executeWithContextInternal(ExecutorContext context, List<ASTNode> statements) {
        List<String> outputs = new ArrayList<>();
        boolean partialExecution = false;
    
        try {
            context.setStatements(statements);
            while (context.getExecutionPointer() < context.getStatements().size()) {
                ASTNode statement = context.getStatements().get(context.getExecutionPointer());
    
                executeStatement(statement, context, outputs);
    
                if (context.isWaitingForInput()) {
                    throw new RuntimeException("Functions cannot perform read operations.");
                }
    
                context.incrementExecutionPointer();
            }
        } catch (ReturnException re) {
            return new ExecutorState(outputs, partialExecution, null, re.getReturnValue());
        } catch (Exception e) {
            String errorMsg = "ExecuteWithContextInternal error: " + e.getMessage();
            outputs.add("Error: " + e.getMessage());
            logger.log(Level.SEVERE, errorMsg, e);
            return new ExecutorState(outputs, false, null);
        }
    
        return new ExecutorState(outputs, partialExecution, null);
    }
    
    /**
     * Parses the input string into an appropriate Object type.
     *
     * @param input The input string provided by the user.
     * @return The parsed Object (Integer or Float).
     */
    private Object parseInput(String input) {
        try {
            if (input.contains(".")) {
                return Float.parseFloat(input);
            }
            return Integer.parseInt(input);
        } catch(NumberFormatException e){
            throw new RuntimeException("Invalid input format.");
        }
    }
}
