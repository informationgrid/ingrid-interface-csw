/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.enums;

public enum ElementSetName {
	BRIEF {
		public String toString() {
			return "brief";
		}
	},
	SUMMARY {
		public String toString() {
			return "summary";
		}
	},
	FULL {
		public String toString() {
			return "full";
		}
	}
}
