/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
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
	},
	/**
	 * CSW-Publication interface
	 */
	TRANSACTION {
		@Override
		public String toString() {
			return "Transaction";
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
