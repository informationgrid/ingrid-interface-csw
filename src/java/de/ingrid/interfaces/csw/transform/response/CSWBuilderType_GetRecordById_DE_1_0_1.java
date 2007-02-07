/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_GetRecordById_DE_1_0_1 extends CSWBuilderType {

    private static Log log = LogFactory.getLog(CSWBuilderType_GetRecordById_DE_1_0_1.class);
    
    public Element build() throws Exception {

        Element rootElement = DocumentFactory.getInstance().createElement("GetRecordByIdResponse");

        String elementSetName = session.getElementSetName();
        
        String[] requestedFields = null;
        
        if (elementSetName.equalsIgnoreCase("brief")) {
            requestedFields = IngridQueryHelper.REQUESTED_STRING_BRIEF;
        } else if (elementSetName.equalsIgnoreCase("summary")) {
            requestedFields = IngridQueryHelper.REQUESTED_STRING_SUMMARY;
        } else if (elementSetName.equalsIgnoreCase("full")) {
            requestedFields = IngridQueryHelper.REQUESTED_STRING_FULL;
        } else {
            log.error("Unsupported CSW element set name (" + elementSetName + ") only brief, summary, full are supported");
            throw new IllegalArgumentException("Unsupported CSW element set name (" + elementSetName + ") only brief, summary, full are supported");
        }
        IngridHitDetail[] details = CSWInterfaceConfig.getInstance().getIBus().getDetails(hits.getHits(), query,
                requestedFields);

        IngridHit hit = hits.getHits()[0];
        hit.put("detail", details[0]);
        
        CSWBuilderMetaData builder = CSWBuilderFactory.getBuilderMetadata(session.getElementSetName(), "CSW_2_0_DE_1_0_1");
        builder.setHit(hit);
        rootElement.add(builder.build());

        return rootElement;
    }
}
