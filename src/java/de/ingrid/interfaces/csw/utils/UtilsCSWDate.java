/*
 * Copyright (c) 1997-2006 by wemove GmbH
 */
package de.ingrid.interfaces.csw.utils;

/**
 * This class contains date conversion helper functions.
 * 
 * @author joachim@wemove.com
 */
public class UtilsCSWDate {

	public static boolean isCSWDate(String dateString) {
		if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]")) {
			return true;
		}
		if (dateString.matches("[0-9][0-9][0-9][0-9][0-1][0-9][0-3][0-9]T[0-2][0-9][0-5][0-9][0-5][0-9]")) {
			return true;
		}
		return false;
	}

	public static String getDateWithoutTime(String dateString) {
		if (dateString != null && dateString.length() >= 8) {
			return dateString.substring(0, 8);
		}
		return dateString;
	}

	public static String getDBDateStyle(String dateString) {
		if (isCSWDate(dateString)) {
			String dateWithoutTime = getDateWithoutTime(dateString);
			return dateWithoutTime.substring(0, 4).concat("-").concat(dateWithoutTime.substring(4,6)).concat("-").concat(dateWithoutTime.substring(6,8));
		}
		return dateString;
	}
}
