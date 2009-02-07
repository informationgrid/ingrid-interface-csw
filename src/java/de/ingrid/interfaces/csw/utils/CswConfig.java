/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.utils;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides access to the opensearch preferences.
 * 
 * @author joachim@wemove.com
 */
public class CswConfig extends PropertiesConfiguration {

    // private stuff
    private static CswConfig instance = null;

    private final static Log log = LogFactory.getLog(CswConfig.class);

    public static final String SERVER_PORT = "server.port";

    public static final String SERVER_INTERFACE_HOST = "server.interface.host";
    public static final String SERVER_INTERFACE_PORT = "server.interface.port";
    
    public static final String KEY_INTERFACE_HOST = "INTERFACE_HOST";
    public static final String KEY_INTERFACE_PORT = "INTERFACE_PORT";

    public static final String MAX_RETURNED_HITS = "max.returned.hits";
    
    public static synchronized CswConfig getInstance() {
        if (instance == null) {
            try {
                instance = new CswConfig();
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

    private CswConfig() throws Exception {
        super("ingrid-csw.properties");
    }
}
