/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Provides access to config.properties.
 * 
 * @author ingo@wemove.com
 */
public class ApplicationProperties {

    private static final String CONFIG_PROPERTIES_FILE = "config.properties";
    private static final String CONFIG_PROPERTIES_OVERRIDE_FILE = "config.override.properties";

    private static Properties properties = null;

    /**
     * Load the properties if not done already.
     */
    private static void initialize() {
        if (properties == null) {
            try {
                properties = new Properties();
                properties.load(ApplicationProperties.class.getClassLoader()
                        .getResourceAsStream(CONFIG_PROPERTIES_FILE));
                
                // apply override configuration
                Properties overrideProps = new Properties();
                InputStream overrideStream = ApplicationProperties.class.getClassLoader().getResourceAsStream(CONFIG_PROPERTIES_OVERRIDE_FILE);
                if (overrideStream != null) {
                    overrideProps.load( overrideStream );
                    Enumeration<Object> keys = overrideProps.keys();
                    while (keys.hasMoreElements()) {
                        Object key = keys.nextElement();
                        properties.put( key, overrideProps.get( key ) );
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Missing configuration '" + CONFIG_PROPERTIES_FILE + "'.");
            }
        }
    }

    /**
     * Get the value for the given property name.
     * 
     * @param propertyName
     * @return String
     */
    public static String get(String propertyName) {
        initialize();
        return properties.getProperty(propertyName);
    }

    /**
     * Get a value as Integer.
     * 
     * @param propertyName
     * @param defaultValue
     * @return
     * @throws NumberFormatException
     */
    public static Integer getInteger(String propertyName, Integer defaultValue) throws NumberFormatException {
        initialize();
        if (!properties.containsKey(propertyName)) {
            return defaultValue;
        }
        return Integer.parseInt(properties.getProperty(propertyName));
    }

    /**
     * Get a value as Boolean. The boolean returned represents the value true if
     * the string argument is not null and is equal, ignoring case, to the
     * string "true".
     * 
     * @param propertyName
     * @param defaultValue
     * @return
     */
    public static Boolean getBoolean(String propertyName, Boolean defaultValue) {
        initialize();
        if (!properties.containsKey(propertyName)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(properties.getProperty(propertyName));
    }

    /**
     * Get the value for the given property name. If the property does not
     * exist, the given default value will be returned.
     * 
     * @param propertyName
     * @param defaultValue
     * @return String
     */
    public static String get(String propertyName, String defaultValue) {
        initialize();
        if (!properties.containsKey(propertyName)) {
            return defaultValue;
        }
        return properties.getProperty(propertyName);
    }

    /**
     * Get the value for the given property name. Throws an exception, if the
     * key does not exist.
     * 
     * @param propertyName
     * @return String
     */
    public static String getMandatory(String propertyName) {
        initialize();
        if (!properties.containsKey(propertyName)) {
            throw new RuntimeException("The property '" + propertyName + "' is not defined in '"
                    + CONFIG_PROPERTIES_FILE + "'");
        }
        return get(propertyName);
    }

    public static void setProperty(String name, String value) {
        initialize();
        properties.setProperty(name, value);
    }

}
