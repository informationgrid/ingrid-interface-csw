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
            resources = FileUtils.getPackageContent("classpath*:gdide_test_data/**");
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
