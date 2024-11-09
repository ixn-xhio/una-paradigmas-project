package com.una.ac.cr.paradigms_project.utils;

public class ReturnException extends RuntimeException {
    private Object returnValue;

    public ReturnException(Object returnValue){
        this.returnValue = returnValue;
    }

    public Object getReturnValue(){
        return returnValue;
    }
}
