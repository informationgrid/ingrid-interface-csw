/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.Serializable;

import org.w3c.dom.Document;

import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class IdfUtils {

	private static final String ID_XPATH = "idf:html/idf:body/idf:idfMdMetadata/gmd:fileIdentifier/gco:CharacterString";

	/**
	 * Extract the idf document from the given record. Throws an exception
	 * if there is not idf content.
	 * @param record
	 * @return Document
	 * @throws Exception
	 */
	public static Document getIdfDocument(Record record) throws Exception {
		String content = IdfTool.getIdfDataFromRecord(record);
		if (content != null) {
			return StringUtils.stringToDocument(content);
		}
		else {
			throw new IOException("Document contains no IDF data.");
		}
	}

	/**
	 * Extract the id from the idf document.
	 * @param document
	 * @return Serializable
	 * @throws Exception
	 */
	public static Serializable getRecordId(Document document) throws Exception {
		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
		Serializable id = xpath.getString(document, ID_XPATH);
		return id;
	}

	/**
	 * Extract the id from the idf content of the given record. Throws an exception
	 * if there is not idf content.
	 * @param record
	 * @return Serializable
	 * @throws Exception
	 */
	public static Serializable getRecordId(Record record) throws Exception {
		Document doc = getIdfDocument(record);
		return getRecordId(doc);
	}
}
