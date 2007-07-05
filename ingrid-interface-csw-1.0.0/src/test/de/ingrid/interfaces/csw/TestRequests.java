/*
 * Created on 03.10.2005
 *
 */
package de.ingrid.interfaces.csw;

import java.util.Properties;

/**
 * @author rschaefer
 * 
 */
public final class TestRequests {
    

    private static final String GETREC_WORD = "GetRecords";

    // SOAP 1.2 requests:
    

    public static final String GETCAP1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<soapenv:Body>\n"
            + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>2.0.0</Version>\n"
            + "</AcceptVersions>\n" + "</GetCapabilities>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETCAP2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + " <soapenv:Body>\n"
            + "<GetCapabilities service=\"CSW\" />\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETCAPINVALID1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<soapenv:Body>\n"
            + "<GetCap service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>0.0.6</Version>\n"
            + "<Version>1.0.0</Version>\n" + "</AcceptVersions>\n" + "</GetCap>\n" + " </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    public static final String GETCAPINVALID2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + " <soapenv:Body>\n"
            + "<GetCapabilities>\n" + "<AcceptVersions>\n" + "<Version>0.0.6</Version>\n"
            + "<Version>1.0.0</Version>\n" + "</AcceptVersions>\n" + "</GetCapabilities>\n" + " </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    public static final String GETCAPINVALID3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" + "<GetCapabilities service=\"WMS\" >\n" + "<AcceptVersions>\n"
            + "<Version>0.0.6</Version>\n" + "<Version>1.0.0</Version>\n" + "</AcceptVersions>\n"
            + "</GetCapabilities>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETCAPINVALID4 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n"
            + "<Version>0.0.6</Version>\n" + "<Version>1.0.0</Version>\n" + "</AcceptVersions>\n"
            + "</GetCapabilities>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: "title:Wasser"
     * 
     * - sortorder will be ignored
     */
    public static final String GETREC1 =

        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
                + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
                + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                " <soapenv:Body>\n" +
                "<" + GETREC_WORD + " maxRecords=\"4\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
                + "      requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
                + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
                + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
                + "<Constraint version=\"1.0.0\">\n" 
                + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" 
                + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Wasser</Literal>\n" + "</PropertyIsEqualTo>\n"
                + "</Filter>\n" 
                + "</Constraint>\n" 
                + "<SortBy>\n" 
                + "<SortProperty>\n" + "<PropertyName>title</PropertyName>\n" + "<SortOrder>ASC</SortOrder>\n" + "</SortProperty>\n" + "</SortBy>" 
                + "</Query>\n" + "</" + GETREC_WORD + ">\n" 
                + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: "title:Wasser"
     * 
     * - sortorder will be ignored
     */
    public static final String GETREC1_brief =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <soapenv:Body>\n" +
            "<" + GETREC_WORD + " maxRecords=\"4\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:service,csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + "<Constraint version=\"1.0.0\">\n" 
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" 
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Wasser</Literal>\n" + "</PropertyIsEqualTo>\n"
            + "</Filter>\n" 
            + "</Constraint>\n" 
            + "<SortBy>\n" 
            + "<SortProperty>\n" + "<PropertyName>title</PropertyName>\n" + "<SortOrder>ASC</SortOrder>\n" + "</SortProperty>\n" + "</SortBy>" 
            + "</Query>\n" + "</" + GETREC_WORD + ">\n" 
            + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: "title:Wasser"
     * 
     * - sortorder will be ignored
     */
    public static final String GETREC1_summary =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <soapenv:Body>\n" +
            "<" + GETREC_WORD + " maxRecords=\"4\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "      requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">summary</ElementSetName>\n"
            + "<Constraint version=\"1.0.0\">\n" 
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" 
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Wasser</Literal>\n" + "</PropertyIsEqualTo>\n"
            + "</Filter>\n" 
            + "</Constraint>\n" 
            + "<SortBy>\n" 
            + "<SortProperty>\n" + "<PropertyName>title</PropertyName>\n" + "<SortOrder>ASC</SortOrder>\n" + "</SortProperty>\n" + "</SortBy>" 
            + "</Query>\n" + "</" + GETREC_WORD + ">\n" 
            + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: "title:Wasser"
     * 
     * - sortorder will be ignored
     */
    public static final String GETREC1_full =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
            " <soapenv:Body>\n" +
            "<" + GETREC_WORD + " maxRecords=\"4\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "      requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">full</ElementSetName>\n"
            + "<Constraint version=\"1.0.0\">\n" 
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" 
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Wasser</Literal>\n" + "</PropertyIsEqualTo>\n"
            + "</Filter>\n" 
            + "</Constraint>\n" 
            + "<SortBy>\n" 
            + "<SortProperty>\n" + "<PropertyName>title</PropertyName>\n" + "<SortOrder>ASC</SortOrder>\n" + "</SortProperty>\n" + "</SortBy>" 
            + "</Query>\n" + "</" + GETREC_WORD + ">\n" 
            + " </soapenv:Body>\n" + "</soapenv:Envelope>";
    
    
    /**
     * Query: fische NOT (froesche OR lurche)
     */
    public static final String GETREC2 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +

