/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.data;

public final class TestFilter {

	public static final String SIMPLE_FILTER = 
		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		"<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">" +
			"<ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
			"<ogc:PropertyName>identifier</ogc:PropertyName>" +
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
	
	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<PropertyIsEqualTo>\r\n" + 
			"<PropertyName>Title</PropertyName>\r\n" + 
			"<Literal>100</Literal>\r\n" + 
			"</PropertyIsEqualTo>\r\n" + 
			"</Filter>";
	
	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<PropertyIsLessThan>\r\n" + 
			"<PropertyName>DistanceValue</PropertyName>\r\n" + 
			"<Literal>30</Literal>\r\n" + 
			"</PropertyIsLessThan>\r\n" + 
			"</Filter>";
	
	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<Not>\r\n" + 
			"<Disjoint>\r\n" + 
			"<PropertyName>Geometry</PropertyName>\r\n" + 
			"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\r\n" + 
			"<gml:coordinates>13.0983,31.5899 35.5472,42.8143</gml:coordinates>\r\n" + 
			"</gml:Box>\r\n" + 
			"</Disjoint>\r\n" + 
			"</Not>\r\n" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_3_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<BBOX>\r\n" + 
			"<PropertyName>Geometry</PropertyName>\r\n" + 
			"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\r\n" + 
			"<gml:coordinates>13.0983,31.5899 35.5472,42.8143</gml:coordinates>\r\n" + 
			"</gml:Box>\r\n" + 
			"</BBOX>" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_3_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<Within>\r\n" + 
			"<PropertyName>Geometry</PropertyName>\r\n" + 
			"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\r\n" + 
			"<gml:coordinates>13.0983,31.5899 35.5472,42.8143</gml:coordinates>\r\n" + 
			"</gml:Box>\r\n" + 
			"</Within>" + 
			"</Filter>";
	
	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_3_3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<Contains>\r\n" + 
			"<PropertyName>Geometry</PropertyName>\r\n" + 
			"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\r\n" + 
			"<gml:coordinates>13.0983,31.5899 35.5472,42.8143</gml:coordinates>\r\n" + 
			"</gml:Box>\r\n" + 
			"</Contains>" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<And>\r\n" + 
			"<PropertyIsLessThan>\r\n" + 
			"<PropertyName>DistanceValue</PropertyName>\r\n" + 
			"<Literal>30</Literal>\r\n" + 
			"</PropertyIsLessThan>\r\n" + 
			"<Not>\r\n" + 
			"<Disjoint>\r\n" + 
			"<PropertyName>Geometry</PropertyName>\r\n" + 
			"<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\r\n" + 
			"<gml:coordinates>13.0983,31.5899 35.5472,42.8143</gml:coordinates>\r\n" + 
			"</gml:Box>\r\n" + 
			"</Disjoint>\r\n" + 
			"</Not>\r\n" + 
			"</And>" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<FeatureId fid=\"TREESA_1M.1234\"/>\r\n" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_6 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<FeatureId fid=\"TREESA_1M.1234\"/>\r\n" + 
			"<FeatureId fid=\"TREESA_1M.5678\"/>\r\n" + 
			"<FeatureId fid=\"TREESA_1M.9012\"/>\r\n" + 
			"<FeatureId fid=\"INWATERA_1M.3456\"/>\r\n" + 
			"<FeatureId fid=\"INWATERA_1M.7890\"/>\r\n" + 
			"<FeatureId fid=\"BUILTUPA_1M.4321\"/>\r\n" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_7 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<PropertyIsEqualTo>\r\n" + 
			"<Function name=\"SIN\">\r\n" + 
			"<PropertyName>DISPERSION_ANGLE</PropertyName>\r\n" + 
			"</Function>\r\n" + 
			"<Literal>1</Literal>\r\n" + 
			"</PropertyIsEqualTo>\r\n" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<PropertyIsEqualTo>\r\n" + 
			"<PropertyName>PROPA</PropertyName>\r\n" + 
			"<Add>\r\n" + 
			"<PropertyName>PROPB</PropertyName>\r\n" + 
			"<Literal>100</Literal>\r\n" + 
			"</Add>\r\n" + 
			"</PropertyIsEqualTo>\r\n" + 
			"</Filter>";

	// sample filter from OGC 02-059 Filter Specification
	public static final String OGC_FILTER_SAMPLE_9 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">\r\n" + 
			"<PropertyIsBetween>\r\n" + 
			"<PropertyName>DistanceValue</PropertyName>\r\n" + 
			"<LowerBoundary><Literal>100</Literal></LowerBoundary>\r\n" + 
			"<UpperBoundary><Literal>200</Literal></UpperBoundary>\r\n" + 
			"</PropertyIsBetween>\r\n" + 
			"</Filter>";
}
