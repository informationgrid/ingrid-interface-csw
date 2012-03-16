/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.harvest.impl.IBusHarvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;

/**
 * @author ingo@wemove.com
 */
public class IBusHarvesterTest extends TestCase {

	public void testSimple() throws Exception {
		String communicationFilename = "src/test/resources/communication.xml";

		// setup cache
		RecordCache cache = new RecordCache();
		cache.setCachePath("cache");

		// setup requests
		List<RequestDefinition> requestDefinitions = new ArrayList<RequestDefinition>();
		RequestDefinition request = new RequestDefinition();
		request.setQueryString("iplugs:\"/kug-group:kug-iplug-udk-db_uba\"");
		requestDefinitions.add(request);

		// setup harvester
		IBusHarvester iBusHarvester = new IBusHarvester();
		iBusHarvester.setCache(cache);
		iBusHarvester.setCommunicationXml(communicationFilename);
		iBusHarvester.setRequestDefinitions(requestDefinitions);

		// run
		iBusHarvester.run(new Date());
	}
}
