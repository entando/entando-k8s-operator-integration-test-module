package org.entando.kubernetes.model.inprocesstest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.entando.kubernetes.model.DbmsImageVendor;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class DbmsImageVendorTest {

    @Tag("in-process")
    @Test
    public void test() {
        assertThat(DbmsImageVendor.MYSQL.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:mysql://myhost.com:1234/myschema"));
        assertThat(DbmsImageVendor.POSTGRESQL.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:postgresql://myhost.com:1234/mydb"));
        assertThat(DbmsImageVendor.ORACLE.getConnectionStringBuilder().onPort("1234").toHost("myhost.com").usingSchema("myschema")
                .usingDatabase("mydb")
                .buildConnectionString(), is("jdbc:oracle:thin:@//myhost.com:1234/mydb"));
        assertThat("the oracleMavenRepo config is present", DbmsImageVendor.ORACLE.getAdditionalConfig().stream().anyMatch(configVariable ->
                configVariable.getConfigKey().equals("oracleMavenRepo") && configVariable.getEnvironmentVariable()
                        .equals("ORACLE_MAVEN_REPO")));

    }
}
