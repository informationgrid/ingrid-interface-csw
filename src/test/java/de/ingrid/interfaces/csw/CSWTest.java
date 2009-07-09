package de.ingrid.interfaces.csw;

import de.ingrid.interfaces.csw.CSW;
import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.transform.IngridQueryToString;
import de.ingrid.utils.query.IngridQuery;
import junit.framework.TestCase;

public class CSWTest extends TestCase {

//	protected void setUp() throws Exception {
//		super.setUp();
//	}
//
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}

	/*
	 * Test method for 'de.ingrid.interfaces.csw.CSW.setSourceType(IngridQuery, SessionParameters)'
	 */
	public void testSetSourceType() throws Exception {
		
		CSW csw = new CSW();
		
		
		//MAP
		IngridQuery ingridQuery = new IngridQuery();
		
		SessionParameters sessionParameters = new SessionParameters();
		
		sessionParameters.setTypeNameIsDataset(true);
		
		
		ingridQuery = csw.setSourceType(ingridQuery, sessionParameters);
		
	
        IngridQueryToString ingridQueryToString = new IngridQueryToString();
        
          
        //System.out.println("CSWTest ingridQuery ingridQueryToString: " + ingridQueryToString.transform(ingridQuery));
		
		//(assertEquals(ingridQueryToString.transform(ingridQuery), "( AND source:map )");
		
        // geaendert Dirk Schwarzmann 13.09.06
		//assertEquals(ingridQueryToString.transform(ingridQuery), "( AND t01_object.obj_class:1 )");
		
		assertEquals(ingridQueryToString.transform(ingridQuery), "( AND metaclass:map )");
		
		
		
		//SERVICE
		ingridQuery = new IngridQuery();
		
		sessionParameters = new SessionParameters();
		
		sessionParameters.setTypeNameIsService(true);
		
		
		ingridQuery = csw.setSourceType(ingridQuery, sessionParameters);
		
		
      //System.out.println("CSWTest ingridQuery ingridQueryToString: " + ingridQueryToString.transform(ingridQuery));
		
		//assertEquals(ingridQueryToString.transform(ingridQuery), "( AND source:service )");
		
		// geaendert Dirk Schwarzmann 13.09.06
		//assertEquals(ingridQueryToString.transform(ingridQuery), "( AND t01_object.obj_class:3 )");
		
		assertEquals(ingridQueryToString.transform(ingridQuery), "( AND metaclass:service )");
		
		
        //MAP and SERVICE
		ingridQuery = new IngridQuery();
		
		sessionParameters = new SessionParameters();
		
		sessionParameters.setTypeNameIsDataset(true);
		
		sessionParameters.setTypeNameIsService(true);
		
		ingridQuery = csw.setSourceType(ingridQuery, sessionParameters);

          
        //System.out.println("CSWTest ingridQuery ingridQueryToString: " + ingridQueryToString.transform(ingridQuery));
	
		// geaendert Dirk Schwarzmann 13.09.06
		//assertEquals(ingridQueryToString.transform(ingridQuery), "(AND ( OR t01_object.obj_class:1  OR t01_object.obj_class:3 ))");
		
		assertEquals(ingridQueryToString.transform(ingridQuery), "(AND ( OR metaclass:map  OR metaclass:service ))");
		
		
		
		
		//DEFAULT:Nothing --> every source type!
		ingridQuery = new IngridQuery();
		
		sessionParameters = new SessionParameters();
		
		ingridQuery = csw.setSourceType(ingridQuery, sessionParameters);
		
      //System.out.println("CSWTest ingridQuery ingridQueryToString: " + ingridQueryToString.transform(ingridQuery));
		
		//assertEquals(ingridQueryToString.transform(ingridQuery), "( AND source:map )");
	
		assertEquals(ingridQueryToString.transform(ingridQuery), "( AND metaclass:map )");
		
		
	}
	
//	/*
//	 * Test method for 'de.ingrid.interfaces.csw.CSW.setDataType(IngridQuery)'
//	 */
//	public void testSetDataType() throws Exception {
//		
//		CSW csw = new CSW();
//		
//		IngridQuery ingridQuery = new IngridQuery();
//		
//		IngridQueryToString ingridQueryToString = new IngridQueryToString();
//		
//		ingridQuery = csw.setDataType(ingridQuery);
//	
//		System.out.println("ingridQueryToString.transform(ingridQuery): " + ingridQueryToString.transform(ingridQuery));
//		//does not work..
//		//assertEquals(ingridQueryToString.transform(ingridQuery), "( AND datatype:dsc_csw )");
//		
//       
////   String[] positiveDataTypes = ingridQuery.getPositiveDataTypes();
////   
////   System.out.println("positiveDataTypes: " + positiveDataTypes[0]);
////		
////		assertEquals(positiveDataTypes.length, 4);
////		
////		assertEquals(positiveDataTypes[0], "default");
////		assertEquals(positiveDataTypes[1], "topics");
////		assertEquals(positiveDataTypes[2], "metadata");
////		assertEquals(positiveDataTypes[3], "dsc_ecs");
//		
//	
//	}	
	
	
//	/*
//	 * Test method for 'de.ingrid.interfaces.csw.CSW.setRanking(IngridQuery)'
//	 */
//	public void testSetRankingType() throws Exception {
//		
//		
//        CSW csw = new CSW();
//		
//		IngridQuery ingridQuery = new IngridQuery();
//		
//		IngridQueryToString ingridQueryToString = new IngridQueryToString();
//		
//		ingridQuery = csw.setRankingType(ingridQuery);
//		
//		//System.out.println("ranking type: " + ingridQuery.getRankingType());
//		
//		assertEquals("score", ingridQuery.getRankingType());
//		
//	
//		
//	}

}
