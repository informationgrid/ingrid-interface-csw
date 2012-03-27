/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

public enum OutputFormat {
	APPLICATION_XML {
		@Override
		public String toString() {
			return "application/xml";
		}
	},
	TEXT_XML {
		@Override
		public String toString() {
			return "text/xml";
		}
	}
}
