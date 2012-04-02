/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

import java.util.Hashtable;
import java.util.Map;

import de.ingrid.interfaces.csw.domain.exceptions.CSWOperationNotSupportedException;

public enum Operation {

	/**
	 * OGC_Service interface
	 */
	GET_CAPABILITIES {
		@Override
		public String toString() {
			return "GetCapabilities";
		}
	},
	/**
	 * CSW-Discovery interface
	 */
	DESCRIBE_RECORD {
		@Override
		public String toString() {
			return "DescribeRecord";
		}
	},
	GET_DOMAIN {
		@Override
		public String toString() {
			return "GetDomain";
		}
	},
	GET_RECORDS {
		@Override
		public String toString() {
			return "GetRecords";
		}
	},
	GET_RECORD_BY_ID {
		@Override
		public String toString() {
			return "GetRecordById";
		}
	};

	/**
	 * Operation name to Operation mapping
	 */
	private static Map<String, Operation> nameMapping = null;
	static {
		nameMapping = new Hashtable<String, Operation>();
		for (Operation op : Operation.values()) {
			nameMapping.put(op.toString(), op);
		}
	}

	public static Operation getByName(String name) throws CSWOperationNotSupportedException {
		Operation op = nameMapping.get(name);
		if (op == null) {
			throw new CSWOperationNotSupportedException("The operation '"+name+"' is unknown.", name);
		}
		return op;
	}
}
