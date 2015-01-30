/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
package de.ingrid.interfaces.csw.admin;

import java.io.StringReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.search.CSWRecordResults;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

@Controller
public class SearchTestController {

    final protected static Log log = LogFactory.getLog(SearchTestController.class);

    final LuceneSearcher searcher;

    public static final String TEMPLATE_SEARCH_URI = "/search.html";
    public static final String TEMPLATE_SEARCH_VIEW = "/search";
    
    private final String baseLink;

    @Autowired
    public SearchTestController(final LuceneSearcher searcher) {
        this.searcher = searcher;
        String host = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_HOST, "localhost");
        String port = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_PORT, "80");
        String path = ApplicationProperties.get(ConfigurationKeys.SERVER_INTERFACE_PATH, "");
        baseLink = "http://" + host + ":" + port + "/" + path + "?REQUEST=GetRecordById&SERVICE=CSW&ID=RECORD_ID";
    }

    @RequestMapping(value = TEMPLATE_SEARCH_URI, method = RequestMethod.GET)
    public String getScheduling(final ModelMap modelMap) {
        modelMap.addAttribute("query", "");
        return TEMPLATE_SEARCH_VIEW;
    }

    @RequestMapping(value = TEMPLATE_SEARCH_URI, method = RequestMethod.POST)
    public String postScheduling(final ModelMap modelMap, @RequestParam(value = "query", required = false) String query) {

        if (query == null || query.trim().length() == 0) {
            return TEMPLATE_SEARCH_VIEW;
        }
        
        modelMap.addAttribute("query", query);

        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        Document queryDocument = null;
        try {
            queryDocument = df.newDocumentBuilder().parse(new InputSource(new StringReader(query)));
        } catch (Exception e) {
            try {
            	if (log.isDebugEnabled()) {
                	log.debug("Exception building queryDocument via DocumentBuilderFactory (query '" + query + "'):" + e.getMessage());            		
                	log.debug("We set up own queryDocument");            		
            	}
                queryDocument = df
                        .newDocumentBuilder()
                        .parse(
                                new InputSource(
                                        new StringReader(
                                                "<GetRecords maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
                                                        + "            requestId=\"csw:1\" resultType=\"results\" startPosition=\"1\"\n"
                                                        + "            xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\" version=\"2.0.2\">\n"
                                                        + "            <Query typeNames=\"csw:service,csw:dataset\">\n"
                                                        + "                <ElementSetName typeNames=\"\">full</ElementSetName>\n"
                                                        + "                <Constraint version=\"1.1.0\"> \n"
                                                        + "                    <Filter xmlns=\"http://www.opengis.net/ogc\">\n"
                                                        + "                        <PropertyIsEqualTo>\n"
                                                        + "                            <PropertyName>AnyText</PropertyName>\n"
                                                        + "                            <Literal>" + query
                                                        + "</Literal>\n"
                                                        + "                        </PropertyIsEqualTo>\n"
                                                        + "                    </Filter>\n"
                                                        + "                </Constraint>\n" + "            </Query>\n"
                                                        + "        </GetRecords>")));

            } catch (Exception e1) {
                throw new RuntimeException("Error parsing request: ", e1);
            }

        }
        XMLEncoding encoding = new XMLEncoding();
        try {
            if (log.isDebugEnabled()) {
            	log.debug("Incoming request: " + StringUtils.nodeToString(queryDocument.getDocumentElement()));
            }
            CSWQuery cswQuery = encoding.getQuery(queryDocument.getDocumentElement());
            CSWRecordResults results = searcher.search(cswQuery);
            XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());
            Map<String, String> map;
            List<Map<String, String>> resultDisplayList = new ArrayList<Map<String, String>>();
            if (results.getResults() != null) {
                for (CSWRecord record : results.getResults()) {
                    map = new HashMap<String, String>();
                    map.put("title", xpath.getString(record.getDocument(), "//gmd:title/gco:CharacterString"));
                    map.put("link", baseLink.replaceAll("RECORD_ID", URLEncoder.encode(xpath.getString(record.getDocument(), "//gmd:fileIdentifier/gco:CharacterString"), "UTF-8")));
                    map.put("abstract", xpath.getString(record.getDocument(), "//gmd:abstract/gco:CharacterString"));
                    resultDisplayList.add(map);
                }
            }
            modelMap.addAttribute("hits", resultDisplayList);
            modelMap.addAttribute("hitCount", cswQuery.getMaxRecords() > results.getTotalHits() ? results
                    .getTotalHits() : cswQuery.getMaxRecords());
            modelMap.addAttribute("totalHitCount", results.getTotalHits());
        } catch (Exception e) {
            throw new RuntimeException("Error performing query: ", e);
        }

        return TEMPLATE_SEARCH_VIEW;
    }

}
