/*
 * Created on 07.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.transform;

import java.util.ArrayList;

import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import de.ingrid.interfaces.csw.TestRequests;
import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.transform.IngridQueryToString;
import de.ingrid.interfaces.csw.transform.RequestTransformer;
import de.ingrid.utils.query.IngridQuery;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RequestTransformerTest extends TestCase {

    public final void testTransformFilter() throws Exception {
      
        RequestTransformer requestTransformer = new RequestTransformer();
        IngridQuery ingridQuery = null;
        
        SOAPElement soapElementFilter = null;
        
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETREC1_brief);
        
        String queryString = null;
        
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        
        assertNotNull(ingridQuery);
        
       
          
        queryString = ingridQueryToString.transform(ingridQuery);
        
        
        assertEquals("( AND title:Wasser )", queryString);
        
        
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETREC3);
        
       
        
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);
        assertEquals("(AND ( AND fische  AND halle NOT ( OR saale  OR hufeisensee )))", queryString);
        
        
        //System.out.println("get records as ingrid query string: " + queryString);
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETRECORDS_WILDCARD);
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);
        assertEquals("( AND zip:3* )", queryString);

        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GETRECORDS_MODIFIED);
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);
        assertEquals("( AND t01_object.mod_time:[19901231 TO 99990101] )", queryString);
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GET_RECORDS_BW4);
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);

        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GET_RECORDS_HH1);
        try {
        	ingridQuery = requestTransformer.transform(soapElementFilter, null);
        	// must throw an exception
        	assertTrue(false);
        } catch (Exception e) {
        }
        
        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GET_RECORDS_MV1);
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);
        assertEquals("( AND title:Wasser )", queryString);

        soapElementFilter = getSOAPElementFilterFromString(TestRequests.GET_RECORDS_MV2);
        ingridQuery = requestTransformer.transform(soapElementFilter, null);
        assertNotNull(ingridQuery);
        queryString = ingridQueryToString.transform(ingridQuery);
        assertEquals("( AND title:Wasser )", queryString);
        
        
        
        
    }
    
    
    public final void testTransformList() throws Exception {
      
       
        RequestTransformer requestTransformer = new RequestTransformer();
        
        IngridQuery ingridQuery = null;
        
        ArrayList idsList = new ArrayList();
        
        String queryString = null;
        
        idsList.add("AID");
        
        idsList.add("BID");
        
        idsList.add("CID");
   
        ingridQuery = requestTransformer.transform(idsList);
        
        assertNotNull(ingridQuery);
        
       
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
        queryString = ingridQueryToString.transform(ingridQuery);
        
        //System.out.println("get record by id as lucene query: " + queryString);
        
        assertEquals("( AND t01_object.obj_id:AID  AND t01_object.obj_id:BID  AND t01_object.obj_id:CID )", queryString);
        
        
    }
    
    
    
    private final SOAPElement getSOAPElementFilterFromString(String string) throws Exception {
        
        SOAPElement soapElementFilter = null;
        
        SOAPMessage soapMessageRequest = null;
        
        soapMessageRequest = AxisTools.createSOAPMessage(string);
        
        soapElementFilter = (SOAPElement) soapMessageRequest.getSOAPBody().getElementsByTagName("Filter").item(0);
        
        return soapElementFilter;
    }

}
