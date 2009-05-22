/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.constants;

import java.util.Hashtable;
import java.util.Map;

public enum Operation {
	
	/**
	 * OGC_Service interface
	 */
	GET_CAPABILITIES {
		public String toString() {
			return "GetCapabilities";
		}
	},
	/**
	 * CSW-Discovery interface
	 */
	DESCRIBE_RECORD {
		public String toString() {
			return "DescribeRecord";
		}
	},
	GET_DOMAIN {
		public String toString() {
			return "GetDomain";
		}
	},
	GET_RECORDS {
		public String toString() {
			return "GetRecords";
		}
	},
	GET_RECORD_BY_ID {
		public String toString() {
			return "GetRecordById";
		}
	};
	
	/**
	 * Operation name to Operation mapping
	 */
	private static Map<String, Operation> nameMapping = null;
	
	public static Operation getByName(String name) {
		if (nameMapping == null) {
			nameMapping = new Hashtable<String, Operation>();
			for (Operation op : Operation.values()) {
				nameMapping.put(op.toString(), op);
			}
		}
		return nameMapping.get(name);
	}
}
