/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.analyse.CommonAnalyser;
import de.ingrid.interfaces.csw.analyse.DescRecAnalyser;
import de.ingrid.interfaces.csw.analyse.GetCapAnalyser;
import de.ingrid.interfaces.csw.analyse.GetRecAnalyser;
import de.ingrid.interfaces.csw.analyse.GetRecByIdAnalyser;
import de.ingrid.interfaces.csw.analyse.Namespaces;
import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.tools.AxisTools;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.tools.ServletTools;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.tools.XPathUtils;
import de.ingrid.interfaces.csw.transform.RequestTransformer;
import de.ingrid.interfaces.csw.transform.response.CSWResponseTransformer;
import de.ingrid.interfaces.csw.utils.CswConfig;
import de.ingrid.interfaces.csw.utils.IBusHelper;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.ClauseQuery;
import de.ingrid.utils.query.FieldQuery;
import de.ingrid.utils.query.IngridQuery;

/**
 * This class represents the catalogue service web (CSW)
 * 
 * @author rschaefer
 * @author joachim@wemove.com
 */
public class CSW {
    /**
     * the log object
     */
    private static Log log = LogFactory.getLog(CSW.class);

    /*
     * Static object used to retrieve the config data
     */
    private static CSWInterfaceConfig cswConfig = CSWInterfaceConfig.getInstance();
    
    private HttpServletRequest request = null;

