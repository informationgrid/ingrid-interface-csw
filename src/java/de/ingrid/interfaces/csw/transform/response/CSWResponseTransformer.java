/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.io.DOMWriter;
import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWResponseTransformer {

    private static Log log = LogFactory.getLog(CSWBuilderMetadata_brief_DE_1_0_1.class);
    
    public Document transform(IngridHits hits, IngridQuery q, final SessionParameters session) throws Exception {

        long startTime = System.currentTimeMillis();
        
        CSWBuilderType builder = null;
        if (session.isOperationIsGetRecs()) {
            builder = CSWBuilderFactory.getBuilderType(session.getResultType(), "GetRecords", "CSW_2_0_DE_1_0_1");
            builder.setHits(hits);
            builder.setQuery(q);
            builder.setSessionParameter(session);
        } else if (session.isOperationIsGetRecById()) {
            builder = CSWBuilderFactory.getBuilderType(session.getResultType(), "GetRecordById", "CSW_2_0_DE_1_0_1");
            builder.setHits(hits);
            builder.setQuery(q);
            builder.setSessionParameter(session);
        } else if (session.isOperationIsGetCap()) {
        } else if (session.isOperationIsDescRec()) {
        }

        org.dom4j.Document doc = DocumentFactory.getInstance().createDocument();;
        doc.add(builder.build());
        
        log.info("build CSW response in: " + (System.currentTimeMillis() - startTime) + "ms.");
        
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
