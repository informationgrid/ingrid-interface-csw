/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;

public class XsltUtils {

	private Map<File, Transformer> transformers = new Hashtable<File, Transformer>();

	/**
	 * Transform the given document using the stylesheet
	 * @param full
	 * @param stylesheet
	 * @return Node
	 * @throws Exception
	 */
	public Node transform(Node full, File styleSheet) throws Exception {

		if (!this.transformers.containsKey(styleSheet)) {
			// create transformer for the stylesheet, if it does not exist yet
			Source source = new StreamSource(new ClassPathResource(styleSheet.getName()).getInputStream());
			TransformerFactory factory = TransformerFactory.newInstance();
			this.transformers.put(styleSheet, factory.newTransformer(source));
		}

		// perform transformation
		Transformer transformer = this.transformers.get(styleSheet);
		DOMSource source = new DOMSource(full);
		DOMResult result = new DOMResult();
		transformer.transform(source, result);

		return result.getNode();
	}
}
