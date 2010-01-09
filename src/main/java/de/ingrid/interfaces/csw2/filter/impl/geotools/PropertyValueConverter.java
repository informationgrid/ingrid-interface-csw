/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw2.filter.impl.geotools;

import de.ingrid.utils.udk.UtilsCSWDate;

public class PropertyValueConverter {

	public static String convert(String propertyName, Object value) {
		if (propertyName.equalsIgnoreCase("Modified")) {
			if (UtilsCSWDate.isCSWDate(value.toString())) {
				return UtilsCSWDate.getQueryDateStyle(value.toString());
			}
		}
		return value.toString();
	}	
}
