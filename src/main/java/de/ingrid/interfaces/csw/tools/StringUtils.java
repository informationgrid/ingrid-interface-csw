/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class StringUtils {

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
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer();
	    // just set this to get literally equal results
	    transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
	    transformer.transform(source, result);
	    return stringWriter.getBuffer().toString();
	} catch (Exception e) {
	    return e.getMessage();
	}
    }

    public static Document stringToDocument(String string) throws Exception {
	DocumentBuilderFactory domFactory = DocumentBuilderFactory
		.newInstance();
	domFactory.setNamespaceAware(true);
	DocumentBuilder builder = domFactory.newDocumentBuilder();
	InputSource inStream = new InputSource();
	inStream.setCharacterStream(new StringReader(string));
	Document doc = builder.parse(inStream);
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
}
