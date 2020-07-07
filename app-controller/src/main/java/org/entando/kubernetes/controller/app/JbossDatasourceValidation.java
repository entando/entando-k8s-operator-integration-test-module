/*
 *
 * Copyright 2015-Present Entando Inc. (http://www.entando.com) All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 *  This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

/**
 * contains the ValidConnectionChecker and ExceptionSorted class names
 */
package org.entando.kubernetes.controller.app;

import java.util.Optional;
import org.entando.kubernetes.model.DbmsVendor;

public enum JbossDatasourceValidation {

    MYSQL("org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker",
            "org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"),
    POSTGRES("org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker",
            "org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"),
    ORACLE("org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker",
            "org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter"),
    DEFAULT("org.jboss.jca.adapters.jdbc.extensions.novendor.NullValidConnectionChecker",
            "org.jboss.jca.adapters.jdbc.extensions.novendor.NullExceptionSorter");


    private String validConnectionCheckerClassName;
    private String exceptionSorterClassName;

    JbossDatasourceValidation(String validConnectionCheckerClassName, String exceptionSorterClassName) {
        this.validConnectionCheckerClassName = validConnectionCheckerClassName;
        this.exceptionSorterClassName = exceptionSorterClassName;
    }

    public String getValidConnectionCheckerClassName() {
        return validConnectionCheckerClassName;
    }

    public String getExceptionSorterClassName() {
        return exceptionSorterClassName;
    }

    /**
     * receives a DbmsVendor and returns the corresponing JbossDatasourceValidation
     * @param dbmsVendor the DbmsVendor on which switch
     * @return the corresponing JbossDatasourceValidation
     */
    public static JbossDatasourceValidation getValidConnectionCheckerClass(DbmsVendor dbmsVendor) {

        switch (Optional.ofNullable(dbmsVendor).orElse(DbmsVendor.NONE)) {

            case MYSQL:
                return MYSQL;

            case ORACLE:
                return ORACLE;

            case POSTGRESQL:
                return POSTGRES;

            default:
                return DEFAULT;
        }
    }

}
