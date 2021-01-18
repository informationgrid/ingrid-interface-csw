/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.index;

import org.eclipse.jdt.internal.core.Assert;
import org.junit.Test;
import de.ingrid.utils.statusprovider.StatusProvider;


public class StatusProviderTest {

    @Test
    public void testStatusProvider() throws Exception {
        StatusProvider provider = new StatusProvider();

        provider.addState("key 1", "this is state 1");
        Thread.sleep(1000);
        provider.addState("key 2", "this is state 2");

        Assert.isTrue(provider.toString().contains("state 1"));
        Assert.isTrue(provider.toString().contains("state 2"));

        provider.addState("key 1", "this is a modified state 1");

        Assert.isTrue(provider.toString().contains("modified state 1"));
        Assert.isTrue(!provider.toString().contains("is state 1"));

        provider.clear();

        Assert.isTrue(provider.toString().equals(""));
    }

}
