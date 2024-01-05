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
package de.ingrid.interfaces.csw.index;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.ingrid.utils.statusprovider.StatusProvider;


public class StatusProviderTest {

    @Test
    void testStatusProvider() throws Exception {
        StatusProvider provider = new StatusProvider();

        provider.addState("key 1", "this is state 1");
        Thread.sleep(1000);
        provider.addState("key 2", "this is state 2");

        Assertions.assertTrue(provider.toString().contains("state 1"));
        Assertions.assertTrue(provider.toString().contains("state 2"));

        provider.addState("key 1", "this is a modified state 1");

        Assertions.assertTrue(provider.toString().contains("modified state 1"));
        Assertions.assertTrue(!provider.toString().contains("is state 1"));

        provider.clear();

        Assertions.assertTrue(provider.toString().equals(""));
    }

}
