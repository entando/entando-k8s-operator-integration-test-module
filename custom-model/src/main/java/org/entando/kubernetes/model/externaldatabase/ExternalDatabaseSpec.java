package org.entando.kubernetes.model.externaldatabase;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.entando.kubernetes.model.DbmsImageVendor;

@JsonSerialize
@JsonDeserialize
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(fieldVisibility = Visibility.ANY, isGetterVisibility = Visibility.NONE, getterVisibility = Visibility.NONE,
        setterVisibility = Visibility.NONE)
public class ExternalDatabaseSpec {

    private String dbms;
    private String host;
    private Integer port;
    private String databaseName;
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

    public DbmsImageVendor getDbms() {
        return DbmsImageVendor.forValue(dbms);
    }

    public String getHost() {
        return host;
    }

    public String getSecretName() {
        return secretName;
    }

    public Optional<Integer> getPort() {
        return Optional.ofNullable(port);
    }

    public String getDatabaseName() {
        return databaseName;
    }

}
