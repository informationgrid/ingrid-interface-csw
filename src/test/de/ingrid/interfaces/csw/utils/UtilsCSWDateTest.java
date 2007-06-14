/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

import junit.framework.TestCase;

/**
 * @author joachim
 *
 */
public class UtilsCSWDateTest extends TestCase {

	public final void testIsCSWDate() {
		assertEquals(true, UtilsCSWDate.isCSWDate("20061012"));
	}
	
	
	public final void testGetDBDateStyle() {
		assertEquals("2006-10-12", UtilsCSWDate.getDBDateStyle("20061012"));
		assertEquals("2006-10-12", UtilsCSWDate.getDBDateStyle("20061012T121247"));
	}
	
}
