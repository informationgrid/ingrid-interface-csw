/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.harvest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;

/**
 * @author ingo@wemove.com
 */
public class IBusHarvesterTestLocal extends TestCase {

	private static final String COMMUNICATION_XML_PATH = "src/test/resources/communication.xml";
	private static final String CACHE_PATH = "tmp/cache";

	public void testSimple() throws Exception {
		String communicationFilename = COMMUNICATION_XML_PATH;

		// setup cache
		RecordCache cache = new RecordCache();
		cache.setCachePath(new File(CACHE_PATH));

		// setup requests
		List<RequestDefinition> requestDefinitions = new ArrayList<RequestDefinition>();
		RequestDefinition request = new RequestDefinition();
		request.setQueryString("iplugs:\"/kug-group:kug-iplug-udk-db_be\" ranking:score");
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
