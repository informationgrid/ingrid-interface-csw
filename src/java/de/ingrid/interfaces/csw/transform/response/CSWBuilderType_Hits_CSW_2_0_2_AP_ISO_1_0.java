/*
 * Copyright (c) 1997-2009 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.GregorianCalendar;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import de.ingrid.utils.IngridHits;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_Hits_CSW_2_0_2_AP_ISO_1_0 extends CSWBuilderType {

    public Element build() throws Exception {

        Element rootElement = DocumentFactory.getInstance().createElement("csw:GetRecordsResponse", "http://www.opengis.net/cat/csw/2.0.2");
        this.getResponseHeader_GetRecords(rootElement, hits);

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
        parent.addElement("csw:RequestId");
        Element e = parent.addElement("csw:SearchStatus");
        GregorianCalendar calendar = new GregorianCalendar();
        e.addAttribute("timestamp", DATE_TIME_FORMAT.format(calendar.getTime()));
        return parent.addElement("csw:SearchResults").addAttribute("resultSetId", "")
                .addAttribute("elementSet", "").addAttribute("numberOfRecordsMatched", Long.toString(hits.length()))
                .addAttribute("numberOfRecordsReturned", Integer.toString(hits.getHits().length));
    }
    
}