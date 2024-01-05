/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
package de.ingrid.interfaces.csw.domain.constants;

/**
 * Names used in the configuration
 */
public class ConfigurationKeys {

    /** ingrid-csw.properties **/
    public static final String RESPONSE_ENCODING = "responseEncoding";
    public static final String SERVER_PORT = "server.port";
    public static final String SERVER_WEBAPP = "server.webapp";
    public static final String SERVER_INTERFACE_HOST = "server.interface.host";
    public static final String SERVER_INTERFACE_PORT = "server.interface.port";
    public static final String SERVER_INTERFACE_PATH = "server.interface.path";
    public static final String SERVER_INTERFACE_PATH_CSWT = "server.interface.cswt.path";
    public static final String MAX_RETURNED_HITS = "max.returned.hits";
    public static final String CAPABILITIES_DOC = "capabilities";
    public static final String RECORDDESC_DOC = "describerecord";
    public static final String INGRID_ADMIN_PASSWORD = "ingrid.admin.password";
    public static final String CSWT_USERS = "cswt.users";
    public static final String CACHE_ENABLE = "cache.enable";
    public static final String CACHE_ENABLE_HARVEST = "cache.harvest.enable";
    public static final String QUERY_PARAMETER_2_CONSTRAINTS = "query.parameter.to.constraints";
    public static final String QUERY_PARAMETER_2_CAPABILITIES_VARIANT = "query.parameter.to.capabilities.variant";
    public static final String HARVESTER_IBUS_DATATYPES_ALLOW = "harvester.ibus.datatypes.allow";
    public static final String HARVESTER_IBUS_DATATYPES_DENY = "harvester.ibus.datatypes.deny";
    public static final String IDF_2_FULL_PROCESSING_XSLT = "idf.to.full.processing.xslt";

    public static final String IDF_2_FULL_PROCESSING_XSLT_OGC = "idf.to.full.processing.xslt.ogc";
    /**
     * Total timeout in ms for folder operations delete and move. This is
     * necessary on windows, because on some systems the index file resources
     * are not released immediately.
     **/
    public static final String FILE_OPERATION_TIMEOUT = "file.operation.timeout";

    /**
     * Timeout in ms between retry index folder operations delete and move. This
     * is necessary on windows, because on some systems the index file resources
     * are not released immediately. Operations are retried until the
     * file.operation.timeout is reached.
     **/
    public static final String FILE_OPERATION_RETRY_TIMEOUT = "file.operation.retry.timeout";

    /** variables in documents **/
    public static final String VARIABLE_INTERFACE_HOST = "INTERFACE_HOST";
    public static final String VARIABLE_INTERFACE_PORT = "INTERFACE_PORT";
    public static final String VARIABLE_INTERFACE_PATH = "INTERFACE_PATH";
    public static final String VARIABLE_INTERFACE_PATH_CSWT = "INTERFACE_CSW-T_PATH";

    /** static configuration keys **/
    public static final String CSW_VERSION_2_0_2 = "2.0.2";

}
