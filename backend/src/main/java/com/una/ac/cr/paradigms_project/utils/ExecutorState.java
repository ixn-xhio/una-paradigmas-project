package com.una.ac.cr.paradigms_project.utils;

import java.util.List;

public class ExecutorState {
    private List<String> outputs;
    private boolean requiresInput;
    private String sessionId;
    private Object returnValue; // Nuevo campo para manejar valores de retorno

    // Constructor para ejecuciones normales
    public ExecutorState(List<String> outputs, boolean requiresInput, String sessionId) {
        this.outputs = outputs;
        this.requiresInput = requiresInput;
        this.sessionId = sessionId;
        this.returnValue = null;
    }

    // Constructor sobrecargado para manejar valores de retorno
    public ExecutorState(List<String> outputs, boolean requiresInput, String sessionId, Object returnValue) {
        this.outputs = outputs;
        this.requiresInput = requiresInput;
        this.sessionId = sessionId;
        this.returnValue = returnValue;
    }

    // Getters y Setters
    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
    }
    
    public void addOutput(String output){
        outputs.add(output);
    }

    public boolean isRequiresInput() {
        return requiresInput;
    }

    public void setRequiresInput(boolean requiresInput) {
        this.requiresInput = requiresInput;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