            "<And>\n" +

            "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>fische</Literal>\n"
            + "</PropertyIsEqualTo>\n" +

            "<Not>\n" +

            "<Or>\n" +

            "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>froesche</Literal>\n"
            + "</PropertyIsEqualTo>\n" +

            "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>lurche</Literal>\n"
            + "</PropertyIsEqualTo>\n" +

            "</Or>\n" +

            "</Not>\n" +

            "</And>\n" +

            "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: fische ort:halle NOT (saale OR Hufeisensee)
     */
    public static final String GETREC3 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<And>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>fische</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "<PropertyIsEqualTo>\n"
            + "<PropertyName>GeographicDescriptionCode</PropertyName>\n" + "<Literal>halle</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "<Not>\n" + "<Or>\n" + "<PropertyIsEqualTo>\n"
            + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>saale</Literal>\n" + "</PropertyIsEqualTo>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>hufeisensee</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Not>\n" + "</And>\n" + "</Filter>\n" + "</Constraint>\n"
            + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: Bounding Box
     * 
     */
    public static final String GETREC4 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
            // "<Or>\n" +
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>saale</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +

            "<BBOX xmlns:gml=\"http://www.opengis.net/gml\">\n" + "<PropertyName>Geometry</PropertyName>\n"
            + "<gml:Box srsName=\"http://www.opengis.net/gml/srs/epsg.xml#4326\">\n" +

            "<gml:coordinates>13.0,31.5 35.5,42.8</gml:coordinates>\n" +

            "</gml:Box>\n" + "</BBOX>\n" +

            // "</Or>\n" +

