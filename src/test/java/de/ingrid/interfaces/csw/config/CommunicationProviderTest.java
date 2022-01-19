/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
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

import junit.framework.TestCase;
import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationClient;
import de.ingrid.interfaces.csw.tools.FileUtils;

/**
 * @author joachim@wemove.com
 */
public class CommunicationProviderTest extends TestCase {

    private static final File CONFIGURATION_FILE = new File("src/test/resources/communication-testcase.xml");
    private static final File CONFIGURATION_FILE_TMP = new File("communication-testcase-tmp.xml").getAbsoluteFile();

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
            FileUtils.deleteRecursive(CONFIGURATION_FILE_TMP);
        }

    }

    public void testLoad() throws Exception {

        CommunicationProvider communicationProvider = new CommunicationProvider();
        communicationProvider.setConfigurationFile(CONFIGURATION_FILE);

        Communication configuration = communicationProvider.getConfiguration();

        assertEquals("/kug-group:csw-interface-test", configuration.getClient().getName());

    }

}
