/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.tools;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides access to the opensearch preferences.
 * 
 * @author joachim@wemove.com
 */
public class CSWConfig extends PropertiesConfiguration {

    // private stuff
    private static CSWConfig instance = null;

    private final static Log log = LogFactory.getLog(CSWConfig.class);
    
    public static synchronized CSWConfig getInstance() {
        if (instance == null) {
            try {
                instance = new CSWConfig();
            } catch (Exception e) {
                if (log.isFatalEnabled()) {
                    log.fatal(
                            "Error loading the portal config application config file. (ingrid-opensearch.properties)",
                            e);
                }
            }
        }
        return instance;
    }

    /**
     * Get a string value. Throws a RuntimeException, if the key does not exist
     * @param key
     * @return String
     */
    public synchronized String getStringMandatory(String key) {
		CSWConfig config = CSWConfig.getInstance();
		if (!config.containsKey(key))
			throw new RuntimeException("Unknown interface configuration key requested: "+key);
		return config.getString(key);
    }

    private CSWConfig() throws Exception {
        super("ingrid-csw.properties");
    }
}
