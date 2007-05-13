/*
 * Created on 07.10.2005
 *
 */
package de.ingrid.interfaces.csw.transform;

import java.io.Serializable;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.tools.XSLTools;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Column;
import de.ingrid.utils.dsc.Record;

/**
 * class for transforming
 * 
 * @author rschaefer
 * 
 */
public class ResponseTransformer {

    /**
     * the log object
     */
    private static Log log = LogFactory.getLog(ResponseTransformer.class);

    // Static object used to retrieve the config data
    private static CSWInterfaceConfig cswConfig = CSWInterfaceConfig.getInstance();

    /**
     * String to store the typename of the current metadataset, because the
     * information is in a subrecord.
     */
    private String currMDSetTypeName = "dataset";

    /**
     * Use the bus object to get the detailed records/subrecords and put them
     * into the internal xml encoding in the response document.
     * 
     * @param results
     *            IngridHit[] all the results in an IngridHit array
     * @param bus
     *            IBus the bus object
     * @param ingridQuery
     *            IngridQuery
     * @param sessionParameters
     *            SessionParameters
     * @return responseDoc Document the response document
     * @throws Exception
     *             e
     */
    public final Document getDetailedResults(final IngridHit[] results, final IBus bus,
            final SessionParameters sessionParameters) throws Exception {
        String elementSetName = sessionParameters.getElementSetName();
        int intStartPosition = sessionParameters.getStartPosition();

        if (results.length != 0 && intStartPosition > results.length) {
            throw new CSWInvalidParameterValueException(
                    "Value of Attribute 'startPosition' is greater than number of found result sets.", "startPosition");
        }
        int intMaxRecords = sessionParameters.getMaxRecords();
        intStartPosition = intStartPosition - 1;
        int intStopPosition = 0;

        if (results.length > intMaxRecords + intStartPosition) {
            intStopPosition = intMaxRecords + intStartPosition;
        } else {
            intStopPosition = results.length;
        }

        Document responseDoc = XMLTools.create();
        Element elemRoot = responseDoc.createElement("IngridRecords");
        responseDoc.appendChild(elemRoot);
        IngridHit result = null;
        // IngridHitDetail details = null;
        Element elemRecord = null;
        int docCount = 1;
        int numOfRecsToReturn = intStopPosition - intStartPosition;
        int numOfRecsReturned = 0;

        for (int start = intStartPosition; start < intStopPosition; start++) {
            log.debug("doc#: " + docCount++ + " of " + numOfRecsToReturn);
            result = results[start];

            PlugDescription plug = bus.getIPlug(result.getPlugId());
            int docId = result.getDocumentId();
            log.debug("docId: " + docId);
            String plugId = (String) plug.getPlugId();
            log.debug("plugId: " + plugId);
            Record record = null;

            // Step over an errorneous record request
            try {
                record = bus.getRecord(result);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                log.warn("Cannot get record: " + e.getMessage());
                record = null;
            }

            if (record == null) {
                log.warn("record is null, proceed with next one");
            } else {
                String mdPrefix = null;
                // choose md element name and namespaceURI
                if ("full".equals(elementSetName)) {
                    mdPrefix = "iso19115full";
                } else if ("summary".equals(elementSetName)) {
                    mdPrefix = "iso19115summary";
                } else if ("brief".equals(elementSetName)) {
                    mdPrefix = "iso19115brief";
                }

                elemRecord = getDetailedRecords(record, mdPrefix, responseDoc, sessionParameters);
                elemRecord.setAttribute("elementSetName", sessionParameters.getElementSetName());
                elemRecord.setAttribute("typeName", currMDSetTypeName);
                elemRoot.appendChild(elemRecord);
                numOfRecsReturned++;
            }
        }

        sessionParameters.setNumberOfRecordsReturned(numOfRecsReturned);

        XSLTools xslt = new XSLTools();

        if ("full".equals(sessionParameters.getElementSetName())) {
            responseDoc = xslt.transform(responseDoc, cswConfig.getUrlPath(CSWInterfaceConfig.FILE_OUTPUTXSL_FULL));
        } else {
            // TODO:
            // Use the commented line if the xslt file is corrected to really
            // support the element set "summary".
            // Until then, use the xslt file for element set "full"
            // responseDoc = xslt.transform(responseDoc,
            // cswConfig.getUrlPath(CSWInterfaceConfig.FILE_OUTPUTXSL));
            responseDoc = xslt.transform(responseDoc, cswConfig.getUrlPath(CSWInterfaceConfig.FILE_OUTPUTXSL_FULL));
        }

        return responseDoc;
    }