            "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query: (denominator > 50 AND denominator < 100) OR (denominator >= 10 AND
     * denominator <= 15)
     * 
     */
    public static final String GETREC5 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n" +

            "<And>\n" +

            "<PropertyIsGreaterThan>\n" + "<PropertyName>Denominator</PropertyName>\n" + "<Literal>50</Literal>\n"
            + "</PropertyIsGreaterThan>\n" +

            "<PropertyIsLessThan>\n" + "<PropertyName>Denominator</PropertyName>\n" + "<Literal>100</Literal>\n"
            + "</PropertyIsLessThan>\n" +

            "</And>\n" + "<And>\n" +

            "<PropertyIsGreaterThanOrEqualTo>\n" + "<PropertyName>Denominator</PropertyName>\n"
            + "<Literal>10</Literal>\n" + "</PropertyIsGreaterThanOrEqualTo>\n" +

            "<PropertyIsLessThanOrEqualTo>\n" + "<PropertyName>Denominator</PropertyName>\n"
            + "<Literal>15</Literal>\n" + "</PropertyIsLessThanOrEqualTo>\n" +

            "</And>\n" +

            "</Or>\n" + "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * Query:
     * 
     * NOT (saale OR Hufeisensee)
     */
    public static final String GETREC6 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
            // "<Or>\n" +
            // "<And>\n" +
            //				 
            //
            //				
            //				 
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>blabla</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +
            //				
            //				
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>rarbrarb</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +

            // "</And>\n" +

            "<And>\n" +

            "<PropertyIsEqualTo>\n" + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>kraken</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "<PropertyIsEqualTo>\n"
            + "<PropertyName>GeographicDescriptionCode</PropertyName>\n" + "<Literal>darmstadt</Literal>\n"
            + "</PropertyIsEqualTo>\n" +

            "<Not>\n" +
            // "<Or>\n" +
            "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>AnyText</PropertyName>\n" + "<Literal>saale</Literal>\n" + "</PropertyIsLike>\n" +
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>hufeisensee</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +
            // "</Or>\n" +
            "</Not>\n" +

            // "<Not>\n" +
            //					
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>hufeisensee</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +
            //					  
            // "</Not>\n" +

            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>AnyText</PropertyName>\n" +
            // "<Literal>fische</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +
            // "<PropertyIsEqualTo>\n" +
            // "<PropertyName>GeographicDescriptionCode</PropertyName>\n" +
            // "<Literal>halle</Literal>\n" +
            // "</PropertyIsEqualTo>\n" +

            "</And>\n" +
            // "</Or>\n" +
            "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    /**
     * request GETHITS with resultType=hits for just getting the number of
     * matched records
     */
    public static final String GETHITS =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " outputFormat=\"text/xml\"\n"
            + "		 requestId=\"csw:1\" resultType=\"hits\" service=\"CSW\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +

            "<Query typeNames=\"csw:dataset\">\n" + " <Constraint version=\"1.0.0\">\n"
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
            // "<Or>\n" +
            // "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            // +
            // "<PropertyName>Modified</PropertyName>\n" +
            // "<Literal>2005-11-01</Literal>\n" +
            // "</PropertyIsLike>\n" +
            "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Wasser</Literal>\n"
            + "</PropertyIsEqualTo>\n" +
            // "</Or>\n" +
            "</Filter>\n" + "</Constraint>\n" + "</Query>\n" +

            "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID1 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<GetRecord maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n"
            + "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" + "<Literal>arte*</Literal>\n" + "</PropertyIsLike>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Test</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Filter>\n" + "</Constraint>\n" + "</Query>\n"
            + "</GetRecord>\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID2 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"WMS\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n"
            + "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" + "<Literal>arte*</Literal>\n" + "</PropertyIsLike>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Test</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</"
            + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID3 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"1.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n"
            + "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" + "<Literal>arte*</Literal>\n" + "</PropertyIsLike>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Test</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</"
            + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID4 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
            // "<Query typeNames=\"csw:dataset\">\n" +
            "<ElementSetName typeNames=\"\">brief</ElementSetName>\n" + " <Constraint version=\"1.0.0\">\n"
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n"
            + "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" + "<Literal>arte*</Literal>\n" + "</PropertyIsLike>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Test</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Filter>\n" + "</Constraint>\n" +
            // "</Query>\n" +
            "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID5 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"10\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"1\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" + "<Query>\n"
            + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n" + " <Constraint version=\"1.0.0\">\n"
            + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" + "<Or>\n"
            + "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" + "<Literal>arte*</Literal>\n" + "</PropertyIsLike>\n"
            + "<PropertyIsEqualTo>\n" + "<PropertyName>Title</PropertyName>\n" + "<Literal>Test</Literal>\n"
            + "</PropertyIsEqualTo>\n" + "</Or>\n" + "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</"
            + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID6 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"20\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"2\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECINVALID7 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<" + GETREC_WORD + " maxRecords=\"20\" outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "		 requestId=\"csw:1\" resultType=\"results\" service=\"CSW\" startPosition=\"2\"\n"
            + "        version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Query typeNames=\"csw:dataset\">\n" + "<ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + " <Constraint version=\"1.0.0\">\n" + "<Filter xmlns=\"http://www.opengis.net/ogc\">\n" +
            // "<Or>\n" +
            "<PropertyIsLike escape=\"!\" singleChar=\"#\" wildCard=\"*\">\n"
            + "<PropertyName>Abstract</PropertyName>\n" +
            // "<Literal>arte*</Literal>\n" +
            "</PropertyIsLike>\n" +
            // "</Or>\n" +
            "</Filter>\n" + "</Constraint>\n" + "</Query>\n" + "</" + GETREC_WORD + ">\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECBYID1 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n"
            + "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Id>F3659A31-26C3-4017-BF82-6DCB483460D6</Id>\n" + "<ElementSetName>full</ElementSetName>\n" + "</GetRecordById>\n"
            + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECBYID2 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n"
            + "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n"
            + "<Id>2, 6, 8</Id>\n" +
            // "<ElementSetName>summary</ElementSetName>\n" +
            "</GetRecordById>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String GETRECBYIDINVALID1 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n"
            + "<GetRecordById service=\"CSW\" version=\"2.0.0\" xmlns=\"http://www.opengis.net/cat/csw\">\n" +
            // "<Id>2,6,8</Id>\n" +
            "<ElementSetName>summary</ElementSetName>\n" + "</GetRecordById>\n" + " </soapenv:Body>\n"
            + "</soapenv:Envelope>";

    public static final String DESCREC1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<DescribeRecord service=\"CSW\" version=\"2.0.0\" outputFormat=\"text/xml\" schemaLanguage=\"XMLSCHEMA\"/>\n"
            +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final String DESCREC2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
            + "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<DescribeRecord service=\"CSW\" version=\"2.0.0\" />\n" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    // Keyword value pair (KVP) requests:

    public static final String UDK =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
            + "                    xmlns:udk-query=\"http://www.umweltdatenkatalog.de/udk/query\">\n"
            + "<soapenv:Body>" + "<udk-query:udk>" + "<udk-query:data-source-query>" + "<udk-query:general>"
            + "<udk-query:search-term>wasser</udk-query:search-term>" + "</udk-query:general>"
            + "</udk-query:data-source-query>" + "</udk-query:udk>" + "</soapenv:Body>" + "</soapenv:Envelope>";

    public static final String WCAS006 =

    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +

    // ns just for SOAP 1.1
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
            // "<soapenv:Envelope
            // xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n" +
            "                   xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
            + "                   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +

            " <soapenv:Body>\n" +

            "<GetRecord outputFormat=\"XML\" outputRecType=\"19115\" version=\"0.0.6\" service=\"StSC\" "
            + " maxRecords=\"all\" startPosition=\"1\" >"
            + "<Query typeName=\"Product\"><PropertySet setName=\"Brief\" />" + "<Filter>"
            + "<PropertyIsLike wildCard=\"*\" singleChar=\"?\" escapeChar=\"/\" escape=\"/\">" + "<PropertyName>"
            + "MD_Metadata/identificationInfo/MD_DataIdentification/descriptiveKeywords/MD_Keywords/keyword"
            + "</PropertyName>" + "<Literal>Wasser*</Literal></PropertyIsLike>" + "</Filter>" + "</Query>"
            + "</GetRecord>" +

            " </soapenv:Body>\n" + "</soapenv:Envelope>";

    public static final Properties KVPGETCAP1 = new Properties();

    public static final Properties KVPGETCAP2 = new Properties();

    public static final Properties KVPGETCAP3 = new Properties();

    public static final Properties KVPGETCAP4 = new Properties();

    public static final Properties KVPGETCAPINVALID5 = new Properties();

    // Although object are declared final, they are filled by this block
    static {
        KVPGETCAP1.setProperty("REQUEST", "GetCapabilities");
        KVPGETCAP1.setProperty("SERVICE", "CSW");

        KVPGETCAP2.setProperty("REQUEST", "GetCapabilities");
        KVPGETCAP2.setProperty("SERVICE", "CSW");
        KVPGETCAP2.setProperty("ACCEPTVERSIONS", "2.0.0");

        KVPGETCAP3.setProperty("REQUEST", "GetCapabilities");
        KVPGETCAP3.setProperty("SERVICE", "CSW");
        KVPGETCAP3.setProperty("ACCEPTVERSIONS", "1.0.0,2.0.0");

        KVPGETCAP4.setProperty("REQUEST", "GetCapabilities");
        KVPGETCAP4.setProperty("SERVICE", "CSW");
        KVPGETCAP4.setProperty("ACCEPTVERSIONS", "2.0.0,1.0.0");

        KVPGETCAPINVALID5.setProperty("REQUEST", "GetCapabilities");
        KVPGETCAPINVALID5.setProperty("SERVICE", "CSW");
        KVPGETCAPINVALID5.setProperty("ACCEPTVERSIONS", "1.0.0");
    }
}
