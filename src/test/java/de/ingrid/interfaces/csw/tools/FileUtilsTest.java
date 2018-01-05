/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.interfaces.csw.tools;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;
import org.springframework.core.io.Resource;

import com.vividsolutions.jts.util.Assert;

/**
 * @author joachim@wemove.com
 * 
 */
public class FileUtilsTest {

    /**
     * Test method for
     * {@link de.ingrid.interfaces.csw.tools.FileUtils#getPackageContent(java.lang.String)}
     * .
     */
    @Test
    public void testGetPackageContent() {
        Resource[] resources = null;
        try {
            resources = FileUtils.getPackageContent("classpath*:gdide_test_data/*xml");
        } catch (IOException e) {
            fail("Test failed");
        }
        Assert.isTrue(resources != null);
        Assert.isTrue(resources.length > 3);
        try {
            for (Resource r : resources) {
                String str = FileUtils.convertStreamToString(r.getInputStream());
                Assert.isTrue(str != null && str.length() > 0);
            }
        } catch (IOException e) {
            fail("Test failed");
        }
    }

}
