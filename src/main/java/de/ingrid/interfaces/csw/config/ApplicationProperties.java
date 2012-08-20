/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config;

import java.io.IOException;
import java.util.Properties;

/**
 * Provides access to config.properties.
 * 
 * @author ingo@wemove.com
 */
public class ApplicationProperties {

    private static final String CONFIG_PROPERTIES_FILE = "config.properties";

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
}