    /**
     * Get the details from one single record and recursively get the details
     * from the subrecords of this record. Put the details into the xml encoding
     * in an element. If the mdPrefix is not null then use it as prefix of the
     * metadata element, otherwise this record is just a subrecord and the name
     * of the element is a table name.
     * 
     * @param record
     *            Record
     * @param mdPrefix
     *            String
     * @param domDocument
     *            Document
     * @param sessionParameters
     *            SessionParameters
     * @return elemRecord Element the xml element with the details
     * @throws Exception
     *             e
     */
    public final Element getDetailedRecords(final Record record, final String mdPrefix, final Document domDocument,
            final SessionParameters sessionParameters) throws Exception {
        Element elemRecord = null;
        String recordTagName = null;
        Column column = null;
        String targetName = null;
        String value = null;
        Element elemCurr = null;
        Text elemTxtCurr = null;
        int numCols = record.numberOfColumns();

        // if this is a subrecord, use this as tagname;
        // else create a metadata element with namespace
        if (mdPrefix != null) {
            elemRecord = domDocument.createElementNS("http://schemas.opengis.net/" + mdPrefix, mdPrefix
                    + ":MD_Metadata");
        } else if (numCols > 0) { // determine name of subrecord from column
                                    // name
            column = record.getColumn(0);
            targetName = column.getTargetName();
            recordTagName = targetName.substring(0, targetName.indexOf("."));
            elemRecord = domDocument.createElement(recordTagName);
        }

        for (int i = 0; i < numCols; i++) {
            column = record.getColumn(i);
            targetName = column.getTargetName();
            value = record.getValueAsString(column);

            if ("T01_st_class.class_name".equalsIgnoreCase(targetName)) {
                // Service/Application: Dienst/Anwendung/Informationssystem
                if ("Dienst/Anwendung/Informationssystem".equalsIgnoreCase(value)) {
                    currMDSetTypeName = "service";
                } else { // Geo-Information/Karte is default.
                    currMDSetTypeName = "dataset";
                }
            }

            elemCurr = domDocument.createElement(targetName);
            elemTxtCurr = domDocument.createTextNode(value);
            // log.debug("content: " + targetName + ": " +
            // elemTxtCurr.getNodeValue());
            elemCurr.appendChild(elemTxtCurr);
            elemRecord.appendChild(elemCurr);
        }

        // do subrecords
        Record[] subRecords = record.getSubRecords();

        if (subRecords != null && subRecords.length > 0) {
            for (int j = 0; j < subRecords.length; j++) {
                elemRecord.appendChild(getDetailedRecords(subRecords[j], null, domDocument, sessionParameters));
            }
        }
        return elemRecord;
    }

    /**
     * 
     * @see de.ingrid.interfaces.csw.transform.CSWResponseTransformer#transform(de.ingrid.utils.IngridDocument[])
     */
    public final Document transform(final IngridDocument[] ingridDocuments, final SessionParameters sessionParameters)
            throws Exception {
        String elementSetName = sessionParameters.getElementSetName();
        int intStartPosition = sessionParameters.getStartPosition();
        int intMaxRecords = sessionParameters.getMaxRecords();
        Document responseDoc = XMLTools.create();
        IngridDocument currIngridDoc = null;
        Element elemRoot = responseDoc.createElement("IngridDocuments");
        responseDoc.appendChild(elemRoot);
        Element elemIngridDoc = null;
        int numOfIngridDocs = ingridDocuments.length;
        log.debug("number of metadatasets: " + numOfIngridDocs);

        if (numOfIngridDocs != 0 && intStartPosition > numOfIngridDocs) {
            throw new CSWInvalidParameterValueException("Value of Attribute 'startPosition' is greater "
                    + "than number of found result sets.", "startPosition");
        }

        intStartPosition = intStartPosition - 1;
        int intStopPosition = 0;

        if (numOfIngridDocs > intMaxRecords + intStartPosition) {
            intStopPosition = intMaxRecords + intStartPosition;
        } else {
            intStopPosition = numOfIngridDocs;
        }

        int numOfRecsReturned = 0;

        for (int start = intStartPosition; start < intStopPosition; start++) {
            currIngridDoc = ingridDocuments[start];
            elemIngridDoc = transform(currIngridDoc, elementSetName, responseDoc);
            elemRoot.appendChild(elemIngridDoc);
            numOfRecsReturned++;
        }

        // set some parameters like numberOfRecordsMatched or
        // numberOfRecordsReturned
        sessionParameters.setNumberOfRecordsReturned(numOfRecsReturned);

        // do XSLT transform for brief, summary or full elementSetName
        XSLTools xslt = new XSLTools();

        // get xsl filename/path
        responseDoc = xslt.transform(responseDoc, cswConfig.getUrlPath(CSWInterfaceConfig.FILE_OUTPUTXSL));

        return responseDoc;
    }

