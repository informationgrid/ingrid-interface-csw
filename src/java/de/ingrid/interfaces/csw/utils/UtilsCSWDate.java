/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.interfaces.csw.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import de.ingrid.utils.udk.UtilsDate;

/**
 * This class contains date conversion helper functions.
 * 
 * @author joachim@wemove.com
 */
public class UtilsCSWDate {

	
    private final static Log log = LogFactory.getLog(UtilsDate.class);
	
	public static boolean isCSWDate(String dateString) {
		return getDatePattern(dateString) != null;
	}

	public static String getDateWithoutTime(String dateString) {
		if (dateString != null && dateString.length() >= 8) {
			return dateString.substring(0, 8);
		}
		return dateString;
	}

	public static String getQueryDateStyle(String dateString) {
		
		String srcPattern = getDatePattern(dateString);
		
		if (srcPattern != null) {
			return UtilsDate.convertDateString(dateString, srcPattern, "yyyyMMdd");
		} else {
			log.warn("Unrecognizes date type: " + dateString);
			return dateString;
		}
	}
	
	public static String getDatePattern(String dateString) {
		String datePattern = null;
		if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
			datePattern = "yyyyMMdd";
		} else if (dateString.matches("[0-9][0-9][0-9][0-9]-[0-1][0-9]-[0-3][0-9]")) {
			datePattern = "yyyy-MM-dd";
		} else if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]T[0-2][0-9][0-5][0-9][0-5][0-9]")) {
			datePattern = "yyyyMMdd'T'HHmmss";
		}
		return datePattern;
	}
}
