/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response;

import junit.framework.TestCase;

/**
 * @author Administrator
 *
 */
public class IngridQueryHelperTest extends TestCase {

	/**
	 * Test method for {@link de.ingrid.interfaces.csw.transform.response.IngridQueryHelper#hasValue(java.lang.String)}.
	 */
	public void testHasValue() {
		assertEquals(false, IngridQueryHelper.hasValue(null));
		assertEquals(false, IngridQueryHelper.hasValue(""));
		assertEquals(false, IngridQueryHelper.hasValue(new String()));
		assertEquals(true, IngridQueryHelper.hasValue("1"));
	}

}
