/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.GregorianCalendar;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.core.io.ClassPathResource;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.tools.DocumentStyler;
import de.ingrid.interfaces.csw.utils.IPlugVersionInspector;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;

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
            // prepare hit for usage with helper function
            IngridHit hit = hits.getHits()[i];
            hit.put("hitDetail", details[i]);
            
            Element metadataNode = null;
            if (IPlugVersionInspector.getIPlugVersion(hit).equals(IPlugVersionInspector.VERSION_IDF_1_0_DSC_OBJECT)) {

                Record idfRecord = (Record)details[i].get("idfRecord"); 
                // if no idf record was found in detail data, fall back to get data from getRecord
                if (idfRecord == null) {
                    idfRecord = CSWInterfaceConfig.getInstance().getIBus().getRecord(hit);
                }
                String idfData = IdfTool.getIdfDataFromRecord(idfRecord);
                Document idfDoc = DocumentHelper.parseText(idfData);
                // extract MD_Metadata
                Source style = new StreamSource(new ClassPathResource("idf_1_0_0_to_iso_metadata.xsl").getInputStream());
                DocumentStyler docStyler = new DocumentStyler(style);
                Document metadataDoc = docStyler.transform(idfDoc);
                
                metadataNode = metadataDoc.getRootElement();
                if (metadataNode == null) {
                    log.warn("Could not find valid metadata in IDF data response:" + idfData);
                    log.warn("Build CSW answer via data reconstruction from iplugs (" + hit.getPlugId() + ") index data for record with file identifier: " + IngridQueryHelper.getFileIdentifier(hit));
                }
            } else if (IngridQueryHelper.hasValue(IngridQueryHelper.getDetailValueAsString(hit, "cswData"))) {
            	String cswData = IngridQueryHelper.getDetailValueAsString(hit, "cswData");
                Document document = DocumentHelper.parseText(cswData);
            	metadataNode = (Element)document.selectSingleNode("//gmd:MD_Metadata");
                if (metadataNode == null) {
                	log.warn("Could not find valid metadata in direct data response:" + cswData);
                    log.warn("Build CSW answer via data reconstruction from iplugs (" + hit.getPlugId() + ") index data for record with file identifier: " + IngridQueryHelper.getFileIdentifier(hit));
                }
            }
            if (metadataNode != null) {
                Node metadataIdNode = metadataNode.selectSingleNode("./@id");
                if (metadataIdNode != null) {
                    metadataIdNode.setText("ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":original-response");
                } else {
                    metadataNode.addAttribute("id", "ingrid:" + hit.getPlugId() + ":" + hit.getDocumentId() + ":pass-through");
                }
                searchResults.add(metadataNode);
                continue;
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
