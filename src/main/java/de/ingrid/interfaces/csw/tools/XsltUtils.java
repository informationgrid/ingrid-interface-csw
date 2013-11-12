/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.tools;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Node;

public class XsltUtils {

    final protected static Log log = LogFactory.getLog(XsltUtils.class);

	private Map<File, Transformer> transformers = new Hashtable<File, Transformer>();

	/**
	 * Transform the given document using the stylesheet
	 * @param document
	 * @param stylesheet
	 * @return Node
	 * @throws Exception
	 */
	public Node transform(Node document, File styleSheet) throws Exception {

		if (!this.transformers.containsKey(styleSheet)) {
			// create transformer for the stylesheet, if it does not exist yet
			
			if (log.isDebugEnabled()) {
				String fisString = new Scanner(new ClassPathResource(styleSheet.getName()).getInputStream(),
					"UTF-8").useDelimiter("\\A").next();
				log.debug("\n\nAdding styleSheet:\n\n" + fisString);
			}

			Source source = new StreamSource(new ClassPathResource(styleSheet.getName()).getInputStream());
			TransformerFactory factory = TransformerFactory.newInstance();
			this.transformers.put(styleSheet, factory.newTransformer(source));
		}

		// perform transformation
		Transformer transformer = this.transformers.get(styleSheet);
		DOMSource source = new DOMSource(document);
		DOMResult result = new DOMResult();
		transformer.transform(source, result);

		return result.getNode();
	}
}
