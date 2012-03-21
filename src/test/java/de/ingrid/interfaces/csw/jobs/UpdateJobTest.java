/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.jobs;

import java.io.File;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;

/**
 * @author ingo@wemove.com
 */
public class UpdateJobTest extends TestCase {

	private static final File CONFIGURATION_FILE = new File("src/test/resources/config-updatejobtest.xml");

	public void testSimple() throws Exception {

		ConfigurationProvider configProvider = new ConfigurationProvider();
		configProvider.setConfigurationFile(CONFIGURATION_FILE);

		UpdateJob job = new UpdateJob();
		job.setConfigurationProvider(configProvider);
		job.execute();
	}
}
