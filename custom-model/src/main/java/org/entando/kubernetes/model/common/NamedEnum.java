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

package org.entando.kubernetes.model.common;

import com.google.common.base.CaseFormat;
import java.util.Locale;

public interface NamedEnum {

    String name();

    default String getCamelCaseName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    default String getHyphenatedName() {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
    }

    static <T extends NamedEnum> T resolve(T[] values, String name) {
        if (name == null) {
            return null;
        }
        String nameToMatch = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z\\d]", "");
        for (T value : values) {
            if (nameToMatch.equals(value.name().toLowerCase(Locale.ROOT).replaceAll("[^a-z\\d]", ""))) {
                return value;
            }
        }
        return null;
    }

}