    public CSW(HttpServletRequest request) {
    	this.request = request;
    }
    
    
    /**
     * This method performs a SOAP request
     * 
     * @param soapRequestMessage
     *            Message
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    protected final Message doSoapRequest(final SOAPMessage soapRequestMessage) throws Exception {
    	Document responseDoc = null;
		Properties reqParams = ServletTools.createPropertiesFromRequest(request, true); // all

        SessionParameters sessionParameters = new SessionParameters();
		sessionParameters.setPartner(reqParams.getProperty("PARTNER"));
		sessionParameters.setProvider(reqParams.getProperty("PROVIDER"));
		sessionParameters.setIplugId(reqParams.getProperty("IPLUG"));

		CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        SOAPBody body = soapRequestMessage.getSOAPBody();
        SOAPBodyElement be = (SOAPBodyElement) body.getFirstChild();

        // String nameSpacePrefix = be.getPrefix();
        // System.out.println("nameSpacePrefix: " + nameSpacePrefix);

        // test if namespace is correct
        String nameSpaceURI = be.getNamespaceURI();
        // System.out.println("doRequest nameSpaceURI: " + nameSpaceURI);

        // String opName = be.getNodeName();
        String opName = be.getLocalName();

        if (nameSpaceURI != null && !"".equals(nameSpaceURI) && !Namespaces.CSW.equals(nameSpaceURI) && !Namespaces.CSW_2_0_2.equals(nameSpaceURI)) {
            Exception e = new CSWOperationNotSupportedException("Operation '" + opName + "' within namespace URI '"
                    + nameSpaceURI + "' is not supported.", opName);
            throw e;
        }

        // TODO remove opName analyse in analysers?
        if (!commonAnalyser.analyseOperationName(opName)) {
            throw new CSWOperationNotSupportedException("Operation '" + opName + "' is not supported.", opName);
        }

        if (sessionParameters.isOperationIsGetCap()) {
            GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
            if (getCapAnalyser.analyse(be)) {
            	responseDoc = doGetCapabilitiesRequest();
            }
        } else if (sessionParameters.isOperationIsGetRecs()) {
            GetRecAnalyser getRecAnalyser = new GetRecAnalyser(sessionParameters);

            if (getRecAnalyser.analyse(be)) {
                /*
                 * // DEBUG Dirk Schwarzmann // Return a predefined file with
                 * reference content of GeoTask... log.debug("Sending fake
                 * GetRecords answer (from GeoTask)"); URL url = new
                 * URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_FAKERESPONSE));
                 * Reader reader = new InputStreamReader(url.openStream());
                 * Document doc = XMLTools.parse(reader); soapResponseMessage =
                 * AxisTools.createSOAPMessage(doc); log.debug("Finished faking
                 * answer");
                 */
                // The original method:
            	responseDoc = doGetRecordsRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsGetRecById()) {
            GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);

            if (getRecByIdAnalyser.analyse(be)) {
            	responseDoc = doGetRecordByIdRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsDescRec()) {
            DescRecAnalyser descRecAnalyser = new DescRecAnalyser();

            if (descRecAnalyser.analyse(be)) {
            	responseDoc = doDescribeRecordRequest();
            }
        }
        Message soapResponseMessage = AxisTools.createSOAPMessage(responseDoc);
        return soapResponseMessage;
    }

    /**
     * This method performs a SOAP request
     * 
     * @param soapRequestMessage
     *            Message
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    protected final Document doPostRequest(final Document inDoc) throws Exception {
        Document respDoc = null;
		Properties reqParams = ServletTools.createPropertiesFromRequest(request, true); // all

        SessionParameters sessionParameters = new SessionParameters();
		sessionParameters.setPartner(reqParams.getProperty("PARTNER"));
		sessionParameters.setProvider(reqParams.getProperty("PROVIDER"));
		sessionParameters.setIplugId(reqParams.getProperty("IPLUG"));

        CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        Element be = (Element)inDoc.getFirstChild();

        // String nameSpacePrefix = be.getPrefix();
        // System.out.println("nameSpacePrefix: " + nameSpacePrefix);

        // test if namespace is correct
        String nameSpaceURI = be.getNamespaceURI();
        // System.out.println("doRequest nameSpaceURI: " + nameSpaceURI);

        // String opName = be.getNodeName();
        String opName = be.getLocalName();

        if (nameSpaceURI != null && !"".equals(nameSpaceURI) && !Namespaces.CSW.equals(nameSpaceURI) && !Namespaces.CSW_2_0_2.equals(nameSpaceURI)) {
            Exception e = new CSWOperationNotSupportedException("Operation '" + opName + "' within namespace URI '"
                    + nameSpaceURI + "' is not supported.", opName);
            throw e;
        }

        // TODO remove opName analyse in analysers?
        if (!commonAnalyser.analyseOperationName(opName)) {
            throw new CSWOperationNotSupportedException("Operation '" + opName + "' is not supported.", opName);
        }

        if (sessionParameters.isOperationIsGetCap()) {
            GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
            if (getCapAnalyser.analyse(be)) {
            	respDoc = doGetCapabilitiesRequest();
            }
        } else if (sessionParameters.isOperationIsGetRecs()) {
            GetRecAnalyser getRecAnalyser = new GetRecAnalyser(sessionParameters);

            if (getRecAnalyser.analyse(be)) {
                /*
                 * // DEBUG Dirk Schwarzmann // Return a predefined file with
                 * reference content of GeoTask... log.debug("Sending fake
                 * GetRecords answer (from GeoTask)"); URL url = new
                 * URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_FAKERESPONSE));
                 * Reader reader = new InputStreamReader(url.openStream());
                 * Document doc = XMLTools.parse(reader); soapResponseMessage =
                 * AxisTools.createSOAPMessage(doc); log.debug("Finished faking
                 * answer");
                 */
                // The original method:
            	respDoc = doGetRecordsRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsGetRecById()) {
            GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);

            if (getRecByIdAnalyser.analyse(be)) {
            	respDoc = doGetRecordByIdRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsDescRec()) {
            DescRecAnalyser descRecAnalyser = new DescRecAnalyser();

            if (descRecAnalyser.analyse(be)) {
            	respDoc = doDescribeRecordRequest();
            }
        }
        return respDoc;
    }    
    
    /**
     * performs a describe record request
     * 
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Document doDescribeRecordRequest() throws Exception {
        URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_DESCRIBERECORD));
        Reader reader = new InputStreamReader(url.openStream());
        return XMLTools.parse(reader);
    }

    /**
     * performs a get capabilities request
     * 
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Document doGetCapabilitiesRequest() throws Exception {
        URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_GETCAPABILITIES));
        Reader reader = new InputStreamReader(url.openStream());
        Document doc = XMLTools.parse(reader);
        
        // try to replace the interface URLs
        NodeList nodes = XPathUtils.getNodeList(doc, "//ows:Operation/*/ows:HTTP/*/@xlink:href");
		// get host
        String host = CswConfig.getInstance().getString(CswConfig.SERVER_INTERFACE_HOST, null);
        if (host == null) {
        	log.info("The interface host address is not specified, use local hosts address instead.");
        	try {
                InetAddress addr = InetAddress.getLocalHost();
                host = addr.getHostAddress();
            } catch (UnknownHostException e) {
            	log.error("Unable to get interface host address.", e);
            	throw e;
            }
        }
		// get port
        String port = CswConfig.getInstance().getString(CswConfig.SERVER_INTERFACE_PORT, "80");
		// get path
        String path = CswConfig.getInstance().getString(CswConfig.SERVER_INTERFACE_PATH, "csw");
        // replace interface host and port and path
        for (int idx = 0; idx < nodes.getLength(); idx++) {
			String s = nodes.item(idx).getTextContent();
			s = s.replaceAll(CswConfig.KEY_INTERFACE_HOST, host);
			s = s.replaceAll(CswConfig.KEY_INTERFACE_PORT, port);
			s = s.replaceAll(CswConfig.KEY_INTERFACE_PATH, path);
			nodes.item(idx).setTextContent(s);
		}
        
        return doc;
    }

    /**
     * performs a get records request
     * 
     * @param be
     *            SOAPBodyElement
     * @param sessionParameters
     *            SessionParameters
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Document doGetRecordsRequest(final Element be, final SessionParameters sessionParameters)
            throws Exception {

        // transform the OGC filter (request) into IngridQuery; including
        // SessionParameters
        Element soapElementFilter = (Element) be.getElementsByTagNameNS("http://www.opengis.net/ogc", "Filter").item(0);
        RequestTransformer requestTransformer = new RequestTransformer();
        IngridQuery ingridQuery = requestTransformer.transform(soapElementFilter, sessionParameters);

        IngridHits hits = doBusRequest(ingridQuery, sessionParameters);

        CSWResponseTransformer responseTransformer = new CSWResponseTransformer();
        return responseTransformer.transform(hits, ingridQuery, sessionParameters);
    }

    /**
     * This method forwards the query to the iBus and returns an IngridHit array
     * with all results from csw, ecs and dsc_ecs datatype in case results
     * should be returned at all. Otherwise, only the amount of hits are set in
     * the session parameters.
     * 
     * @param ingridQuery
     * @param sessionParameters
     * @return results IngridHits
     * @throws Exception
     */
    protected static final IngridHits doBusRequest(IngridQuery ingridQuery, final SessionParameters sessionParameters)
            throws Exception {

        int requestedHits = sessionParameters.getMaxRecords();
        int startPosition = sessionParameters.getStartPosition();

        ingridQuery = setDataTypeCSW(ingridQuery);
        ingridQuery = setSourceType(ingridQuery, sessionParameters);
        ingridQuery = setQueryExtensions(ingridQuery, sessionParameters);

        IngridHits hits = callBus(ingridQuery, requestedHits, startPosition);
        long totalHits = hits.length();
    	if (log.isInfoEnabled()) {
    		log.info("Hits for CSW: " + totalHits);
    	}

        sessionParameters.setNumberOfRecordsMatched((int) totalHits);

        return hits;
    }

    private static IngridQuery setQueryExtensions(IngridQuery ingridQuery, final SessionParameters sessionParameters) {
            if (sessionParameters.getPartner() != null && sessionParameters.getPartner().length() > 0) {
            	ingridQuery.addField(new FieldQuery(true, false, "partner", sessionParameters.getPartner()));
            }
            if (sessionParameters.getProvider() != null && sessionParameters.getProvider().length() > 0) {
            	ingridQuery.addField(new FieldQuery(true, false, "provider", sessionParameters.getProvider()));
            }
            if (sessionParameters.getIplugId() != null && sessionParameters.getIplugId().length() > 0) {
            	ingridQuery.addField(new FieldQuery(true, false, "iplugs", sessionParameters.getIplugId()));
            }
            
            return ingridQuery;
	}

	/**
     * performs a get record by id request
     * 
     * @param be
     *            SOAPBodyElement
     * @param sessionParameters
     *            SessionParameters
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Document doGetRecordByIdRequest(final Element be, final SessionParameters sessionParameters)
            throws Exception {

        // TODO store in session?
        IngridQuery ingridQuery = null;

        // put the ids of SessionParameters into IngridQuery
        RequestTransformer requestTransformer = new RequestTransformer();

        NamedNodeMap namedNodeMapTmp = null;

        ingridQuery = requestTransformer.transform(sessionParameters.getIdsList());

        sessionParameters.setResultType("results");

        // forward the request/query to the ibus
        IngridHits hits = doBusRequest(ingridQuery, sessionParameters);

        CSWResponseTransformer responseTransformer = new CSWResponseTransformer();
        return responseTransformer.transform(hits, ingridQuery, sessionParameters);
    }

    /**
     * sets the source type of the query TODO use metaclass:map / service
     * instead?
     * 
     * @param ingridQuery
     *            IngridQuery the query to change
     * @param sessionParameters
     *            SessionParameters
     * @return ingridQuery IngridQuery
     */

    public static final IngridQuery setSourceType(final IngridQuery ingridQuery, final SessionParameters sessionParameters) {
        boolean required = true;
        boolean prohibited = false;
        boolean sourceTypeIsDataset = false;
        boolean sourceTypeIsService = false;

        if (sessionParameters.isTypeNameIsDataset() || sessionParameters.isTypeNameIsDatasetcollection()) {
            sourceTypeIsDataset = true;
        }

        if (sessionParameters.isTypeNameIsApplication() || sessionParameters.isTypeNameIsService()) {
            sourceTypeIsService = true;
        }
        if (log.isDebugEnabled()) {
	        log.debug("isDataset=" + sourceTypeIsDataset);
	        log.debug("isService=" + sourceTypeIsService);
        }

        if (sourceTypeIsDataset && sourceTypeIsService) {
            ClauseQuery clauseQuery = new ClauseQuery(required, prohibited);
            required = false;
            clauseQuery.addField(new FieldQuery(required, prohibited, "metaclass", "map"));
            clauseQuery.addField(new FieldQuery(required, prohibited, "metaclass", "service"));
            ingridQuery.addClause(clauseQuery);
        	// nothing to be done, all meta data is queried
        } else if (sourceTypeIsDataset) {
            required = true;
            ingridQuery.addField(new FieldQuery(required, prohibited, "metaclass", "map"));
        } else if (sourceTypeIsService) {
            required = true;
            ingridQuery.addField(new FieldQuery(required, prohibited, "metaclass", "service"));
        }

        return ingridQuery;
    }

    /**
     * sets the query to csw datatype
     * 
     * @param ingridQuery
     *            IngridQuery
     * @return ingridQuery IngridQuery with datatype csw
     */
    public static final IngridQuery setDataTypeCSW(final IngridQuery ingridQuery) {
        boolean required = true;
        boolean prohibited = false;

//        ingridQuery.addField(new FieldQuery(required, prohibited, IngridQuery.DATA_TYPE, DataTypes.CSW));
//        ingridQuery.addField(new FieldQuery(required, prohibited, IngridQuery.DATA_TYPE, DataTypes.ECS));
        ingridQuery.addField(new FieldQuery(required, prohibited, IngridQuery.DATA_TYPE, DataTypes.DSCECS));
        ingridQuery.addField(new FieldQuery(required, prohibited, IngridQuery.RANKED, IngridQuery.ANY_RANKED));

        return ingridQuery;
    }

    /**
     * This method calls the search method of a running iBus to get the hits
     * 
     * @param ingridQuery
     *            IngridQuery the query for the iBus
     * @param requestedHits
     *            int the max number of records (maxRecords) to be returned from
     *            the iBus
     * @return hits IngridHits the hits with the results
     * @throws Exception
     *             e
     */
    private static IngridHits callBus(final IngridQuery ingridQuery, final int requestedHits, final int startPosition) throws Exception {
        String str_timeOut = cswConfig.getString(CSWInterfaceConfig.TIMEOUT);
        int timeOut = Integer.parseInt(str_timeOut);

        
        int pageNo = 0;
        int hitsPerPage = 0;
        // the iBus search interface takes only pageNo and pageSize as parameter 
        // to determine the start- and number of hits to return
        // if startPosition is no multiple of requestedHits we need special treatment.
        // Get paging based on the startPosition and requested hits.
        // see below for cutting the right results out from the result array.
        int[] paging = IBusHelper.getPaging(startPosition, requestedHits);
        pageNo = paging[0];
        hitsPerPage = paging[1];
		int searchResultStart = paging[1] == 1 ? paging[0] : Math.max((paging[0] - 1) * paging[1] + 1, 1);
		int searchResultEnd = searchResultStart + paging[1] - 1;
        boolean diffResultRange = searchResultStart != startPosition || searchResultEnd != (requestedHits + startPosition - 1);
        if (log.isDebugEnabled()) {
	        log.debug("translating start position and reuqested hits into page number and page size for ibus querying.");
	        log.debug("start,length : first,last (" + startPosition + "," + requestedHits + " : " + startPosition + "," + (requestedHits + startPosition - 1) + ")");
	        log.debug("pageNo,pageSize : first,last (" + pageNo + "," + hitsPerPage + " : " + searchResultStart + "," + searchResultEnd + ")");
        }
        
        IngridHits hits = null;
        
        try {
            IBus myBus = cswConfig.getIBus();
            if (log.isDebugEnabled()) {
            	log.debug("Fire query: " + ingridQuery.toString());
            }
            IBusHelper.injectCache(ingridQuery);
            hits = myBus.search(ingridQuery, hitsPerPage, pageNo, (pageNo-1) * hitsPerPage, timeOut);
            if (hits.length() < startPosition) {
            	// oh another funny behavior of the ibus: if more results than 
            	// available are requested the ibus returns STILL results
            	// make sure the CSW interface does NOT
            	hits = new IngridHits((int)hits.length(), new IngridHit[0]);
            } else if (diffResultRange) {
            	// see comment above
            	// here we cut the right hits out of the requested hits,
            	// so we match the CSW request
            	int length = Math.min(hits.getHits().length - (startPosition - searchResultStart), requestedHits);
            	IngridHit[] requestedIngridHitsArray = new IngridHit[length];
                if (log.isDebugEnabled()) {
                	log.debug("Cutting " + length + " results: starting with result no " + (startPosition - searchResultStart) + ".");
                }
            	System.arraycopy(hits.getHits(), (startPosition - searchResultStart), requestedIngridHitsArray, 0, length);
            	hits = new IngridHits((int)hits.length(), requestedIngridHitsArray);
            }
        } catch (Throwable t) {
            log.error("Error getting IBus: " + t.getMessage());
            throw new Exception("Timeout problem while connecting to subsequent servers.");
        }

        return hits;
    }

    public static IngridDocument[] createTestDocs() {
        IngridDocument ingridDoc = new IngridDocument("ID", "map");
        ingridDoc.put("T01_object.obj_id", "XYZ");
        ingridDoc.put("mdHrLv", "dataset");
        ingridDoc.put("mdLang", "deu");
        ingridDoc.put("mdChar", "ISO-????");
        ingridDoc.put("rpIndName", "Ralf Schaefer");
        ingridDoc.put("rpOrgName", "GIStec");
        ingridDoc.put("contactRole", "publisher");
        ingridDoc.put("voice", "+49-155-255");
        ingridDoc.put("facsimile", "+49-155-259");
        ingridDoc.put("country", "Germany");
        ingridDoc.put("eMailAdd", "ralf.schaefer@gistec-online.de");
        ingridDoc.put("linkage", "http://www.gistec-online.de");
        ingridDoc.put("mdDateSt", "2005-11-01");
        ingridDoc.put("resTitle", "Titel der Karte");
        ingridDoc.put("dataLang", "eng");
        ingridDoc.put("dataChar", "UTF-8"); // default encoding for test data
        ingridDoc.put("tpCat", "Topic");
        ingridDoc.put("exTypeCode", "true");
        ingridDoc.put("vertDatum", "Amsterdam");
        ingridDoc.put("vertMinVal", "81.3");
        ingridDoc.put("vertMaxVal", "950.9");
        ingridDoc.put("geographicDescriptionCode", "Hesse");
        ingridDoc.put("westBL", "-180.00");
        ingridDoc.put("eastBL", "180.00");
        ingridDoc.put("southBL", "-90.00");
        ingridDoc.put("northBL", "90.00");
        ingridDoc.put("vertUom", "meter");
        ingridDoc.put("tempExtent_begin", "2001-10-10T00:00:00-00:00");
        ingridDoc.put("tempExtent_end", "2003-08-14T00:00:00-00:00");
        // ingridDoc.put("abstract", "Beschreibung der Karte");
        // ingridDoc.put("modified", "2005-10-28");
        // int i = 0;
        //
        // while (i < 3){
        // ingridDoc.put("title" + i, "blubbblubb" + i);
        // ingridDoc.put("abstract" + i, "laaaber" + i);
        // ingridDoc.put("modified" + i, "2005-10-28");
        // i++;
        // }
        IngridDocument[] ingridDocuments = new IngridDocument[1];
        // //contacts
        // IngridDocument[] contacts = new IngridDocument[2];
        // contacts[0] = new IngridDocument("ID", "contact1");
        // contacts[1] = new IngridDocument("ID", "contact2");
        // contacts[0].put("country", "Germany");
        // contacts[0].put("individualName", "Ralf Schaefer");
        // contacts[0].put("voice", "+49-155-255");
        // contacts[1].put("country", "Germany");
        // contacts[1].put("individualName", "Nico S. Beck");
        // contacts[1].put("voice", "+49-155-257");
        //
        // //indentification info
        // IngridDocument[] identificationInfos = new IngridDocument[2];
        // identificationInfos[0] = new IngridDocument("ID", "identinfo1");
        // identificationInfos[1] = new IngridDocument("ID", "identinfo2");
        // identificationInfos[0].put("title", "Title number one");
        // identificationInfos[1].put("title", "Title number two");
        //
        // // topic category
        // IngridDocument[] topicCategories = new IngridDocument[2];
        // topicCategories[0] = new IngridDocument("ID", "topic1");
        // topicCategories[1] = new IngridDocument("ID", "topcic2");
        // topicCategories[0].put("topicCategory", "topicCategory number one");
        // topicCategories[1].put("topicCategory", "topicCategory number two");
        //
        // //put topicCategories into first identificationInfo
        // identificationInfos[0].put("topicCategories", topicCategories);
        // //put contacts into ingrid doc
        // ingridDoc.put("contacts", contacts);
        //
        // // put identificationInfos into ingrid doc
        // ingridDoc.put("identificationInfos", identificationInfos);

        int j = 0;
        while (j < ingridDocuments.length) {
            ingridDocuments[j] = ingridDoc;
            j++;
        }

        return ingridDocuments;
    }
}