    /**
     * FIXME remove
     * 
     * @see de.ingrid.interfaces.csw.transform.CSWResponseTransformer#transform(de.ingrid.utils.IngridDocument,
     *      org.w3c.dom.Document)
     */
    public final Element transform(final IngridDocument ingridDocument, final String elementSetName,
            final Document domDocument) throws Exception {
        Element elemCurr = null;
        Text elemTxtCurr = null;
        Serializable key = null;
        Serializable value = null;
        Element elemIngridDoc = domDocument.createElement("IngridDocument");
        // TODO elementSetName
        elemIngridDoc.setAttribute("elementSetName", elementSetName);
        Set keySet = ingridDocument.keySet();
        log.debug("ingridDocument number of keys: " + keySet.size());
        Iterator iterator = keySet.iterator();

        while (iterator.hasNext()) {
            key = (Serializable) iterator.next();
            value = (Serializable) ingridDocument.get(key);
            log.debug("ingridDocument key: " + key + "  value: " + value);

            if (key instanceof Integer) {
                if (key.equals(new Integer(0))) {
                    elemCurr = domDocument.createElement("DocumentID");
                } else if (key.equals(new Integer(1))) {
                    elemCurr = domDocument.createElement("DocumentContent");
                }

                // encode value (e.g. UTF-8)
                String txtEncoding = cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING);
                if (txtEncoding.equals("NONE")) {
                    elemTxtCurr = domDocument.createTextNode(value.toString());
                } else {
                    elemTxtCurr = domDocument.createTextNode(URLEncoder.encode(value.toString(), cswConfig
                            .getString(CSWInterfaceConfig.RESPONSE_ENCODING)));
                }
                elemCurr.appendChild(elemTxtCurr);
            } else if (key instanceof String) {
                elemCurr = domDocument.createElement(key.toString());

                if (value instanceof IngridDocument[]) {
                    IngridDocument[] ingridDocuments = ((IngridDocument[]) value);
                    int len = ingridDocuments.length;

                    for (int i = 0; i < len; i++) {
                        // recursion: call transform ...
                        elemCurr.appendChild(transform(ingridDocuments[i], elementSetName, domDocument));
                    }
                } else {
                    // encode value (e.g. UTF-8)
                    String txtEncoding = cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING);
                    if (txtEncoding.equals("NONE")) {
                        elemTxtCurr = domDocument.createTextNode(value.toString());
                    } else {
                        elemTxtCurr = domDocument.createTextNode(URLEncoder.encode(value.toString(), cswConfig
                                .getString(CSWInterfaceConfig.RESPONSE_ENCODING)));
                    }
                    elemCurr.appendChild(elemTxtCurr);
                }
            }

            elemIngridDoc.appendChild(elemCurr);
        } // end while

        return elemIngridDoc;
    }

    /**
     * FIXME remove
     * 
     * @see de.ingrid.interfaces.csw.transform.CSWResponseTransformer#transform(org.w3c.dom.Document,
     *      de.ingrid.interfaces.csw.analyse.SessionParameters)
     */
    public final void transform(final Document responseDoc, final SessionParameters sessionParameters) throws Exception {
        // TODO remove element RequestId?
        NodeList nl = responseDoc.getElementsByTagName("SearchStatus");
        Element elemSearchStatus = (Element) nl.item(0);
        NamedNodeMap namedNodeMap = elemSearchStatus.getAttributes();
        Node ndTemp = namedNodeMap.getNamedItem("status");
        ndTemp.setNodeValue("complete");
        ndTemp = namedNodeMap.getNamedItem("timestamp");
        GregorianCalendar calendar = new GregorianCalendar();
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String timestamp = dateformat.format(calendar.getTime());
        ndTemp.setNodeValue(timestamp);
        nl = responseDoc.getElementsByTagName("SearchResults");
        Element elemSearchResults = (Element) nl.item(0);
        namedNodeMap = elemSearchResults.getAttributes();
        namedNodeMap.removeNamedItem("resultSetId");
        ndTemp = namedNodeMap.getNamedItem("elementSet");
        ndTemp.setNodeValue(sessionParameters.getElementSetName());

        if (sessionParameters.getResultType().equalsIgnoreCase("HITS")) {
            namedNodeMap.removeNamedItem("elementSet");
        }

        ndTemp = namedNodeMap.getNamedItem("numberOfRecordsMatched");
        ndTemp.setNodeValue(Integer.toString(sessionParameters.getNumberOfRecordsMatched()));
        ndTemp = namedNodeMap.getNamedItem("numberOfRecordsReturned");
        ndTemp.setNodeValue(Integer.toString(sessionParameters.getNumberOfRecordsReturned()));
    }
}
