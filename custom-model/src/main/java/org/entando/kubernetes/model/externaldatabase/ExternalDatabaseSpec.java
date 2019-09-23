package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;

public class ExternalDatabaseSpec {

    @JsonProperty
    private String dbms;
    @JsonProperty
    private String host;
    @JsonProperty
    private Integer port;
    @JsonProperty
    private String databaseName;
    @JsonProperty
    private String secretName;

    public ExternalDatabaseSpec() {
        super();
    }

    public ExternalDatabaseSpec(DbmsImageVendor dbms, String host, Integer port, String databaseName, String secretName) {
        this.dbms = dbms.toValue();
        this.host = host;
        this.secretName = secretName;
        this.port = port;
        this.databaseName = databaseName;
    }

    @JsonIgnore
    public DbmsImageVendor getDbms() {
        return DbmsImageVendor.forValue(dbms);
    }

    @JsonIgnore
    public String getHost() {
        return host;
    }

    @JsonIgnore
    public String getSecretName() {
        return secretName;
    }

    @JsonIgnore
    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    @JsonIgnore
    public String getDatabaseName() {
        return databaseName;
    }

}
