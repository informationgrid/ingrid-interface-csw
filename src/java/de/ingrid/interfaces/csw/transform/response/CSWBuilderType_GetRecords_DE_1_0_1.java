/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_GetRecords_DE_1_0_1 extends CSWBuilderType {

    private static Log log = LogFactory.getLog(CSWBuilderType_GetRecords_DE_1_0_1.class);
    
    public Element build() throws Exception {

        Element rootElement = DocumentFactory.getInstance().createElement("GetRecordsResponse");
        Element searchResults = this.getResponseHeader_GetRecords(rootElement, hits);

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

        for (int i = 0; i < hits.getHits().length; i++) {
            IngridHit hit = hits.getHits()[i];
            hit.put("detail", details[i]);
            
            CSWBuilderMetaData builder = CSWBuilderFactory.getBuilderMetadata(session.getElementSetName(), "CSW_2_0_DE_1_0_1");
            builder.setHit(hit);
            searchResults.add(builder.build());
        }

        return rootElement;
    }

    /**
     * Adds the beginning of a CSW response for GetRecords requests to a
     * root element.
     * 
     * @param doc
     *            The document to add the elements to.
     * @param hits
     *            The hits of an Ingrid Query.
     * @return The 'SearchResults' element of the CSW response.
     */
    private Element getResponseHeader_GetRecords(Element parent, IngridHits hits) {
        parent.addElement("RequestId");
        String searchStatus;
        if (hits.getHits().length < hits.length()) {
            searchStatus = "subset";
        } else {
            searchStatus = "complete";
        }
        Element e = parent.addElement("SearchStatus").addAttribute("status", searchStatus);
        GregorianCalendar calendar = new GregorianCalendar();
        e.addAttribute("timestamp", DATE_TIME_FORMAT.format(calendar.getTime()));
        return parent.addElement("SearchResults").addAttribute("resultSetId", "")
                .addAttribute("elementSet", "").addAttribute("numberOfRecordsMatched", Long.toString(hits.length()))
                .addAttribute("numberOfRecordsReturned", Integer.toString(hits.getHits().length));
    }
    
}
