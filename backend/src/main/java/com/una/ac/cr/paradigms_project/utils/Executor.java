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
        } if (statement instanceof ObjectInstantiationNode) {
            ObjectInstantiationNode instantiationNode = (ObjectInstantiationNode) statement;
        
            String className = instantiationNode.getClassName();
            String instanceName = instantiationNode.getVariableName();
        
            // Retrieve the class definition from the context
            ClassDef classDef = context.getClass(className);
            if (classDef == null) {
                throw new RuntimeException("Class not found: " + className);
            }
        
            // Create the instance and initialize fields
            Map<String, Object> instance = new HashMap<>();
            instance.put("__class__", className); // Store class name for method resolution
        
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) classDef.getFields()).entrySet()) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                
                if (fieldValue instanceof List<?>) {
                    List<?> fieldList = (List<?>) fieldValue;
        
                    // Handle array or list fields by collecting all elements
                    List<Object> collectedValues = new ArrayList<>();
                    for (Object fieldItem : fieldList) {
                        if (fieldItem instanceof FieldNode) {
                            FieldNode fieldNode = (FieldNode) fieldItem;
        
                            Object defaultValue = fieldNode.getExpression() != null
                                ? fieldNode.getExpression().evaluate(context)
                                : getDefaultFieldValue(fieldNode.getType());
        
                            collectedValues.add(defaultValue);
                        } else if (fieldItem instanceof Integer || fieldItem instanceof Float || fieldItem instanceof String) {
                            collectedValues.add(fieldItem);
                        } else {
                            throw new RuntimeException("Unexpected item in field list: " + fieldItem.getClass().getName());
                        }
                    }
        
                    // Store the entire list as a field value
                    instance.put(fieldName, collectedValues);
                } else if (fieldValue instanceof FieldNode) {
                    FieldNode fieldNode = (FieldNode) fieldValue;
            
                    Object defaultValue = fieldNode.getExpression() != null
                        ? fieldNode.getExpression().evaluate(context)
                        : getDefaultFieldValue(fieldNode.getType());
            
                    if (fieldNode.getType().startsWith("Array")) {
                        List<Object> arrayList = new ArrayList<>();
                        if (defaultValue instanceof List) {
                            arrayList.addAll((List<?>) defaultValue);
                        } else if (defaultValue instanceof ArrayLiteralNode) {
                            arrayList.addAll(((ArrayLiteralNode) defaultValue).getElements());
                        } else {
                            throw new RuntimeException("Expected ArrayLiteralNode or List for field " + fieldNode.getName());
                        }
                        instance.put(fieldNode.getName(), arrayList);
                    } else {
                        instance.put(fieldNode.getName(), defaultValue);
                    }
                } else if (fieldValue instanceof Integer || fieldValue instanceof Float || fieldValue instanceof String || fieldValue instanceof Boolean) {
                    // Direct Integer, Float, or String field handling
                    instance.put(fieldName, fieldValue);
                } else {
                    throw new RuntimeException("Unexpected field type in class definition: " + fieldValue.getClass().getName());
                }
            }            
        
            // Store the instance in the context
            context.setVariable(instanceName, instance);
            System.out.println("Instance created: " + instanceName + " with fields " + instance);
        } else if (statement instanceof ObjectFieldAssignmentNode) {
            ObjectFieldAssignmentNode fieldAssign = (ObjectFieldAssignmentNode) statement;
            Object value = fieldAssign.getExpression().evaluate(context);
            context.setVariableValue(fieldAssign.getObjectName() + "." + fieldAssign.getFieldName(), value);
            logger.info("Field '" + fieldAssign.getObjectName() + "." + fieldAssign.getFieldName() + "' set to: " + value);
        }
        else if (statement instanceof DoWhileNode) {
            DoWhileNode doWhileNode = (DoWhileNode) statement;
        
            logger.info("Executing DoWhileNode");
        
            do {
                for (ASTNode bodyStatement : doWhileNode.getBody()) {
                    executeStatement(bodyStatement, context, outputs); // Add an empty list or appropriate argument
                    // Check if execution should pause (e.g., waiting for input)
                    if (context.isWaitingForInput()) {
                        return;
                    }
                }
            } while (Boolean.TRUE.equals(doWhileNode.getCondition().evaluate(context)));
        
            logger.info("DoWhileNode execution completed.");
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
        else if (statement instanceof IfNode) {
            IfNode ifNode = (IfNode) statement;
            boolean condition = (boolean) ifNode.getCondition().evaluate(context);
            
            logger.info("IfNode condition evaluated to: " + condition);
            
            // First, check if the 'if' condition is true
            if (condition) {
                // Execute the trueBranch
                for (ASTNode branchStatement : ifNode.getTrueBranch()) {
                    executeStatement(branchStatement, context, outputs);
                    if (context.isWaitingForInput()) {
                        return;  // Stop execution if waiting for input
                    }
                }
            } else {
                // Check for else-if branches (evaluate in order)
                boolean elseIfExecuted = false;
                for (IfNode elseIfNode : ifNode.getElseIfBranches()) {
                    boolean elseIfCondition = (boolean) elseIfNode.getCondition().evaluate(context);
                    logger.info("ElseIfNode condition evaluated to: " + elseIfCondition);
                    
                    if (elseIfCondition) {
                        // Execute the trueBranch of the first matching else-if
                        for (ASTNode branchStatement : elseIfNode.getTrueBranch()) {
                            executeStatement(branchStatement, context, outputs);
                            if (context.isWaitingForInput()) {
                                return;  // Stop execution if waiting for input
                            }
                        }
                        elseIfExecuted = true;
                        break;  // Only execute the first else-if that is true
                    }
                }
                
                // If no else-if was executed, execute the falseBranch
                if (!elseIfExecuted && ifNode.getFalseBranch() != null) {
                    for (ASTNode branchStatement : ifNode.getFalseBranch()) {
                        executeStatement(branchStatement, context, outputs);
                        if (context.isWaitingForInput()) {
                            return;  // Stop execution if waiting for input
                        }
                    }
                }
            }
        } else if (statement instanceof ForRangeNode) {
            ForRangeNode forNode = (ForRangeNode) statement;
            List<ASTNode> bodyToExecute = forNode.getBody();

            logger.info("Executing forNode with body of size: " + bodyToExecute.size());
        
            int start = (int) forNode.getStartExpr().evaluate(context); 
            int end = (int) forNode.getEndExpr().evaluate(context); 
        
            int increment = 1;
            if (forNode.getIncrementExpr() != null) {
                increment = (int) forNode.getIncrementExpr().evaluate(context); 
            }
            for (int i = start; i <= end; i += increment) {
                for (ASTNode branchStatement : bodyToExecute) {
                    executeStatement(branchStatement, context, outputs);
                    
                    if (context.isWaitingForInput()) {
                        return;
                    }
                }
            }
            
            logger.info("Start of cicle: " + start);
            logger.info("End of cicle " + end);
            logger.info("Iterative value: " + increment);
            
        } else if (statement instanceof ForRangeNodeWithIterator) {
            ForRangeNodeWithIterator forNode = (ForRangeNodeWithIterator) statement;
            List<ASTNode> bodyToExecute = forNode.getBody();
        
            int start = (int) forNode.getStartExpr().evaluate(context); 
            int end = (int) forNode.getEndExpr().evaluate(context); 
            int increment = 1;
            if (forNode.getIncrementExpr() != null) {
                increment = (int) forNode.getIncrementExpr().evaluate(context); 
            }
        
            String iteratorVar = forNode.getIterator();
        
            for (int i = start; i <= end; i += increment) {
                context.setVariable(iteratorVar, i);
                
                System.out.println("ForRangeNodeWithIterator - Iteration: " + i + ", Iterator Variable: " + iteratorVar);
        
                for (ASTNode branchStatement : bodyToExecute) {
                    executeStatement(branchStatement, context, outputs);
                }
            }
        
            logger.info("Executed ForRangeNodeWithIterator from " + start + " to " + end + " with step " + increment);
        } else if (statement instanceof ForArrayNode) {
            ForArrayNode forArrayNode = (ForArrayNode) statement;
            List<ASTNode> bodyToExecute = forArrayNode.getBody();
            String indexVar = forArrayNode.getIndexVar();
            ExpressionNode arrayExpr = forArrayNode.getArrayExpr();
        
            // Evaluate the array expression (e.g., obj.ax)
            Object arrayObj = arrayExpr.evaluate(context);
        
            // Ensure the result is a List or Array
            if (arrayObj instanceof List) {
                List<?> list = (List<?>) arrayObj;
                for (int i = 0; i < list.size(); i++) {
                    context.setVariable(indexVar, list.get(i));
                    for (ASTNode branchStatement : bodyToExecute) {
                        executeStatement(branchStatement, context, outputs);
                        if (context.isWaitingForInput()) {
                            return;
                        }
                    }
                }
            } else if (arrayObj instanceof Object[]) {
                Object[] array = (Object[]) arrayObj;
                for (int i = 0; i < array.length; i++) {
                    context.setVariable(indexVar, array[i]);
                    for (ASTNode branchStatement : bodyToExecute) {
                        executeStatement(branchStatement, context, outputs);
                        if (context.isWaitingForInput()) {
                            return;
                        }
                    }
                }
            } else {
                throw new RuntimeException("Expected an array or list for the for-each loop, but got: " + arrayObj);
            }
        } else {
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
                    // deuda tecnica xd
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

    private Object getDefaultFieldValue(String type) {
        switch (type) {
            case "int":
                return 0;
            case "float":
                return 0.0f;
            case "boolean":
                return false;
            default:
                return null; // Default for objects
        }
    }
    
}
