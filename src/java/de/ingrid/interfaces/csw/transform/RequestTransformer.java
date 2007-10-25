/*
 * Created on 07.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;

import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.soap.SOAPElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.interfaces.csw.exceptions.CSWException;
import de.ingrid.interfaces.csw.tools.SOAPTools;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.queryparser.ParseException;
import de.ingrid.utils.queryparser.QueryStringParser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class transforms an OGC XML Filter or 
 * a list of ids into an IngridQuery.
 * @author rschaefer
 */
public class RequestTransformer implements CSWRequestTransformer {
	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(RequestTransformer.class);

    /**
     * 
     * @see de.ingrid.interfaces.csw.transform.CSWRequestTransformer#transform(javax.xml.soap.SOAPElement)
     */
    public final IngridQuery transform(final SOAPElement soapElementFilter) throws Exception {
        IngridQuery ingridQuery = null;
        String ingridQueryString = null;
        FilterImpl filter = getFilterFromSOAPElem(soapElementFilter);
        FilterToIngridQueryString filterToIngrid = new FilterToIngridQueryString();
        ingridQueryString = filterToIngrid.generateQueryFromFilter(filter);
        QueryStringParser parser = new QueryStringParser(new StringReader(ingridQueryString));
        try {
        	ingridQuery = parser.parse();
        } catch (Throwable t) {
        	throw new Exception(t.getMessage());
        }
        return ingridQuery;
    }    
    
    /** 
     * 
     * @see de.ingrid.interfaces.csw.transform.CSWRequestTransformer#transform(java.util.List)
     */
    public final IngridQuery transform(final ArrayList idsList) throws Exception {
        IngridQuery ingridQuery = null;
        String queryString = "";
        
        //FIXME name of field?
        String idField = "T01_object.obj_id:";
        int listSize = idsList.size();
        
        for (int i = 0; i < listSize; i++) {
            queryString = queryString + " " + idField + (String) idsList.get(i);
        }
        
        log.debug("ingridQueryString: " + queryString);
        
        QueryStringParser parser = new QueryStringParser(new StringReader(queryString));
        ingridQuery = parser.parse();
        
        return ingridQuery;
    }
    
    
    /**
     * 
     * @param soapElementFilter SOAPElement
     * @return FilterImpl
     * @throws Exception e
     */
    public final FilterImpl getFilterFromSOAPElem(final SOAPElement soapElementFilter) throws Exception {
        Element  elemFilter = null;
        Document doc = XMLTools.create();
        doc.appendChild(doc.createElement("Filter"));
        elemFilter = doc.getDocumentElement();
        elemFilter = (Element) SOAPTools.copyNode(soapElementFilter, elemFilter);
        return new FilterImpl(elemFilter);
    }
}
