/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

public enum ConstraintLanguage {
	CQL_TEXT {
		@Override
		public String toString() {
			return "CQL_TEXT";
		}
	},
	FILTER {
		@Override
		public String toString() {
			return "FILTER";
		}
	}
}
