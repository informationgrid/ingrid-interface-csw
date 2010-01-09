/*
 * Created on 29.09.2005
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.Properties;

import javax.xml.soap.SOAPBodyElement;

import org.w3c.dom.Element;

import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;


/**
 * @author rschaefer
 *
 */
public class GetRecByIdAnalyser implements CSWAnalyser {

    /**
     * stores values of the request
     */
    private SessionParameters sessionParameters = null;
    
    
    /**
     * constructor 
     * @param sessionParams SessionParameters
     */ 
   public GetRecByIdAnalyser(final SessionParameters sessionParams) {
       
       this.sessionParameters = sessionParams;
       
   }
    
    
    /**
     * 
    * @param be SOAPBodyElement
    * @return boolean 
    * @throws Exception e
    * @see com.gistec.ingeocsw.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
    */
    public final boolean analyse(final Element be) throws Exception {
       
        boolean getRecordByIdRequestValid = false;
         
        String opName = null;
       
        CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
        
        if (be == null) {
            
            throw new Exception("analyse: SOAPBodyElement is null.");
            
        }
        
         
         opName = be.getLocalName();
        
         if (opName == null) {
            
             throw new Exception("analyse: opName is null."); 
         }
        
         
         if (!opName.equals("GetRecordById")) {
             
            
             Exception e = 
               new CSWOperationNotSupportedException("Operation " + opName +
                                      " is not supported.", opName); 
             
             throw e; 
         }
         
         
         commonAnalyser.analyseService(be);
 			sessionParameters.setVersion(commonAnalyser.analyseVersion(be));
         
         
         commonAnalyser.analyseVersion(be);
        
         
         analyseId(be);
         
         
         commonAnalyser.analyseElementSetName(be);
         
       
        
        getRecordByIdRequestValid = true;
        
        return getRecordByIdRequestValid;
        
       
    }
    
    
    /**
     * analyse the id element,
     * return true if ok.
     * @param be SOAPBodyElement
     * @return boolean
     * @throws Exception e
     */
    private boolean analyseId(final Element be) throws Exception {
        
        
        
        Element elemId = (Element) be.getFirstChild();
        
        String ids = null;
        
        //if (elemId == null || elemId.getNodeName() != "Id") {
        if (elemId == null || elemId.getLocalName() != "Id" || elemId.getFirstChild() == null) {     
        	
        	
            Exception e = 
                new CSWMissingParameterValueException("Element 'Id' is missing.", "Id"); 
            
            throw e;     
        
        } else {
            
            ids = elemId.getFirstChild().getNodeValue();
            
            CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);
            
             // tokenize ids: e.g '2, 6, 8'
            
            if (!commonAnalyser.analyseIds(ids)) {
                
                
                Exception e = 
                    new CSWInvalidParameterValueException(" 'Id' is invalid.", "Id"); 
                
                throw e;     
                
            }
            
            
            
           
            
            
        }
     
        return true;
    }   
    
    
    
    /**
     * 
    * @param kvpStr String
    * @return boolean 
    * @throws Exception e
    * @see com.gistec.ingeocsw.analyse.CSWAnalyser#analyse(java.lang.String)
    */
  
public final boolean analyse(final String kvpStr) throws Exception {
       
       System.out.println("GetRecByIdAnalyser.analyse(kvpStr): Not implemented yet.");
       
       return false;
   }
    

	/**
	 * Checks if the given request parameters are correct for performing a
	 * GetCapabilities action.
	 * Currently, no check is made on the existence and correctness of the
	 * parameter 'REQUEST'. This has to be done before.
	 * 
	 * Currently checks:
	 * The values of the parameters are checked with case sensitivity.
	 * 
	 * @param reqParams The request parameters as a Properties object
	 * @return true if the check was ok
	 * @throws Exception if a check failed
	 * @see de.ingrid.interfaces.csw.analyse.CSWAnalyser#analyse(java.util.Properties)
	 */
	public final boolean analyse(final Properties reqParams)
	throws Exception {
		boolean result = false;
		System.out.println("***Analyser.analyse(Properties): Not implemented yet.");
		return result;
	}
}
