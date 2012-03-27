/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain.constants;

import javax.xml.namespace.QName;

public enum TypeName {
	MD_METADATA {
		@Override
		public QName getQName() {
			return Namespace.ISO_METADATA.getQName();
		}
	},
	RECORD {
		@Override
		public QName getQName() {
			return Namespace.CSW_RECORD.getQName();
		}
	},
	DATASET {
		@Override
		public QName getQName() {
			return Namespace.CSW_DATASET.getQName();
		}
	};

	/**
	 * Get the QName of a typename constant
	 * 
	 * @return
	 */
	public abstract QName getQName();
}
