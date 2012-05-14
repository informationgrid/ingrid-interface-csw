package de.ingrid.interfaces.csw.admin;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.domain.query.CSWQuery;
import de.ingrid.interfaces.csw.search.CSWRecordResults;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

@Controller
public class SearchTestController {

    final LuceneSearcher searcher;

    public static final String TEMPLATE_SEARCH_URI = "/search.html";
    public static final String TEMPLATE_SEARCH_VIEW = "/search";

    @Autowired
    public SearchTestController(final LuceneSearcher searcher) {
        this.searcher = searcher;
    }

    @RequestMapping(value = TEMPLATE_SEARCH_URI, method = RequestMethod.GET)
    public String getScheduling(final ModelMap modelMap) {
        modelMap.addAttribute("query", "");
        return TEMPLATE_SEARCH_VIEW;
    }

    @RequestMapping(value = TEMPLATE_SEARCH_URI, method = RequestMethod.POST)
    public String postScheduling(final ModelMap modelMap, @RequestParam(value = "query", required = false) String query) {

        modelMap.addAttribute("query", query);

        DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        Document queryDocument = null;
        try {
            queryDocument = df.newDocumentBuilder().parse(new InputSource(new StringReader(query)));
        } catch (Exception e) {
            try {
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
            if (Log.isDebugEnabled()) {
                Log.debug("Incoming request: " + StringUtils.nodeToString(queryDocument.getDocumentElement()));
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
