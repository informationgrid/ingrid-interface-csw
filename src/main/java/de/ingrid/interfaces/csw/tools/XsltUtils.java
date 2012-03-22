/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class XsltUtils {

	private Transformer transformer;
	private File styleSheet;

	/**
	 * Constructor
	 * @param styleSheet
	 */
	public XsltUtils(File styleSheet) {
		this.styleSheet = styleSheet;
	}

	/**
	 * Transform the given document using the stylesheet
	 * @param document
	 * @return Node
	 * @throws Exception
	 */
	public Node transform(Document document) throws Exception {

		if (this.transformer == null) {
			// create transformer
			Source source = new StreamSource(new ClassPathResource(this.styleSheet.getName()).getInputStream());
			TransformerFactory factory = TransformerFactory.newInstance();
			this.transformer = factory.newTransformer(source);
		}

		// perform transformation
		DOMSource source = new DOMSource(document);
		DOMResult result = new DOMResult();
		this.transformer.transform(source, result);

		return result.getNode();
	}
}
