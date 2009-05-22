/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.constants;

/** 
 * Names used in the configuration
 */
public class ConfigurationKeys {

	/** beans.xml **/
	public static final String CSW_SERVER_IMPLEMENTATION = "cswServerImpl";
	public static final String CSW_ENCODING_IMPLEMENTATIONS = "cswMessageEncodingImpl";
	public static final String CSW_REQUEST_IMPLEMENTATIONS = "cswRequestImpl";
	public static final String CSW_RESPONSE_IMPLEMENTATION = "cswResponseImpl";

	/** ingrid-csw.properties **/
	public static final String CSW_VERSION = "csw.version";
	public static final String SERVER_PORT = "server.port";
    public static final String SERVER_INTERFACE_HOST = "server.interface.host";
    public static final String SERVER_INTERFACE_PORT = "server.interface.port";
    public static final String MAX_RETURNED_HITS = "max.returned.hits";
    public static final String CAPABILITIES_DOC = "capabilities";

    /** variables in documents **/
    public static final String VARIABLE_INTERFACE_HOST = "INTERFACE_HOST";
    public static final String VARIABLE_INTERFACE_PORT = "INTERFACE_PORT";

}
