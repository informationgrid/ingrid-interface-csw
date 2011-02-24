/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.GregorianCalendar;

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
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.tool.GZipTool;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_GetRecords_CSW_2_0_2_AP_ISO_1_0 extends CSWBuilderType {

    private static Log log = LogFactory.getLog(CSWBuilderType_GetRecords_CSW_2_0_2_AP_ISO_1_0.class);
    
    public Element build() throws Exception {

        Element rootElement = DocumentFactory.getInstance().createElement("csw:GetRecordsResponse", "http://www.opengis.net/cat/csw/2.0.2");
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
            hit.put("hitDetail", details[i]);
            String cswData = null;
            boolean compressed = false;
            if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDF_1_0_DSC_OBJECT)) {
                String compressedCswData = IngridQueryHelper.getDetailValueAsString(hit, "compressedCswData");
                if (compressedCswData != null && compressedCswData.equals("true")) {
                    compressed = true;
                }
            }
            if (IngridQueryHelper.hasValue(IngridQueryHelper.getDetailValueAsString(hit, "cswData"))) {
            	cswData = IngridQueryHelper.getDetailValueAsString(hit, "cswData");
            	if (compressed) {
            	    cswData = GZipTool.ungzip(cswData);
            	}
                Document document = DocumentHelper.parseText(cswData);
            	Element metadataNode = (Element)document.selectSingleNode("//gmd:MD_Metadata");
            	if (metadataNode != null) {
                	Node metadataIdNode = metadataNode.selectSingleNode("./@id");
                	if (metadataIdNode != null) {
                		metadataIdNode.setText("ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":original-response");
                	} else {
                		metadataNode.addAttribute("id", "ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":pass-through");
                	}
                	searchResults.add(metadataNode);
                	continue;
            	} else {
                    log.warn("Could not find valid metadata in direct data response:" + IngridQueryHelper.getDetailValueAsString(hit, "cswData"));
                    log.warn("Build CSW answer via data reconstruction from iplugs (" + hit.getPlugId() + ") index data for record with file identifier: " + IngridQueryHelper.getFileIdentifier(hit));
            	}
            }
            CSWBuilderMetaData builder = CSWBuilderFactory.getBuilderMetadata(session);
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
    private Element getResponseHeader_GetRecords(Element parent, IngridHits hits) throws Exception {
        parent.addElement("csw:RequestId");
        Element e = parent.addElement("csw:SearchStatus");
        GregorianCalendar calendar = new GregorianCalendar();
        e.addAttribute("timestamp", DATE_TIME_FORMAT.format(calendar.getTime()));
        return parent.addElement("csw:SearchResults").addAttribute("resultSetId", "")
                .addAttribute("elementSet", session.getElementSetName()).addAttribute("numberOfRecordsMatched", Long.toString(hits.length()))
                .addAttribute("numberOfRecordsReturned", Integer.toString(hits.getHits().length));
    }
    
}
