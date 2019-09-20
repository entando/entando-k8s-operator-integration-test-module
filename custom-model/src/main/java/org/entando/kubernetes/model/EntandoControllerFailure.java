package org.entando.kubernetes.model;

public class EntandoControllerFailure {

    private String failedObjectType;
    private String failedObjectName;
    private String errorMessage;

    public String getFailedObjectType() {
        return failedObjectType;
    }

    public void setFailedObjectType(String failedObjectType) {
        this.failedObjectType = failedObjectType;
    }

    public String getFailedObjectName() {
        return failedObjectName;
    }

    public void setFailedObjectName(String failedObjectName) {
        this.failedObjectName = failedObjectName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
