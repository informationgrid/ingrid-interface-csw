/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import junit.framework.TestCase;

import org.geotoolkit.lucene.filter.LuceneOGCFilter;
import org.geotoolkit.lucene.filter.SerialChainFilter;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.filter.spatial.BBOX;
import org.opengis.filter.spatial.Contains;
import org.opengis.filter.spatial.DWithin;
import org.opengis.filter.spatial.Intersects;

import de.ingrid.interfaces.csw.domain.filter.impl.LuceneFilterParser;
import de.ingrid.interfaces.csw.tools.StringUtils;

public class FilterParserTest extends TestCase {

	private LuceneFilterParser filterParser;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	@Override
	@Before
	public void setUp() throws Exception {
		this.filterParser = new LuceneFilterParser();
	}

	@Override
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test simple comparison filter.
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testSimpleComparisonFilter() throws Exception {

		/**
		 * Test 1: a simple Filter propertyIsLike
		 */
		String XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"           +
				"    <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"        <ogc:PropertyName>apiso:Title</ogc:PropertyName>"                   +
				"        <ogc:Literal>*VM*</ogc:Literal>"                                    +
				"    </ogc:PropertyIsLike>"                                                  +
				"</ogc:Filter>";
		SpatialQuery spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "title:*VM*");


		/**
		 * Test 2: a simple Filter PropertyIsEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"+
				"    <ogc:PropertyIsEqualTo>"                              +
				"        <ogc:PropertyName>apiso:Title</ogc:PropertyName>" +
				"        <ogc:Literal>VM</ogc:Literal>"                    +
				"    </ogc:PropertyIsEqualTo>"                             +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "title:VM");

		/**
		 * Test 3: a simple Filter PropertyIsNotEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"+
				"    <ogc:PropertyIsNotEqualTo>"                           +
				"        <ogc:PropertyName>apiso:Title</ogc:PropertyName>" +
				"        <ogc:Literal>VM</ogc:Literal>"                    +
				"    </ogc:PropertyIsNotEqualTo>"                          +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "metafile:doc NOT title:VM");


		/**
		 * Test 4: a simple Filter PropertyIsNull
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"+
				"    <ogc:PropertyIsNull>"                                 +
				"        <ogc:PropertyName>apiso:Title</ogc:PropertyName>" +
				"    </ogc:PropertyIsNull>"                                +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "title:null");

		/**
		 * Test 5: a simple Filter PropertyIsGreaterThanOrEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"       +
				"    <ogc:PropertyIsGreaterThanOrEqualTo>"                        +
				"        <ogc:PropertyName>apiso:CreationDate</ogc:PropertyName>" +
				"        <ogc:Literal>2007-06-02</ogc:Literal>"                   +
				"    </ogc:PropertyIsGreaterThanOrEqualTo>"                       +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "creationdate:[2007-06-02 9999-01-01]");

		/**
		 * Test 6: a simple Filter PropertyIsGreaterThan
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"       +
				"    <ogc:PropertyIsGreaterThan>"                                 +
				"        <ogc:PropertyName>apiso:CreationDate</ogc:PropertyName>" +
				"        <ogc:Literal>2007-06-02</ogc:Literal>"                   +
				"    </ogc:PropertyIsGreaterThan>"                                +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "creationdate:{2007-06-02 9999-01-01}");

		/**
		 * Test 7: a simple Filter PropertyIsLessThan
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"       +
				"    <ogc:PropertyIsLessThan>"                                    +
				"        <ogc:PropertyName>apiso:CreationDate</ogc:PropertyName>" +
				"        <ogc:Literal>2007-06-02</ogc:Literal>"                   +
				"    </ogc:PropertyIsLessThan>"                                   +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "creationdate:{0000-01-01 2007-06-02}");


		/**
		 * Test 8: a simple Filter PropertyIsLessThanOrEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"       +
				"    <ogc:PropertyIsLessThanOrEqualTo>"                           +
				"        <ogc:PropertyName>apiso:CreationDate</ogc:PropertyName>" +
				"        <ogc:Literal>2007-06-02</ogc:Literal>"                   +
				"    </ogc:PropertyIsLessThanOrEqualTo>"                          +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "creationdate:[0000-01-01 2007-06-02]");
	}

	/**
	 * Test simple logical filter (unary and binary).
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testSimpleLogicalFilter() throws Exception {

		/**
		 * Test 1: a simple Filter AND between two propertyIsEqualTo
		 */
		String XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"  +
				"    <ogc:And>                                        "             +
				"        <ogc:PropertyIsEqualTo>"                                   +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>"      +
				"            <ogc:Literal>Der Hessische Landbote</ogc:Literal>"     +
				"        </ogc:PropertyIsEqualTo>"                                  +
				"        <ogc:PropertyIsEqualTo>"                                   +
				"            <ogc:PropertyName>apiso:OrganisationName</ogc:PropertyName>"     +
				"            <ogc:Literal>Georg Buechner</ogc:Literal>"             +
				"        </ogc:PropertyIsEqualTo>"                                  +
				"    </ogc:And>"                                                    +
				"</ogc:Filter>";
		SpatialQuery spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "(title:\"Der Hessische Landbote\" AND organisationname:\"Georg Buechner\")");

		/**
		 * Test 2: a simple Filter OR between two propertyIsEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"     +
				"    <ogc:Or>                                        "          +
				"        <ogc:PropertyIsEqualTo>"                               +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>"  +
				"            <ogc:Literal>Der Hessische Landbote</ogc:Literal>" +
				"        </ogc:PropertyIsEqualTo>"                              +
				"        <ogc:PropertyIsEqualTo>"                               +
				"            <ogc:PropertyName>apiso:OrganisationName</ogc:PropertyName>" +
				"            <ogc:Literal>Georg Buechner</ogc:Literal>"         +
				"        </ogc:PropertyIsEqualTo>"                              +
				"    </ogc:Or>"                                                 +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "(title:\"Der Hessische Landbote\" OR organisationname:\"Georg Buechner\")");


		/**
		 * Test 3: a simple Filter OR between three propertyIsEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"         +
				"    <ogc:Or>                                        "              +
				"        <ogc:PropertyIsEqualTo>"                                   +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>"      +
				"            <ogc:Literal>Der Hessische Landbote</ogc:Literal>"     +
				"        </ogc:PropertyIsEqualTo>"                                  +
				"        <ogc:PropertyIsEqualTo>"                                   +
				"            <ogc:PropertyName>apiso:OrganisationName</ogc:PropertyName>"     +
				"            <ogc:Literal>Georg Buechner</ogc:Literal>"             +
				"        </ogc:PropertyIsEqualTo>"                                  +
				"        <ogc:PropertyIsEqualTo>"                                   +
				"            <ogc:PropertyName>apiso:Identifier</ogc:PropertyName>" +
				"            <ogc:Literal>268</ogc:Literal>"                        +
				"        </ogc:PropertyIsEqualTo>"                                  +
				"    </ogc:Or> "                                                    +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "(title:\"Der Hessische Landbote\" OR organisationname:\"Georg Buechner\" OR identifier:268)");


		/**
		 * Test 4: a simple Filter Not propertyIsEqualTo
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\">"      +
				"    <ogc:Not>                                        "          +
				"        <ogc:PropertyIsEqualTo>"                                +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>"   +
				"            <ogc:Literal>Der Hessische Landbote</ogc:Literal>"  +
				"        </ogc:PropertyIsEqualTo>"                               +
				"    </ogc:Not>"                                                 +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getQuery(), "title:\"Der Hessische Landbote\"");
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.NOT);
	}


	/**
	 * Test simple Spatial filter
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testSimpleSpatialFilter() throws Exception {

		/**
		 * Test 1: a simple spatial Filter Intersects
		 */
		String XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"" +
				"            xmlns:gml=\"http://www.opengis.net/gml\">         "  +
				"    <ogc:Intersects>                                          "  +
				"       <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"         <gml:Envelope srsName=\"EPSG:4326\">                 "  +
				"             <gml:lowerCorner>7 12</gml:lowerCorner>          "  +
				"             <gml:upperCorner>20 20</gml:upperCorner>         "  +
				"        </gml:Envelope>                                       "  +
				"    </ogc:Intersects>                                         "  +
				"</ogc:Filter>";
		SpatialQuery spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		LuceneOGCFilter spatialFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spatialFilter.getOGCFilter() instanceof Intersects);

		/**
		 * Test 2: a simple Distance Filter DWithin
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"      " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">         " +
				"    <ogc:DWithin>                                             " +
				"      <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>  " +
				"        <gml:Point srsName=\"EPSG:4326\">                     " +
				"           <gml:coordinates>3.4 2.5</gml:coordinates>         " +
				"        </gml:Point>                                          " +
				"        <ogc:Distance units='m'>1000</ogc:Distance>           " +
				"    </ogc:DWithin>                                            " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spatialFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spatialFilter.getOGCFilter() instanceof DWithin);

		/**
		 * Test 3: a simple spatial Filter Intersects
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"" +
				"            xmlns:gml=\"http://www.opengis.net/gml\">         "  +
				"    <ogc:Intersects>                                          "  +
				"       <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"           <gml:LineString srsName=\"EPSG:4326\">             "  +
				"                <gml:coordinates ts=\" \" decimal=\".\" cs=\",\">1,2 10,15</gml:coordinates>" +
				"           </gml:LineString>                                  "  +
				"    </ogc:Intersects>                                         "  +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spatialFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spatialFilter.getOGCFilter() instanceof Intersects);
	}

	/**
	 * Test Multiple Spatial Filter
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testMultipleSpatialFilter() throws Exception {

		/**
		 * Test 1: two spatial Filter with AND
		 */
		String XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"     "  +
				"            xmlns:gml=\"http://www.opengis.net/gml\">               "  +
				"    <ogc:And>                                                       "  +
				"        <ogc:Intersects>                                            "  +
				"             <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"             <gml:Envelope srsName=\"EPSG:4326\">                   "  +
				"                 <gml:lowerCorner>7 12</gml:lowerCorner>            "  +
				"                 <gml:upperCorner>20 20</gml:upperCorner>           "  +
				"             </gml:Envelope>                                        "  +
				"        </ogc:Intersects>                                           "  +
				"        <ogc:Intersects>                                            "  +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>   "  +
				"             <gml:Envelope srsName=\"EPSG:4326\">                   "  +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>          "  +
				"                  <gml:upperCorner>12 12</gml:upperCorner>          "  +
				"             </gml:Envelope>                                        "  +
				"        </ogc:Intersects>                                           "  +
				"    </ogc:And>                                                      "  +
				"</ogc:Filter>";
		SpatialQuery spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		SerialChainFilter chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.AND);
		assertEquals(chainFilter.getChain().size(),       2);

		/**
		 * Test 2: three spatial Filter with OR
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"            "  +
				"            xmlns:gml=\"http://www.opengis.net/gml\">               "  +
				"    <ogc:Or>                                                        "  +
				"        <ogc:Intersects>                                            "  +
				"             <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"             <gml:Envelope srsName=\"EPSG:4326\">                   "  +
				"                 <gml:lowerCorner>7 12</gml:lowerCorner>            "  +
				"                 <gml:upperCorner>20 20</gml:upperCorner>           "  +
				"             </gml:Envelope>                                        "  +
				"        </ogc:Intersects>                                           "  +
				"        <ogc:Contains>                                              "  +
				"             <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"             <gml:Point srsName=\"EPSG:4326\">                      "  +
				"                 <gml:coordinates>3.4 2.5</gml:coordinates>         "  +
				"            </gml:Point>                                            "  +
				"        </ogc:Contains>                                             "  +
				"         <ogc:BBOX>                                                 "  +
				"              <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>"  +
				"              <gml:Envelope srsName=\"EPSG:4326\">                  "  +
				"                   <gml:lowerCorner>-20 -20</gml:lowerCorner>       "  +
				"                   <gml:upperCorner>20 20</gml:upperCorner>         "  +
				"              </gml:Envelope>                                       "  +
				"       </ogc:BBOX>                                                  "  +
				"    </ogc:Or>                                                       "  +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  2);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.OR);
		assertEquals(chainFilter.getActionType()[1],      SerialChainFilter.OR);
		assertEquals(chainFilter.getChain().size(),       3);

		//we verify each filter
		LuceneOGCFilter f1 = (LuceneOGCFilter) chainFilter.getChain().get(0);
		assertTrue(f1.getOGCFilter() instanceof Intersects);

		LuceneOGCFilter f2 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue(f2.getOGCFilter() instanceof Contains);

		LuceneOGCFilter f3 = (LuceneOGCFilter) chainFilter.getChain().get(2);
		assertTrue(f3.getOGCFilter() instanceof BBOX);

		/**
		 * Test 3: three spatial Filter F1 AND (F2 OR F3)
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               "  +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                  "  +
				"    <ogc:And>                                                          "  +
				"        <ogc:Intersects>                                               "  +
				"             <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>    "  +
				"             <gml:Envelope srsName=\"EPSG:4326\">                      "  +
				"                 <gml:lowerCorner>7 12</gml:lowerCorner>               "  +
				"                 <gml:upperCorner>20 20</gml:upperCorner>              "  +
				"             </gml:Envelope>                                           "  +
				"        </ogc:Intersects>                                              "  +
				"        <ogc:Or>                                                       "  +
				"            <ogc:Contains>                                             "  +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"                <gml:Point srsName=\"EPSG:4326\">                      "  +
				"                    <gml:coordinates>3.4 2.5</gml:coordinates>         "  +
				"                </gml:Point>                                           "  +
				"            </ogc:Contains>                                            "  +
				"            <ogc:BBOX>                                                 "  +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"                <gml:Envelope srsName=\"EPSG:4326\">                   "  +
				"                    <gml:lowerCorner>-20 -20</gml:lowerCorner>         "  +
				"                    <gml:upperCorner>20 20</gml:upperCorner>           "  +
				"                </gml:Envelope>                                        "  +
				"            </ogc:BBOX>                                                "  +
				"        </ogc:Or>                                                      "  +
				"    </ogc:And>                                                         "  +
				"</ogc:Filter>                                                          ";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(metafile:doc)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.AND);
		assertEquals(chainFilter.getChain().size(),       2);

		//we verify each filter
		f1 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue(f1.getOGCFilter() instanceof Intersects);

		SerialChainFilter cf2 = (SerialChainFilter) chainFilter.getChain().get(0);
		assertEquals(cf2.getActionType().length,  1);
		assertEquals(cf2.getActionType()[0],      SerialChainFilter.OR);
		assertEquals(cf2.getChain().size(),       2);


		LuceneOGCFilter cf2_1 = (LuceneOGCFilter) cf2.getChain().get(0);
		assertTrue(cf2_1.getOGCFilter() instanceof Contains);

		LuceneOGCFilter cf2_2 = (LuceneOGCFilter) cf2.getChain().get(1);
		assertTrue(cf2_2.getOGCFilter() instanceof BBOX);

		/**
		 * Test 4: three spatial Filter (NOT F1) AND F2 AND F3
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               "  +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                  "  +
				"    <ogc:And>                                                          "  +
				"        <ogc:Not>                                                      "  +
				"            <ogc:Intersects>                                           "  +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName> "  +
				"                <gml:Envelope srsName=\"EPSG:4326\">                   "  +
				"                    <gml:lowerCorner>7 12</gml:lowerCorner>            "  +
				"                    <gml:upperCorner>20 20</gml:upperCorner>           "  +
				"                </gml:Envelope>                                        "  +
				"            </ogc:Intersects>                                          "  +
				"        </ogc:Not>                                                     "  +
				"        <ogc:Contains>                                                 "  +
				"             <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>    "  +
				"             <gml:Point srsName=\"EPSG:4326\">                         "  +
				"                 <gml:coordinates>3.4 2.5</gml:coordinates>            "  +
				"            </gml:Point>                                               "  +
				"        </ogc:Contains>                                                "  +
				"         <ogc:BBOX>                                                    "  +
				"              <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>   "  +
				"              <gml:Envelope srsName=\"EPSG:4326\">                     "  +
				"                   <gml:lowerCorner>-20 -20</gml:lowerCorner>          "  +
				"                   <gml:upperCorner>20 20</gml:upperCorner>            "  +
				"              </gml:Envelope>                                          "  +
				"       </ogc:BBOX>                                                     "  +
				"    </ogc:And>                                                         "  +
				"</ogc:Filter>                                                          ";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(metafile:doc)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  2);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.AND);
		assertEquals(chainFilter.getActionType()[1],      SerialChainFilter.AND);
		assertEquals(chainFilter.getChain().size(),       3);

		//we verify each filter
		SerialChainFilter cf1 = (SerialChainFilter) chainFilter.getChain().get(0);
		assertEquals(cf1.getChain().size(), 1);
		assertEquals(cf1.getActionType().length,  1);
		assertEquals(cf1.getActionType()[0],      SerialChainFilter.NOT);

		LuceneOGCFilter cf1_1 = (LuceneOGCFilter) cf1.getChain().get(0);
		assertTrue(cf1_1.getOGCFilter() instanceof Intersects);


		f2 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue(f2.getOGCFilter() instanceof Contains);

		f3 = (LuceneOGCFilter) chainFilter.getChain().get(2);
		assertTrue(f3.getOGCFilter() instanceof BBOX);

		/**
		 * Test 5: three spatial Filter NOT (F1 OR F2) AND F3
		 */
		XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"                  "  +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                     "  +
				"    <ogc:And>                                                             "  +
				"        <ogc:Not>                                                         "  +
				"            <ogc:Or>                                                      "  +
				"                <ogc:Intersects>                                          "  +
				"                    <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>"  +
				"                    <gml:Envelope srsName=\"EPSG:4326\">                  "  +
				"                        <gml:lowerCorner>7 12</gml:lowerCorner>           "  +
				"                        <gml:upperCorner>20 20</gml:upperCorner>          "  +
				"                    </gml:Envelope>                                       "  +
				"                </ogc:Intersects>                                         "  +
				"                <ogc:Contains>                                            "  +
				"                    <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>"  +
				"                    <gml:Point srsName=\"EPSG:4326\">                     "  +
				"                        <gml:coordinates>3.4 2.5</gml:coordinates>        "  +
				"                    </gml:Point>                                          "  +
				"                </ogc:Contains>                                           "  +
				"           </ogc:Or>                                                      "  +
				"        </ogc:Not>                                                        "  +
				"         <ogc:BBOX>                                                       "  +
				"              <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>      "  +
				"              <gml:Envelope srsName=\"EPSG:4326\">                        "  +
				"                   <gml:lowerCorner>-20 -20</gml:lowerCorner>             "  +
				"                   <gml:upperCorner>20 20</gml:upperCorner>               "  +
				"              </gml:Envelope>                                             "  +
				"       </ogc:BBOX>                                                        "  +
				"    </ogc:And>                                                            "  +
				"</ogc:Filter>                                                             ";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(metafile:doc)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.AND);
		assertEquals(chainFilter.getChain().size(),       2);

		//we verify each filter
		cf1 = (SerialChainFilter) chainFilter.getChain().get(0);
		assertEquals(cf1.getChain().size(), 1);
		assertEquals(cf1.getActionType().length,  1);
		assertEquals(cf1.getActionType()[0],      SerialChainFilter.NOT);
		assertTrue(cf1.getChain().get(0) instanceof SerialChainFilter);

		SerialChainFilter cf1_cf1 =  (SerialChainFilter) cf1.getChain().get(0);
		assertEquals(cf1_cf1.getChain().size(),   2);
		assertEquals(cf1_cf1.getActionType().length,  1);
		assertEquals(cf1_cf1.getActionType()[0],      SerialChainFilter.OR);

		assertTrue(cf1_cf1.getChain().get(0) instanceof LuceneOGCFilter);
		LuceneOGCFilter cf1_cf1_1 = (LuceneOGCFilter) cf1_cf1.getChain().get(0);
		assertTrue(cf1_cf1_1.getOGCFilter() instanceof Intersects);

		assertTrue(cf1_cf1.getChain().get(1) instanceof LuceneOGCFilter);
		LuceneOGCFilter cf1_cf1_2 = (LuceneOGCFilter) cf1_cf1.getChain().get(1);
		assertTrue(cf1_cf1_2.getOGCFilter() instanceof Contains);

		f2 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue(f2.getOGCFilter() instanceof BBOX);
	}

	/**
	 * Test complex query with both comparison, logical and spatial query
	 * 
	 * @throws java.lang.Exception
	 */
	@Test
	public void testMultipleMixedFilter() throws Exception {

		/**
		 * Test 1: PropertyIsLike AND INTERSECT
		 */
		String XMLrequest ="<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:And>                                                                 " +
				"        <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"           <ogc:PropertyName>apiso:Title</ogc:PropertyName>                   " +
				"           <ogc:Literal>*VM*</ogc:Literal>                                    " +
				"        </ogc:PropertyIsLike>                                                 " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"    </ogc:And>                                                                " +
				"</ogc:Filter>";
		SpatialQuery spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:*VM*)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		LuceneOGCFilter spaFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spaFilter.getOGCFilter() instanceof Intersects);

		/**
		 * Test 2: PropertyIsLike AND INTERSECT AND propertyIsEquals
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:And>                                                                 " +
				"        <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"           <ogc:PropertyName>apiso:Title</ogc:PropertyName>                   " +
				"           <ogc:Literal>*VM*</ogc:Literal>                                    " +
				"        </ogc:PropertyIsLike>                                                 " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"        <ogc:PropertyIsEqualTo>                                               " +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"            <ogc:Literal>VM</ogc:Literal>                                     " +
				"        </ogc:PropertyIsEqualTo>                                              " +
				"    </ogc:And>                                                                " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:*VM* AND title:VM)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spaFilter.getOGCFilter() instanceof Intersects);

		/**
		 * Test 3:  INTERSECT AND propertyIsEquals AND BBOX
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:And>                                                                 " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"        <ogc:PropertyIsEqualTo>                                               " +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"            <ogc:Literal>VM</ogc:Literal>                                     " +
				"        </ogc:PropertyIsEqualTo>                                              " +
				"         <ogc:BBOX>                                                           " +
				"              <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>          " +
				"              <gml:Envelope srsName=\"EPSG:4326\">                            " +
				"                   <gml:lowerCorner>-20 -20</gml:lowerCorner>                 " +
				"                   <gml:upperCorner>20 20</gml:upperCorner>                   " +
				"              </gml:Envelope>                                                 " +
				"       </ogc:BBOX>                                                            " +
				"    </ogc:And>                                                                " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:VM)");
		assertEquals(spaQuery.getSubQueries().size(), 0);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		SerialChainFilter chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.AND);
		assertEquals(chainFilter.getChain().size(),       2);

		LuceneOGCFilter f1 = (LuceneOGCFilter) chainFilter.getChain().get(0);
		assertTrue (f1.getOGCFilter() instanceof Intersects);

		LuceneOGCFilter f2 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue (f2.getOGCFilter() instanceof BBOX);

		/**
		 * Test 4: PropertyIsLike OR INTERSECT OR propertyIsEquals
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:Or>                                                                  " +
				"        <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"           <ogc:PropertyName>apiso:Title</ogc:PropertyName>                   " +
				"           <ogc:Literal>*VM*</ogc:Literal>                                    " +
				"        </ogc:PropertyIsLike>                                                 " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"        <ogc:PropertyIsEqualTo>                                               " +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"            <ogc:Literal>VM</ogc:Literal>                                     " +
				"        </ogc:PropertyIsEqualTo>                                              " +
				"    </ogc:Or>                                                                 " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:*VM* OR title:VM)");
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.OR);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue(spaFilter.getOGCFilter() instanceof Intersects);

		/**
		 * Test 5:  INTERSECT OR propertyIsEquals OR BBOX
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:Or>                                                                  " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"        <ogc:PropertyIsEqualTo>                                               " +
				"            <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"            <ogc:Literal>VM</ogc:Literal>                                     " +
				"        </ogc:PropertyIsEqualTo>                                              " +
				"         <ogc:BBOX>                                                           " +
				"              <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>          " +
				"              <gml:Envelope srsName=\"EPSG:4326\">                            " +
				"                   <gml:lowerCorner>-20 -20</gml:lowerCorner>                 " +
				"                   <gml:upperCorner>20 20</gml:upperCorner>                   " +
				"              </gml:Envelope>                                                 " +
				"       </ogc:BBOX>                                                            " +
				"    </ogc:Or>                                                                 " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:VM)");
		assertEquals(spaQuery.getSubQueries().size(), 0);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.OR);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.OR);
		assertEquals(chainFilter.getChain().size(),       2);

		f1 = (LuceneOGCFilter) chainFilter.getChain().get(0);
		assertTrue (f1.getOGCFilter() instanceof Intersects);

		f2 = (LuceneOGCFilter) chainFilter.getChain().get(1);
		assertTrue (f2.getOGCFilter() instanceof BBOX);

		/**
		 * Test 6:  INTERSECT AND (propertyIsEquals OR BBOX)
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"               " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                         " +
				"    <ogc:And>                                                                 " +
				"        <ogc:Intersects>                                                      " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>             " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                             " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                    " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                    " +
				"             </gml:Envelope>                                                  " +
				"        </ogc:Intersects>                                                     " +
				"        <ogc:Or>                                                              " +
				"            <ogc:PropertyIsEqualTo>                                           " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>              " +
				"                <ogc:Literal>VM</ogc:Literal>                                 " +
				"            </ogc:PropertyIsEqualTo>                                          " +
				"            <ogc:BBOX>                                                        " +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>        " +
				"                <gml:Envelope srsName=\"EPSG:4326\">                          " +
				"                    <gml:lowerCorner>-20 -20</gml:lowerCorner>                " +
				"                    <gml:upperCorner>20 20</gml:upperCorner>                  " +
				"               </gml:Envelope>                                                " +
				"            </ogc:BBOX>                                                       " +
				"        </ogc:Or>                                                             " +
				"    </ogc:And>                                                                " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "metafile:doc");
		assertEquals(spaQuery.getSubQueries().size(), 1);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof Intersects);

		SpatialQuery subQuery1 = spaQuery.getSubQueries().get(0);
		assertTrue  (subQuery1.getSpatialFilter() != null);
		assertEquals(subQuery1.getQuery(), "(title:VM)");
		assertEquals(subQuery1.getSubQueries().size(), 0);
		assertEquals(subQuery1.getLogicalOperator(), SerialChainFilter.OR);

		assertTrue(subQuery1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof BBOX);

		/**
		 * Test 7:  propertyIsNotEquals OR (propertyIsLike AND DWITHIN)
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"                       " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                                 " +
				"        <ogc:Or>                                                                      " +
				"            <ogc:PropertyIsNotEqualTo>                                                " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>VMAI</ogc:Literal>                                       " +
				"            </ogc:PropertyIsNotEqualTo>                                               " +
				"            <ogc:And>                                                                 " +
				"                <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"                    <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"                    <ogc:Literal>LO?Li</ogc:Literal>                                  " +
				"                </ogc:PropertyIsLike>                                                 " +
				"                <ogc:DWithin>                                                         " +
				"                    <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>            " +
				"                    <gml:Point srsName=\"EPSG:4326\">                                 " +
				"                        <gml:coordinates>3.4 2.5</gml:coordinates>                    " +
				"                    </gml:Point>                                                      " +
				"                    <ogc:Distance units='m'>1000</ogc:Distance>                       " +
				"                </ogc:DWithin>                                                        " +
				"            </ogc:And>                                                                " +
				"        </ogc:Or>                                                                     " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() == null);
		assertEquals(spaQuery.getQuery(), "(metafile:doc NOT title:VMAI)");
		assertEquals(spaQuery.getSubQueries().size(), 1);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.OR);

		subQuery1 = spaQuery.getSubQueries().get(0);
		assertTrue  (subQuery1.getSpatialFilter() != null);
		assertEquals(subQuery1.getQuery(), "(title:LO?Li)");
		assertEquals(subQuery1.getSubQueries().size(), 0);
		assertEquals(subQuery1.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(subQuery1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof DWithin);


		/**
		 * Test 8:  propertyIsLike AND INTERSECT AND (propertyIsEquals OR BBOX) AND (propertyIsNotEquals OR (Beyond AND propertyIsLike))
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"                       " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                                 " +
				"    <ogc:And>                                                                         " +
				"        <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">        " +
				"           <ogc:PropertyName>apiso:Title</ogc:PropertyName>                           " +
				"           <ogc:Literal>*VM*</ogc:Literal>                                            " +
				"        </ogc:PropertyIsLike>                                                         " +
				"        <ogc:Intersects>                                                              " +
				"           <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>                     " +
				"             <gml:Envelope srsName=\"EPSG:4326\">                                     " +
				"                  <gml:lowerCorner>-2 -4</gml:lowerCorner>                            " +
				"                  <gml:upperCorner>12 12</gml:upperCorner>                            " +
				"             </gml:Envelope>                                                          " +
				"        </ogc:Intersects>                                                             " +
				"        <ogc:Or>                                                                      " +
				"            <ogc:PropertyIsEqualTo>                                                   " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>PLOUF</ogc:Literal>                                      " +
				"            </ogc:PropertyIsEqualTo>                                                  " +
				"            <ogc:BBOX>                                                                " +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>                " +
				"                <gml:Envelope srsName=\"EPSG:4326\">                                  " +
				"                    <gml:lowerCorner>-20 -20</gml:lowerCorner>                        " +
				"                    <gml:upperCorner>20 20</gml:upperCorner>                          " +
				"               </gml:Envelope>                                                        " +
				"            </ogc:BBOX>                                                               " +
				"        </ogc:Or>                                                                     " +
				"        <ogc:Or>                                                                      " +
				"            <ogc:PropertyIsNotEqualTo>                                                " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>VMAI</ogc:Literal>                                       " +
				"            </ogc:PropertyIsNotEqualTo>                                               " +
				"            <ogc:And>                                                                 " +
				"                <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"                    <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"                    <ogc:Literal>LO?Li</ogc:Literal>                                  " +
				"                </ogc:PropertyIsLike>                                                 " +
				"                <ogc:DWithin>                                                         " +
				"                    <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>            " +
				"                    <gml:Point srsName=\"EPSG:4326\">                                 " +
				"                        <gml:coordinates>3.4 2.5</gml:coordinates>                    " +
				"                    </gml:Point>                                                      " +
				"                    <ogc:Distance units='m'>1000</ogc:Distance>                       " +
				"                </ogc:DWithin>                                                        " +
				"            </ogc:And>                                                                " +
				"        </ogc:Or>                                                                     " +
				"    </ogc:And>                                                                        " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(title:*VM*)");
		assertEquals(spaQuery.getSubQueries().size(), 2);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(spaQuery.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) spaQuery.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof Intersects);

		subQuery1 = spaQuery.getSubQueries().get(0);
		assertTrue  (subQuery1.getSpatialFilter() != null);
		assertEquals(subQuery1.getQuery(), "(title:PLOUF)");
		assertEquals(subQuery1.getSubQueries().size(), 0);
		assertEquals(subQuery1.getLogicalOperator(), SerialChainFilter.OR);

		assertTrue(subQuery1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof BBOX);

		SpatialQuery subQuery2 = spaQuery.getSubQueries().get(1);
		assertTrue  (subQuery2.getSpatialFilter() == null);
		assertEquals(subQuery2.getQuery(), "(metafile:doc NOT title:VMAI)");
		assertEquals(subQuery2.getSubQueries().size(), 1);
		assertEquals(subQuery2.getLogicalOperator(), SerialChainFilter.OR);

		SpatialQuery subQuery2_1 = subQuery2.getSubQueries().get(0);
		assertTrue  (subQuery2_1.getSpatialFilter() != null);
		assertEquals(subQuery2_1.getQuery(), "(title:LO?Li)");
		assertEquals(subQuery2_1.getSubQueries().size(), 0);
		assertEquals(subQuery2_1.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(subQuery2_1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery2_1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof  DWithin);


		/**
		 * Test 9:  NOT propertyIsLike AND NOT INTERSECT AND NOT (propertyIsEquals OR BBOX) AND (propertyIsNotEquals OR (Beyond AND propertyIsLike))
		 */
		XMLrequest =       "<ogc:Filter xmlns:ogc=\"http://www.opengis.net/ogc\"                       " +
				"            xmlns:gml=\"http://www.opengis.net/gml\">                                 " +
				"    <ogc:And>                                                                         " +
				"        <ogc:Not>                                                                     " +
				"            <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">    " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>*VM*</ogc:Literal>                                       " +
				"            </ogc:PropertyIsLike>                                                     " +
				"        </ogc:Not>                                                                    " +
				"        <ogc:Not>                                                                     " +
				"            <ogc:Intersects>                                                          " +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>                " +
				"                <gml:Envelope srsName=\"EPSG:4326\">                                  " +
				"                    <gml:lowerCorner>-2 -4</gml:lowerCorner>                          " +
				"                    <gml:upperCorner>12 12</gml:upperCorner>                          " +
				"                </gml:Envelope>                                                       " +
				"            </ogc:Intersects>                                                         " +
				"        </ogc:Not>                                                                    " +
				"        <ogc:Not>                                                                     " +
				"        <ogc:Or>                                                                      " +
				"            <ogc:PropertyIsEqualTo>                                                   " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>PLOUF</ogc:Literal>                                      " +
				"            </ogc:PropertyIsEqualTo>                                                  " +
				"            <ogc:BBOX>                                                                " +
				"                <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>                " +
				"                <gml:Envelope srsName=\"EPSG:4326\">                                  " +
				"                    <gml:lowerCorner>-20 -20</gml:lowerCorner>                        " +
				"                    <gml:upperCorner>20 20</gml:upperCorner>                          " +
				"               </gml:Envelope>                                                        " +
				"            </ogc:BBOX>                                                               " +
				"        </ogc:Or>                                                                     " +
				"        </ogc:Not>                                                                    " +
				"        <ogc:Or>                                                                      " +
				"            <ogc:PropertyIsNotEqualTo>                                                " +
				"                <ogc:PropertyName>apiso:Title</ogc:PropertyName>                      " +
				"                <ogc:Literal>VMAI</ogc:Literal>                                       " +
				"            </ogc:PropertyIsNotEqualTo>                                               " +
				"            <ogc:And>                                                                 " +
				"                <ogc:PropertyIsLike escapeChar=\"\\\" singleChar=\"?\" wildCard=\"*\">" +
				"                    <ogc:PropertyName>apiso:Title</ogc:PropertyName>                  " +
				"                    <ogc:Literal>LO?Li</ogc:Literal>                                  " +
				"                </ogc:PropertyIsLike>                                                 " +
				"                <ogc:DWithin>                                                         " +
				"                    <ogc:PropertyName>apiso:BoundingBox</ogc:PropertyName>            " +
				"                    <gml:Point srsName=\"EPSG:4326\">                                 " +
				"                        <gml:coordinates>3.4 2.5</gml:coordinates>                    " +
				"                    </gml:Point>                                                      " +
				"                    <ogc:Distance units='m'>1000</ogc:Distance>                       " +
				"                </ogc:DWithin>                                                        " +
				"            </ogc:And>                                                                " +
				"        </ogc:Or>                                                                     " +
				"    </ogc:And>                                                                        " +
				"</ogc:Filter>";
		spaQuery = this.filterParser.parse(StringUtils.stringToDocument(XMLrequest));

		assertTrue(spaQuery.getSpatialFilter() != null);
		assertEquals(spaQuery.getQuery(), "(metafile:doc)");
		assertEquals(spaQuery.getSubQueries().size(), 3);
		assertEquals(spaQuery.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(spaQuery.getSpatialFilter() instanceof SerialChainFilter);
		chainFilter = (SerialChainFilter) spaQuery.getSpatialFilter();

		assertEquals(chainFilter.getActionType().length,  1);
		assertEquals(chainFilter.getActionType()[0],      SerialChainFilter.NOT);
		assertEquals(chainFilter.getChain().size(),       1);

		f1 = (LuceneOGCFilter) chainFilter.getChain().get(0);

		assertTrue (f1.getOGCFilter() instanceof Intersects);

		// first sub-query
		subQuery1 = spaQuery.getSubQueries().get(0);
		assertTrue  (subQuery1.getSpatialFilter() == null);
		assertEquals(subQuery1.getQuery(), "title:*VM*");
		assertEquals(subQuery1.getSubQueries().size(), 0);
		assertEquals(subQuery1.getLogicalOperator(), SerialChainFilter.NOT);


		// second sub-query
		subQuery2 = spaQuery.getSubQueries().get(1);
		assertTrue  (subQuery2.getSpatialFilter() == null);
		assertEquals(subQuery2.getQuery(), "metafile:doc");
		assertEquals(subQuery2.getSubQueries().size(), 1);
		assertEquals(subQuery2.getLogicalOperator(), SerialChainFilter.NOT);

		// second subQuery => first subQuery
		subQuery2_1 = subQuery2.getSubQueries().get(0);
		assertTrue  (subQuery2_1.getSpatialFilter() != null);
		assertEquals(subQuery2_1.getQuery(), "(title:PLOUF)");
		assertEquals(subQuery2_1.getSubQueries().size(), 0);
		assertEquals(subQuery2_1.getLogicalOperator(), SerialChainFilter.OR);

		assertTrue(subQuery2_1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery2_1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof BBOX);

		// third sub-query
		SpatialQuery subQuery3 = spaQuery.getSubQueries().get(2);
		assertTrue  (subQuery3.getSpatialFilter() == null);
		assertEquals(subQuery3.getQuery(), "(metafile:doc NOT title:VMAI)");
		assertEquals(subQuery3.getSubQueries().size(), 1);
		assertEquals(subQuery3.getLogicalOperator(), SerialChainFilter.OR);

		SpatialQuery subQuery3_1 = subQuery3.getSubQueries().get(0);
		assertTrue  (subQuery3_1.getSpatialFilter() != null);
		assertEquals(subQuery3_1.getQuery(), "(title:LO?Li)");
		assertEquals(subQuery3_1.getSubQueries().size(), 0);
		assertEquals(subQuery3_1.getLogicalOperator(), SerialChainFilter.AND);

		assertTrue(subQuery3_1.getSpatialFilter() instanceof LuceneOGCFilter);
		spaFilter = (LuceneOGCFilter) subQuery3_1.getSpatialFilter();

		assertTrue (spaFilter.getOGCFilter() instanceof  DWithin);
	}
}
