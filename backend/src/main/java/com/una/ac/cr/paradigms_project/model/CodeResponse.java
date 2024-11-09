package com.una.ac.cr.paradigms_project.model;

import java.util.List;

public class CodeResponse {
    private List<String> outputs;
    private boolean requiresInput;
    private String sessionId;

    public List<String> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<String> outputs) {
        this.outputs = outputs;
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
}
