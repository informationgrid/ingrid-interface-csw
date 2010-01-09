/*
 * Copyright (c) 2008 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.constants;

import javax.xml.namespace.QName;

public enum TypeName {
	MD_METADATA { public QName getQName() { return Namespace.ISO_METADATA.getQName(); } },
	RECORD 		{ public QName getQName() { return Namespace.CSW_RECORD.getQName(); } },
	DATASET		{ public QName getQName() { return Namespace.CSW_DATASET.getQName(); } };
	
	/**
	 * Get the QName of a typename constant
	 * @return
	 */
	public abstract QName getQName(); 
}
