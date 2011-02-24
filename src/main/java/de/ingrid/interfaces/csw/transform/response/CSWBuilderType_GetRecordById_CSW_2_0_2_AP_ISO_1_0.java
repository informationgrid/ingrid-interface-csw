/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.utils.IPlugVersionInspector;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_GetRecordById_CSW_2_0_2_AP_ISO_1_0 extends CSWBuilderType {

    private static Log log = LogFactory.getLog(CSWBuilderType_GetRecordById_CSW_2_0_2_AP_ISO_1_0.class);
    
    public Element build() throws Exception {

        Element rootElement = DocumentFactory.getInstance().createElement("csw:GetRecordByIdResponse", "http://www.opengis.net/cat/csw/2.0.2");

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

        
        
        if (hits.length() > 0) {
        	IngridHit hit = hits.getHits()[0];
	        hit.put("hitDetail", details[0]);
            if (IngridQueryHelper.hasValue(IngridQueryHelper.getDetailValueAsString(hit, "cswData"))) {
            	Document document = DocumentHelper.parseText(IngridQueryHelper.getDetailValueAsString(hit, "cswData"));
            	Element metadataNode = (Element)document.selectSingleNode("//gmd:MD_Metadata");
            	Node metadataIdNode = metadataNode.selectSingleNode("./@id");
            	if (metadataIdNode != null) {
            		metadataIdNode.setText("ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":original-response");
            	} else {
            		metadataNode.addAttribute("id", "ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":original-response");
            	}
            	rootElement.add(metadataNode);
            } else {
                log.warn("Could not find valid metadata in direct data response:" + IngridQueryHelper.getDetailValueAsString(hit, "cswData"));
                log.warn("Build CSW answer via data reconstruction from iplugs (" + hit.getPlugId() + ") index data for record with file identifier: " + IngridQueryHelper.getFileIdentifier(hit));
                CSWBuilderMetaData builder = CSWBuilderFactory.getBuilderMetadata(session);
                builder.setHit(hit);
                rootElement.add(builder.build());
            }
        }

        return rootElement;
    }
}
