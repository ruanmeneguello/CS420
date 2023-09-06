package com.getsimplex.steptimer.utils;

public class ErrorPayload {

    private String errorMessage;

    public ErrorPayload(String errorMessage){
        this.errorMessage=errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
