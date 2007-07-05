/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.net.URL;

import de.ingrid.utils.IBus;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides access to the csw-interface preferences.
 * 
 * @author dirk.schwarzmann@wemove.com
 */

public class CSWInterfaceConfig extends PropertiesConfiguration {

    // private stuff
    private static CSWInterfaceConfig instance = null;

    private final static Log log = LogFactory.getLog(CSWInterfaceConfig.class);
    
    public static final String CSW_VERSION = "csw.version";
    
    public static final String RESPONSE_ENCODING = "responseEncoding";
    
    public static final String FILE_GETCAPABILITIES = "capabilities";

    public static final String FILE_DESCRIBERECORD = "describerecord";

    public static final String FILE_OUTPUTXSL = "outputXSL";

    public static final String FILE_OUTPUTXSL_FULL = "transform.xsl.records.profile.full";
    
    public static final String FILE_OUTPUTXSL_SUMMARY = "transform.xsl.records.profile.summary";
    
    public static final String FILE_OUTPUTXSL_BRIEF = "transform.xsl.records.profile.brief";

    public static final String FILE_FAKERESPONSE = "fakeResponse";
    
    public static final String MAX_RECORDS = "maxRecords";
    
    public static final String TIMEOUT = "timeOut";
    
    private static IBus myIBus = null;

    public static synchronized CSWInterfaceConfig getInstance() {
        if (instance == null) {
            try {
                instance = new CSWInterfaceConfig();
            } catch (Exception e) {
                if (log.isFatalEnabled()) {
                    log.fatal(
                            "Error loading the csw-interface config application config file. (cswinterface.properties)",
                            e);
                }
            }
        }
        return instance;
    }

    private CSWInterfaceConfig() throws Exception {
        super("ingrid-csw.properties");
    }
    
    /**
     * Returns the full path and file name of the given file identifier (use the constants named "FILE_***")
     * in the notation of an URL, that is, with a protocol prefix like "http://" or "file:/".
     * If you only need the pure file name, use method getString(<file identifier>) instead.
     * 
     * @param fileIdentifier A constant of this class starting with "FILE_"
     * @return Full path including file name
     */
    public String getUrlPath(String fileIdentifier) {
    	String result = null;
    	String file = null;
    	file = getString(fileIdentifier);
    	
    	// truncate a possible leading slash
        String stripped = file.startsWith("/") ? file.substring(1) : file;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            URL url = classLoader.getResource(stripped);
            if (url != null) {
            	result = url.toString();
            	log.debug("Found via thread-classloader: " + result);
            }
        }
        // ??? Was soll das ???
        /*
        if (result == null) {
        	CSWInterfaceConfig.class.getResourceAsStream(file);
        }
        */
        if (result == null) {
            URL url = CSWInterfaceConfig.class.getClassLoader().getResource(stripped);
            if (url != null) {
            	result = url.toString();
            	log.debug("Found via thisclass-classloader: " + result);
            }
        }
        
        return result;
    }
    
    public synchronized void setIBus(IBus iBus) {
    	myIBus = iBus;
    }
    
    public IBus getIBus() {
    	return myIBus;
    }
}
