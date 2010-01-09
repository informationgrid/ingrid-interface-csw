/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */

/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

/**
 * Interface for value parsers used to convert values
 * from CSW to UDK or vice versa.
 *  
 * @author ingo herwig <ingo@wemove.com>
 *
 */
public interface FieldParser {
	
	/**
	 * Parse a value and return the converted value. 
	 * @param value The value to parse
	 * @return The converted value
	 */
	public String parse(String value); 
}
