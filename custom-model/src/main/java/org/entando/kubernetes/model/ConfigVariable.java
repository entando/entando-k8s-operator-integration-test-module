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

package org.entando.kubernetes.model;

public class ConfigVariable {

    private final String configKey;
    private final String environmentVariable;

    public ConfigVariable(String configKey, String environmentVariable) {
        this.configKey = configKey;
        this.environmentVariable = environmentVariable;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getEnvironmentVariable() {
        return environmentVariable;
    }
}
