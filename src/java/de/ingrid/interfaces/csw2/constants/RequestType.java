/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.constants;

public enum RequestType {

	GET {
		public String toString() {
			return "get";
		}
	},
	POST {
		public String toString() {
			return "post";
		}
	},
	SOAP {
		public String toString() {
			return "soap";
		}
	}
}
