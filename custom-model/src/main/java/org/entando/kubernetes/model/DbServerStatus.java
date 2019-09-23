package org.entando.kubernetes.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
public class DbServerStatus extends AbstractServerStatus {

    public DbServerStatus() {
        super();
    }

    public DbServerStatus(String qualifier) {
        super(qualifier);
    }
}
