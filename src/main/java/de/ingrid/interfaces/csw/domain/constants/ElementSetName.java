/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

public enum ElementSetName {
	BRIEF {
		@Override
		public String toString() {
			return "brief";
		}
	},
	SUMMARY {
		@Override
		public String toString() {
			return "summary";
		}
	},
	FULL {
		@Override
		public String toString() {
			return "full";
		}
	}
}
