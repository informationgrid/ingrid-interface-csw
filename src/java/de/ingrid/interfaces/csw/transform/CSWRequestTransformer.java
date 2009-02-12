/*
 * Created on 07.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;


import java.util.ArrayList;

import javax.xml.soap.SOAPElement;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.utils.query.IngridQuery;

/**
 * interface for request transformers
 * @author rschaefer
 *

 */
public interface CSWRequestTransformer {
    
    /**
     * This Method transforms an OGC XML Filter into 
     * an IngridQuery (GetRecords)
     * @param soapElementFilter SOAPElement
     * @return ingridQuery IngridQuery
     * @throws Exception e
     */
    IngridQuery transform(final SOAPElement soapElementFilter, SessionParameters session) throws Exception;
    
    
    /**
     * This Method transforms a list of ids into 
     * an IngridQuery (GetRecordById)
     * @param idsList ArrayList
     * @return ingridQuery IngridQuery
     * @throws Exception e
     */
    IngridQuery transform(final ArrayList idsList) throws Exception;
    

}
