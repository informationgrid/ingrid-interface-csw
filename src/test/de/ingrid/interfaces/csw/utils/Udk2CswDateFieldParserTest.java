/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

import junit.framework.TestCase;

/**
 * @author Administrator
 *
 */
public class Udk2CswDateFieldParserTest extends TestCase {

	/**
	 * Test method for {@link de.ingrid.interfaces.csw.utils.Udk2CswDateFieldParser#parse(java.lang.String)}.
	 */
	public void testParse() {
		assertEquals("2001", Udk2CswDateFieldParser.instance().parse("20010000"));
		assertEquals("2001-12", Udk2CswDateFieldParser.instance().parse("20011200"));
		assertEquals("2001-12-23", Udk2CswDateFieldParser.instance().parse("20011223"));
		assertEquals("2001-12-23T12:13:14", Udk2CswDateFieldParser.instance().parse("20011223121314"));
		
		assertEquals("2001-12-23", Udk2CswDateFieldParser.instance().parseToDate("20011223121314"));
		assertEquals("2001-12-23", Udk2CswDateFieldParser.instance().parseToDate("20011223"));
		assertEquals("assdfdfdf", Udk2CswDateFieldParser.instance().parseToDate("assdfdfdf"));
	}

}
