package org.entando.kubernetes.model;

public abstract class JdbcConnectionStringBuilder {

    private String host;
    private String port;
    private String database;
    private String schema;

    public JdbcConnectionStringBuilder toHost(String host) {
        this.host = host;
        return this;
    }

    public JdbcConnectionStringBuilder onPort(String port) {
        this.port = port;
        return this;
    }

    public JdbcConnectionStringBuilder usingDatabase(String database) {
        this.database = database;
        return this;
    }

    public JdbcConnectionStringBuilder usingSchema(String schema) {
        this.schema = schema;
        return this;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getSchema() {
        return schema;
    }

    public abstract String buildConnectionString();

}
