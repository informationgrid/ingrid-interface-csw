/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.utils.dsc.Record;

/**
 * A CSWRecordMapper that maps records using XSLT.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class XsltMapper implements CSWRecordMapper {

	/**
	 * The cache used to store records.
	 */
	private CSWRecordCache cache;

	public void configure(CSWRecordCache cache) {
		this.cache = cache;
	}

	@Override
	public Node map(Record record, ElementSetName elementSetName) {
		// TODO Auto-generated method stub
		return null;
	}
}
