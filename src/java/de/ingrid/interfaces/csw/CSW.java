/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;

import org.apache.axis.Message;
import org.apache.axis.message.SOAPBody;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;

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
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.transform.RequestTransformer;
import de.ingrid.interfaces.csw.transform.response.CSWResponseTransformer;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridDocument;
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

    /**
     * This method performs a SOAP request
     * 
     * @param soapRequestMessage
     *            Message
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    protected final Message doRequest(final Message soapRequestMessage) throws Exception {
        Message soapResponseMessage = null;
        SessionParameters sessionParameters = new SessionParameters();
        CommonAnalyser commonAnalyser = new CommonAnalyser(sessionParameters);
        SOAPBody body = (SOAPBody) soapRequestMessage.getSOAPBody();
        SOAPBodyElement be = (SOAPBodyElement) body.getFirstChild();

        // String nameSpacePrefix = be.getPrefix();
        // System.out.println("nameSpacePrefix: " + nameSpacePrefix);

        // test if namespace is correct
        String nameSpaceURI = be.getNamespaceURI();
        // System.out.println("doRequest nameSpaceURI: " + nameSpaceURI);

        // String opName = be.getNodeName();
        String opName = be.getLocalName();

        if (nameSpaceURI != null && !"".equals(nameSpaceURI) && !Namespaces.CSW.equals(nameSpaceURI)) {
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
                soapResponseMessage = doGetCapabilitiesRequest();
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
                soapResponseMessage = doGetRecordsRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsGetRecById()) {
            GetRecByIdAnalyser getRecByIdAnalyser = new GetRecByIdAnalyser(sessionParameters);

            if (getRecByIdAnalyser.analyse(be)) {
                soapResponseMessage = doGetRecordByIdRequest(be, sessionParameters);
            }
        } else if (sessionParameters.isOperationIsDescRec()) {
            DescRecAnalyser descRecAnalyser = new DescRecAnalyser();

            if (descRecAnalyser.analyse(be)) {
                soapResponseMessage = doDescribeRecordRequest();
            }
        }
        return soapResponseMessage;
    }

    /**
     * performs a describe record request
     * 
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Message doDescribeRecordRequest() throws Exception {
        Message soapResponseMessage = null;
        URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_DESCRIBERECORD));
        Reader reader = new InputStreamReader(url.openStream());
        Document doc = XMLTools.parse(reader);
        soapResponseMessage = AxisTools.createSOAPMessage(doc);
        return soapResponseMessage;
    }

    /**
     * performs a get capabilities request
     * 
     * @return soapResponseMessage Message
     * @throws Exception
     *             e
     */
    private Message doGetCapabilitiesRequest() throws Exception {
        Message soapResponseMessage = null;
        URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_GETCAPABILITIES));
        Reader reader = new InputStreamReader(url.openStream());
        Document doc = XMLTools.parse(reader);
        soapResponseMessage = AxisTools.createSOAPMessage(doc);
        return soapResponseMessage;
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
    private Message doGetRecordsRequest(final SOAPBodyElement be, final SessionParameters sessionParameters)
            throws Exception {
        Message soapResponseMessage = null;

        // transform the OGC filter (request) into IngridQuery; including
        // SessionParameters
        SOAPElement soapElementFilter = (SOAPElement) be.getElementsByTagName("Filter").item(0);
        RequestTransformer requestTransformer = new RequestTransformer();
        IngridQuery ingridQuery = requestTransformer.transform(soapElementFilter);

        IngridHits hits = doBusRequest(ingridQuery, sessionParameters);

        CSWResponseTransformer responseTransformer = new CSWResponseTransformer();
        Document responseDoc = responseTransformer.transform(hits, ingridQuery, sessionParameters);
        soapResponseMessage = AxisTools.createSOAPMessage(responseDoc);
        return soapResponseMessage;
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
    private final IngridHits doBusRequest(IngridQuery ingridQuery, final SessionParameters sessionParameters)
            throws Exception {

        int requestedHits = sessionParameters.getMaxRecords();
        int startPosition = sessionParameters.getStartPosition();

        ingridQuery = setDataTypeCSW(ingridQuery);
        ingridQuery = setSourceType(ingridQuery, sessionParameters);

        IngridHits hits = callBus(ingridQuery, requestedHits, startPosition);
        long totalHits = hits.length();
        log.info("Hits for CSW: " + totalHits);

        sessionParameters.setNumberOfRecordsMatched((int) totalHits);

        return hits;
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
    private Message doGetRecordByIdRequest(final SOAPBodyElement be, final SessionParameters sessionParameters)
            throws Exception {

        Message soapResponseMessage = null;

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
        Document responseDoc = responseTransformer.transform(hits, ingridQuery, sessionParameters);

        soapResponseMessage = AxisTools.createSOAPMessage(responseDoc);

        return soapResponseMessage;
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

    public final IngridQuery setSourceType(final IngridQuery ingridQuery, final SessionParameters sessionParameters) {
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
        log.debug("isDataset=" + sourceTypeIsDataset);
        log.debug("isService=" + sourceTypeIsService);

        if (sourceTypeIsDataset && sourceTypeIsService) {
            ClauseQuery clauseQuery = new ClauseQuery(required, prohibited);
            required = false;
            clauseQuery.addField(new FieldQuery(required, prohibited, "metaclass", "map"));
            clauseQuery.addField(new FieldQuery(required, prohibited, "metaclass", "service"));
            ingridQuery.addClause(clauseQuery);
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
    public final IngridQuery setDataTypeCSW(final IngridQuery ingridQuery) {
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
    private IngridHits callBus(final IngridQuery ingridQuery, final int requestedHits, final int startPosition) throws Exception {
        String str_timeOut = cswConfig.getString(CSWInterfaceConfig.TIMEOUT);
        int timeOut = Integer.parseInt(str_timeOut);

        // set pageNo to the first page (zero)
        int pageNo = (int) (startPosition / requestedHits) + 1;
        
        // set to requestedHits
        int hitsPerPage = requestedHits;
        IngridHits hits = null;

        try {
            IBus myBus = cswConfig.getIBus();
            hits = myBus.search(ingridQuery, hitsPerPage, pageNo, requestedHits, timeOut);
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
        ingridDoc.put("rpIndName", "Ralf Schäfer");
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
        // contacts[0].put("individualName", "Ralf Schäfer");
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
