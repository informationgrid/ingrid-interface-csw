/*
 * Created on 17.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.ArrayList;

import de.ingrid.interfaces.csw.analyse.CommonAnalyser;
import de.ingrid.interfaces.csw.analyse.SessionParameters;

import junit.framework.TestCase;

/**
 * @author rschaefer
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CommonAnalyserTest extends TestCase {

    
    public final void testAnalyseIds() {
        
        boolean idsIsValid = false; 
        
        ArrayList idsList = null;
        
        SessionParameters sessionParameters = new SessionParameters();
        
        CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        
        
        idsIsValid = commonAnalyser.analyseIds("1, DE_3, XYZ898");
        
         
        assertTrue(idsIsValid);
        
        idsList = sessionParameters.getIdsList();
        
        assertEquals(idsList.size(), 3);
        
        assertEquals("1", (String) idsList.get(0));
        assertEquals("DE_3", (String) idsList.get(1));
        assertEquals("XYZ898", (String) idsList.get(2));
        
        
    }
    
    
    
    public final void testAnalyseTypeNames() {
       
        
        boolean typeNamesIsValid = false; 
        
        SessionParameters sessionParameters = new SessionParameters();
        
        CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        
        
        typeNamesIsValid = commonAnalyser.analyseTypeNames("csw:dataset");
        
        assertTrue(typeNamesIsValid);
        
        assertTrue(sessionParameters.isTypeNameIsDataset());
        
        
        typeNamesIsValid = commonAnalyser.analyseTypeNames("csw:dataset,csw:datasetcollection");
        
        assertTrue(typeNamesIsValid);
       
        assertTrue(sessionParameters.isTypeNameIsDataset());
        
        assertTrue(sessionParameters.isTypeNameIsDatasetcollection());
        
        
        typeNamesIsValid = commonAnalyser.analyseTypeNames("csw:service");
        
        assertTrue(typeNamesIsValid);
       
        assertTrue(sessionParameters.isTypeNameIsService());
        
        
        
        
    }

}
