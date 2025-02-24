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

package org.entando.kubernetes.app.controller.testutils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import io.fabric8.kubernetes.api.model.Container;
import org.entando.kubernetes.test.common.FluentTraversals;

public interface EnvVarAssertionHelper extends FluentTraversals {

    default void assertConnectionValidation(Container dbPopulationJob, String schemaPrefix) {
        assertThat(theVariableNamed(schemaPrefix + "CONNECTION_CHECKER").on(dbPopulationJob),
                is("org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker"));
        assertThat(theVariableNamed(schemaPrefix + "EXCEPTION_SORTER").on(dbPopulationJob),
                is("org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"));
    }
}
