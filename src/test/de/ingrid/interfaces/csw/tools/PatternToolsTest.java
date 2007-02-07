package de.ingrid.interfaces.csw.tools;

import de.ingrid.interfaces.csw.tools.PatternTools;
import junit.framework.TestCase;

public class PatternToolsTest extends TestCase {

	
	/*
	 * Test method for 'com.gistec.ingeocsw.tools.PatternTools.toValidDateFormat(String, boolean)'
	 */
	public void testToValidDateFormat() throws Exception {
		
		
		//Tests for lucene indexing
		String date = "";
		
	
        date = "1970";
		
		date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700101");
		
		
        date = "197008";
		
		date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700801");
		
		
		date = "19700815";
		
		date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700815");
		
	
		date = "19700815100631";
		
        date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700815");
		
		
		
        date = "1970-08-15";
		
		date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700815");
		
		
        
		date = "1970-08-15T10:06:31";
		
		date = PatternTools.toValidDateFormat(date, true);
		
		assertEquals(date, "19700815");
		
		
		
        //Tests for XML date format (NOT for lucene indexing)
		
		
		
		    date = "1970";
			
			date = PatternTools.toValidDateFormat(date, false);
			
			assertEquals(date, "1970-01-01");
			
			
	        date = "197008";
			
			date = PatternTools.toValidDateFormat(date, false);
			
			assertEquals(date, "1970-08-01");
		
		
        date = "19700815";
		
		date = PatternTools.toValidDateFormat(date, false);
		
		assertEquals(date, "1970-08-15");
		
	
		date = "19700815100631";
		
        date = PatternTools.toValidDateFormat(date, false);
		
		assertEquals(date, "1970-08-15");
		
		
		
        date = "1970-08-15";
		
		date = PatternTools.toValidDateFormat(date, false);
		
		assertEquals(date, "1970-08-15");
		
		
        
		date = "1970-08-15T10:06:31";
		
		date = PatternTools.toValidDateFormat(date, false);
		
		assertEquals(date, "1970-08-15");
		
		
	
		//System.out.println("DATUM: " + date);

	 }
	
	
	
//	/*
//	 * Test method for 'com.gistec.ingeocsw.tools.PatternTools.isVersionFormatValid(String)'
//	 */
//	public void testIsVersionFormatValid() throws Exception {
//
//	}
//
//	
//
//	/*
//	 * Test method for 'com.gistec.ingeocsw.tools.PatternTools.toLuceneIndexCoordFormat(String)'
//	 */
//	public void testToLuceneIndexCoordFormat() throws Exception {
//
//	}

}
