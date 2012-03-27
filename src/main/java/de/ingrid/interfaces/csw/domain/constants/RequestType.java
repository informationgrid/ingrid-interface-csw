/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

public enum RequestType {

	GET {
		@Override
		public String toString() {
			return "get";
		}
	},
	POST {
		@Override
		public String toString() {
			return "post";
		}
	},
	SOAP {
		@Override
		public String toString() {
			return "soap";
		}
	}
}
