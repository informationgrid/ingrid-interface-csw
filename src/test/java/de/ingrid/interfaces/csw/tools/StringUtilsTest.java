package de.ingrid.interfaces.csw.tools;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

    @Test
    public void testSplitByFirstOccurence() {
        String[] segments = StringUtils.splitByFirstOccurence("path/::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", "::");
        assertEquals("path/", segments[0]);
        assertEquals("urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", segments[1]);
        segments = StringUtils.splitByFirstOccurence("::urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", "::");
        assertEquals("", segments[0]);
        assertEquals("urn:x-wmo:md:eu.baltrad.SE::baltrad.SE::", segments[1]);
    }

}
