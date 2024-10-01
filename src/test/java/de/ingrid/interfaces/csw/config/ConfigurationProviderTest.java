/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.config.model.impl.IBusHarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.tools.FileUtils;

/**
 * @author ingo@wemove.com
 */
public class ConfigurationProviderTest {

	private static final File CONFIGURATION_FILE = new File("tmp/config.xml");
	private static final String CACHE_PATH = "tmp/cache";

    @Test
    public void testSave() throws Exception {

		try {
    	    Configuration configuration = new Configuration();
    
    		// configure request definitions
    		List<RequestDefinition> requests = new ArrayList<RequestDefinition>();
    		RequestDefinition request1 = new RequestDefinition();
    		request1.setPause(10);
    		request1.setQueryString("iplugs:\"/kug-group:kug-iplug-udk-db_uba\"");
    		request1.setRecordsPerCall(100);
    		request1.setTimeout(1000);
    		requests.add(request1);
    
    		RequestDefinition request2 = new RequestDefinition();
    		request2.setPause(20);
    		request2.setQueryString("iplugs:\"/ingrid-group:iplug-csw-dsc-be\"");
    		request2.setRecordsPerCall(200);
    		request2.setTimeout(2000);
    		requests.add(request2);
    
    		// configure record cache
    		// NOTE several harvester can use the same configuration instance
    		RecordCacheConfiguration cache = new RecordCacheConfiguration();
    		cache.setCachePath(new File(CACHE_PATH));
    
    		// configure a harvesters
    		List<HarvesterConfiguration> harvesters = new ArrayList<HarvesterConfiguration>();
    		IBusHarvesterConfiguration harvester1 = new IBusHarvesterConfiguration();
    		harvester1.setCommunicationXml("path/to/communication1.xml");
    		harvester1.setRequestDefinitions(requests);
    		harvester1.setCacheConfiguration(cache);
    		harvesters.add(harvester1);
    
    		IBusHarvesterConfiguration harvester2 = new IBusHarvesterConfiguration();
    		harvester2.setCommunicationXml("path/to/communication2.xml");
    		harvester2.setCacheConfiguration(cache);
    		harvesters.add(harvester2);
    
    		configuration.setHarvesterConfigurations(harvesters);
    
    		// write the configuration
    		ConfigurationProvider configProvider = new ConfigurationProvider();
    		configProvider.setConfigurationFile(CONFIGURATION_FILE);
    		configProvider.write(configuration);
    
    		// TODO test the xml content
		} finally {
		
    		File tmp = new File("tmp");
		    if (tmp.exists()) {
		        FileUtils.deleteRecursive(tmp);
    		}
		}
	}

    @Test
    public void testRead() throws Exception {
		// TODO read xml
	}
}
