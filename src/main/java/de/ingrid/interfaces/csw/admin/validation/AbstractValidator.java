/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.interfaces.csw.admin.validation;

import java.lang.reflect.ParameterizedType;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

public abstract class AbstractValidator<T> {

    @SuppressWarnings("unchecked")
    private final Class<T> _typeClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

    public static final String getErrorKey(final Class<?> clazz, final String field, final String error) {
        return clazz.getSimpleName() + "." + field + "." + error;
    }

    public static final Object get(final Errors errors, final String field) {
        return errors.getFieldValue(field);
    }

    public static final Boolean getBoolean(final Errors errors, final String field) {
        return (Boolean) errors.getFieldValue(field);
    }

    public static final Integer getInteger(final Errors errors, final String field) {
        return (Integer) errors.getFieldValue(field);
    }

    public static final Float getFloat(final Errors errors, final String field) {
        return (Float) errors.getFieldValue(field);
    }

    public static final String getString(final Errors errors, final String field) {
        return (String) errors.getFieldValue(field);
    }

    public void rejectError(final Errors errors, final String field, final String error) {
        errors.rejectValue(field, getErrorKey(_typeClass, field, error));
    }

    public void rejectIfEmptyOrWhitespace(final Errors errors, final String field) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, field, getErrorKey(_typeClass, field, "empty"));
    }

    public void rejectIfNull(final Errors errors, final String field) {
        if (null == get(errors, field)) {
            rejectError(errors, field, "null");
        }
    }

    public void rejectIfNullOrEmpty(final Errors errors, final String field) {
        final Object[] arr = (Object[]) get(errors, field);
        if (null == arr || arr.length == 0) {
            rejectError(errors, field, "null");
        }
    }
    
    public void rejectIfNaN(final Errors errors, final String field) {
        Object val = errors.getFieldValue(field);
        if (val == null || (val instanceof Double && Double.isNaN((Double) val))) {
            rejectError(errors, field, "invalid");
        }
        
    }
}
