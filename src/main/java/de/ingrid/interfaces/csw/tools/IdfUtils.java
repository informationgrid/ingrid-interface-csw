/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class IdfUtils {

	private static final String ID_XPATH = "idf:html/idf:body/idf:idfMdMetadata/gmd:fileIdentifier/gco:CharacterString";

    final protected static Log log = LogFactory.getLog(IdfUtils.class);
    
    static final XPathUtils XPATH = new XPathUtils(new IDFNamespaceContext());
	
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
			try {
			    return StringUtils.stringToDocument(content);
			} catch (Throwable t) {
			    log.error("Error transforming record to DOM: " + content, t);
			    throw new Exception(t);
			}
		}
		else {
			log.error("Could not retrieve the IDF content from record: " + record);
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
		Serializable id = XPATH.getString(document, ID_XPATH);
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
