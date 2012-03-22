/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class IdfUtils {

	private static final String ID_XPATH = "html/body/idfMdMetadata/fileIdentifier/CharacterString";

	/**
	 * Extract the idf document from the given record. Throws an exception
	 * if there is not idf content.
	 * @param record
	 * @return Document
	 * @throws Exception
	 */
	public static Document getIdfDocument(Record record) throws Exception {
		Document doc = null;
		Reader reader = null;
		try {
			// parse the file into a document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String content = IdfTool.getIdfDataFromRecord(record);
			if (content != null) {
				reader = new StringReader(content);
				InputSource source = new InputSource(reader);
				doc = db.parse(source);
			}
			else {
				throw new IOException("Document contains no IDF data.");
			}
		}
		finally {
			if (reader != null) {
				reader.close();
			}
		}
		return doc;
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
		XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
		Serializable id = xpath.getString(doc, ID_XPATH);
		return id;
	}
}
