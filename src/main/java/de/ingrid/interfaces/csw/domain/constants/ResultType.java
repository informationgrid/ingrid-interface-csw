/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

public enum ResultType {
	HITS {
		@Override
		public String toString() {
			return "hits";
		}
	},
	RESULTS {
		@Override
		public String toString() {
			return "results";
		}
	},
	VALIDATE {
		@Override
		public String toString() {
			return "validate";
		}
	}
}
