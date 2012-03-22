/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.mapping.impl;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;
import de.ingrid.interfaces.csw.mapping.CSWRecordMapper;
import de.ingrid.interfaces.csw.tools.IdfUtils;
import de.ingrid.interfaces.csw.tools.XsltUtils;
import de.ingrid.utils.dsc.Record;

/**
 * A CSWRecordMapper that maps records using XSLT.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class XsltMapper implements CSWRecordMapper {

	final protected static Log log = LogFactory.getLog(XsltMapper.class);

	/**
	 * The xslt style sheet
	 */
	private static final File stylesheet = new File("idf_1_0_0_to_iso_metadata.xsl");

	@Override
	public Node map(Record record, ElementSetName elementSetName) throws Exception {

		Node cswNode = null;

		// use the stylesheet for FULL
		if (elementSetName == ElementSetName.FULL) {
			Document idfDoc = IdfUtils.getIdfDocument(record);
			XsltUtils xslt = new XsltUtils(stylesheet);
			cswNode = xslt.transform(idfDoc);
		}
		return cswNode;
	}
}
