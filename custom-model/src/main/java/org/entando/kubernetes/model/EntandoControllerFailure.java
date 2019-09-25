package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
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
