/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.IBusHarvester;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;

/**
 * @author ingo@wemove.com
 */
public class ConfigurationProviderTest extends TestCase {

	private static final File CONFIGURATION_FILE = new File("tmp/config.xml");

	public void testSave() throws Exception {

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

		// configure a harvesters
		List<IBusHarvester> harvesters = new ArrayList<IBusHarvester>();
		IBusHarvester harvester1 = new IBusHarvester();
		harvester1.setCommunicationXml("path/to/communication1.xml");
		harvester1.setRequestDefinitions(requests);
		harvesters.add(harvester1);

		IBusHarvester harvester2 = new IBusHarvester();
		harvester2.setCommunicationXml("path/to/communication2.xml");
		harvesters.add(harvester2);

		configuration.setHarvesters(harvesters);

		// write the configuration
		ConfigurationProvider configProvider = new ConfigurationProvider();
		configProvider.setConfigurationFile(CONFIGURATION_FILE);
		configProvider.write(configuration);

		// TODO test the xml content
	}

	public void testRead() throws Exception {
		// TODO read xml
	}
}
