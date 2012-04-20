/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.config;

import java.io.File;

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationClient;

/**
 * @author joachim@wemove.com
 */
public class CommunicationProviderTest extends TestCase {

    private static final File CONFIGURATION_FILE = new File("src/test/resources/communication-testcase.xml");
    private static final File CONFIGURATION_FILE_TMP = new File("communication-testcase-tmp.xml");

    public void testSave() throws Exception {

        CommunicationProvider communicationProvider = new CommunicationProvider();
        communicationProvider.setConfigurationFile(CONFIGURATION_FILE_TMP);

        Communication communication = new Communication();
        CommunicationClient client = new CommunicationClient();
        client.setName("dummy");
        communication.setClient(client);

        communicationProvider.write(communication);

        assertEquals(true, CONFIGURATION_FILE_TMP.exists());

        if (CONFIGURATION_FILE_TMP.exists()) {
            CONFIGURATION_FILE_TMP.delete();
        }

    }

    public void testLoad() throws Exception {

        CommunicationProvider communicationProvider = new CommunicationProvider();
        communicationProvider.setConfigurationFile(CONFIGURATION_FILE);

        Communication configuration = communicationProvider.getConfiguration();

        assertEquals("/kug-group:csw-interface-test", configuration.getClient().getName());

    }

}
