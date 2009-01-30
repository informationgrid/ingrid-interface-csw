/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.DOMWriter;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWResponseTransformer {

    private static Log log = LogFactory.getLog(CSWResponseTransformer.class);
    
    public Document transform(IngridHits hits, IngridQuery q, final SessionParameters session) throws Exception {

        long startTime = System.currentTimeMillis();
        
        CSWBuilderType builder = null;
        builder = CSWBuilderFactory.getBuilderType(session);
        builder.setHits(hits);
        builder.setQuery(q);
        builder.setSessionParameter(session);

        org.dom4j.Document doc = DocumentFactory.getInstance().createDocument();
        doc.add(builder.build());
        
        // apply postprocessing
        String postProcessorClassName = CSWInterfaceConfig.getInstance().getString(CSWInterfaceConfig.FILE_POST_PROCESSOR);
        CSWPostProcessor postProcessor = null;
        if (postProcessorClassName != null) {
            if (log.isDebugEnabled()) {
            	log.debug("Found post processor " + postProcessorClassName);
            }
        	Class [] classParm = null;
        	Object [] objectParm = null;
        	         
        	try {
				Class cl = Class.forName(postProcessorClassName);
				java.lang.reflect.Constructor co = cl.getConstructor(classParm);
				postProcessor = (CSWPostProcessor)co.newInstance(objectParm);
	            if (postProcessor != null) {
	            	doc = postProcessor.process(doc);
	            }
        	} catch (Exception e) {
                if (log.isDebugEnabled()) {
                	log.error("Error creating post processor", e);
                }
        	} 
        }
        return transformtoDOM(doc);
    }

    /**
     * Transform the dom4j document of this class into a w3c.dom.Document.
     * 
     * @return
     * @throws DocumentException
     */
    private org.w3c.dom.Document transformtoDOM(org.dom4j.Document doc) throws DocumentException {
      DOMWriter writer = new DOMWriter();
      return writer.write(doc);
    }
    
}
