/**
 * 
 */
package de.ingrid.interfaces.csw.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author joachim
 *
 */
public class Udk2CswDateFieldParser implements FieldParser {

	private final static Log log = LogFactory.getLog(Udk2CswDateFieldParser.class);
	
	private static Udk2CswDateFieldParser instance = null;
	
	private Udk2CswDateFieldParser() {	}
	
	public static Udk2CswDateFieldParser instance() {
		if (instance == null) {
			instance = new Udk2CswDateFieldParser();
		}
		return instance;
	}
	
	
	
	/* (non-Javadoc)
	 * @see de.ingrid.iplug.csw.fieldparser.FieldParser#parse(java.lang.String)
	 */
	public String parse(String value) {
        // 1999-11-11T00:00:00
        SimpleDateFormat df = new SimpleDateFormat();
        SimpleDateFormat cswFormat = new SimpleDateFormat();
        try {
        	if (value.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9][0-2][0-9][0-5][0-9][0-5][0-9]")) {
	            df.applyPattern("yyyyMMddHHmmss");
	            cswFormat.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
				return cswFormat.format(df.parse(value));
			} else if (value.matches("[0-9][0-9][0-9][0-9]0000")) {
			    df.applyPattern("yyyy");
			    cswFormat.applyPattern("yyyy");
				return cswFormat.format(df.parse(value));
			} else if (value.matches("[0-9][0-9][0-9][0-9][0-1][0-9]00")) {
			    df.applyPattern("yyyyMM");
			    cswFormat.applyPattern("yyyy-MM");
				return cswFormat.format(df.parse(value));
			} else if (value.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
			    df.applyPattern("yyyyMMdd");
			    cswFormat.applyPattern("yyyy-MM-dd");
				return cswFormat.format(df.parse(value));
	        }
		} catch (ParseException e) {
			log.error("Parsing CSW date failed (" + value + ").");
		}
    	return value;
	}

}
