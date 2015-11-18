/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.tools;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class StringUtils {

	private static ThreadLocal<Transformer> threadLocalTransformer = new ThreadLocal<Transformer>() {
		@Override
		public Transformer initialValue() {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;
			try {
				transformer = factory.newTransformer();
				transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
				return transformer;
			} catch (TransformerConfigurationException e) {
				new RuntimeException("Error creating Transformer", e);
			}
			return null;
		}
	};

	private static ThreadLocal<DocumentBuilder> threadLocalDocumentBuilder = new ThreadLocal<DocumentBuilder>() {
		@Override
		public DocumentBuilder initialValue() {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory
					.newInstance();
			domFactory.setNamespaceAware(true);
			DocumentBuilder builder;
			try {
				builder = domFactory.newDocumentBuilder();
				return builder;
			} catch (ParserConfigurationException e) {
				new RuntimeException("Error creating DocumentBuilder", e);
			}
			return null;
		}
	};
	
	
	public static String join(Object[] parts, String separator) {
		StringBuilder str = new StringBuilder();
		for (Object part : parts) {
			str.append(part).append(separator);
		}
		if (str.length() > 0)
			return str.substring(0, str.length() - separator.length());

		return str.toString();
	}

	public static String nodeToString(Node node) {
        try {
            Source source = new DOMSource(node);
            StringWriter stringWriter = new StringWriter();
            Result result = new StreamResult(stringWriter);
            Transformer transformer = threadLocalTransformer.get();
            transformer.reset();
            transformer.transform(source, result);
            return stringWriter.getBuffer().toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

	public static Document stringToDocument(String string) throws SAXException, IOException {
		DocumentBuilder builder = threadLocalDocumentBuilder.get();
		builder.reset();
		Document doc = builder.parse(new InputSource(new StringReader(string)));
		return doc;
	}

	public static String generateUuid() {
		UUID uuid = UUID.randomUUID();
		StringBuffer idcUuid = new StringBuffer(uuid.toString().toUpperCase());
		while (idcUuid.length() < 36) {
			idcUuid.append("0");
		}
		return idcUuid.toString();
	}

	/**
	 * Splits a String by a separator. Only the first separator is taken into
	 * account. Thus max. two String segments are returned.
	 * 
	 * @param src
	 * @param separator
	 * @return Max 2 String parts.
	 */
	public static String[] splitByFirstOccurence(String src, String separator) {
		int pos = src.indexOf(separator);
		if (pos == -1) {
			return new String[] { src };
		} else {
			String firstSegment = src.substring(0, pos);
			String lastSegment = src.substring(pos + separator.length());
			return new String[] { firstSegment, lastSegment };
		}
	}
	
}
