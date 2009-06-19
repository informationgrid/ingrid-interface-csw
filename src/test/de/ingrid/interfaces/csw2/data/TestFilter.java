/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.data;

public final class TestFilter {

	public static final String SIMPLE_FILTER = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">" +
			"<ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
			"<ogc:PropertyName>fileIdentifier</ogc:PropertyName>" +
			"<ogc:Literal>1*</ogc:Literal>" + 
			"</ogc:PropertyIsLike>" + 
		"</ogc:Filter>";
	
	public static final String AND_FILTER = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<ogc:Filter xmlns:gml=\"http://www.opengis.net/gml\" xmlns:ogc=\"http://www.opengis.net/ogc\">" + 
			"<And>" +  
				"<ogc:PropertyIsEqualTo escape=\"\\\" singleChar=\"?\" wildCard=\"*\">" + 
					"<ogc:PropertyName>Title</ogc:PropertyName>" +  
					"<ogc:Literal>Wasser</ogc:Literal>" + 
				"</ogc:PropertyIsEqualTo>" + 
				"<ogc:PropertyIsEqualTo escape=\"\\\" singleChar=\"?\" wildCard=\"*\">" + 
					"<ogc:PropertyName>AnyText</ogc:PropertyName>" + 
					"<ogc:Literal>Luft</ogc:Literal>" + 
				"</ogc:PropertyIsEqualTo>" +
			"</And>" +
		"</ogc:Filter>";
	
	public static final String NESTED_FILTER = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">" + 
		"<Or xmlns:gml=\"http://www.opengis.net/gml\">" + 
			"<And>" +
				"<PropertyIsGreaterThan>" +
					"<PropertyName>CreationDate</PropertyName>" + 
					"<Literal>2000-01-01</Literal>" +
				"</PropertyIsGreaterThan>" +
				"<Intersects>" +
					"<PropertyName>Envelope</PropertyName>" +
					"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">" + 
						"<gml:coordinates>8.00,49.32 11.66,52.58</gml:coordinates>" +
					"</gml:Box>" +
				"</Intersects>" +
			"</And>" +
			"<And>" +
				"<PropertyIsEqualTo>" +
					"<PropertyName>Title</PropertyName>" +
					"<Literal>Test</Literal>" +
				"</PropertyIsEqualTo>" +
				"<Intersects>" +
					"<PropertyName>Envelope</PropertyName>" +
					"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">" +
						"<gml:coordinates>8.00,49.32 11.66,52.58</gml:coordinates>" +
					"</gml:Box>" +
				"</Intersects>" +
			"</And>" +
		"</Or>" +
		"</Filter>";	
}
