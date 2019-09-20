package org.entando.kubernetes.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.fabric8.zjsonpatch.internal.guava.Strings;
import java.util.Locale;

public enum JeeServer {
    WILDFLY("entando/entando-de-app-wildfly"),
    EAP("entando/entando-de-app-eap"),
    TOMCAT("entando/entando-de-app-tomcat"),
    JETTY("entando/entando-de-app-jetty");

    private String imageName;

    JeeServer(String imageName) {
        this.imageName = imageName;
    }

    @JsonCreator
    public static JeeServer forValue(String value) {
        if (Strings.isNullOrEmpty(value)) {
            return null;
        }
        return JeeServer.valueOf(value.toUpperCase(Locale.getDefault()));
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase(Locale.getDefault());
    }

    public String getImageName() {
        return imageName;
    }
}
