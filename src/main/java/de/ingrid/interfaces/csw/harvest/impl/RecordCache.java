/**
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.harvest.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.interfaces.csw.cache.AbstractFileCache;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * A cache that stores InGrid records.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class RecordCache extends AbstractFileCache<Record> implements Serializable {

	private static final long serialVersionUID = RecordCache.class.getName().hashCode();

	private static final String ID_XPATH = "html/body/idfMdMetadata/fileIdentifier/CharacterString";

	@Override
	public Serializable getCacheId(Record document) throws Exception {
		Serializable id = null;

		// extract id from xml content
		// TODO might be optimized by caching the id
		Reader reader = null;
		try {
			// parse the file into a document
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String content = IdfTool.getIdfDataFromRecord(document);
			if (content != null) {
				reader = new StringReader(content);
				InputSource source = new InputSource(reader);
				Document xmlDoc = db.parse(source);
				XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
				id = xpath.getString(xmlDoc, ID_XPATH);
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
		return id;
	}

	@Override
	public String serializeDocument(Record document) {
		return IdfTool.getIdfDataFromRecord(document);
	}

	@Override
	public Record unserializeDocument(String str) {
		return IdfTool.createIdfRecord(str, true);
	}

	@Override
	public AbstractFileCache<Record> newInstance() {
		return new RecordCache();
	}
}
