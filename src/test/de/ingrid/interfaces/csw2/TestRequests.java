/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

public final class TestRequests {

	public static final String GETCAP_SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<soapenv:Body>\n"
        + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>2.0.0</Version>\n" +
        		"<Version>2.0.2</Version>\n"
        + "</AcceptVersions>\n" + "</GetCapabilities>\n" + " </soapenv:Body>\n" + "</soapenv:Envelope>";

	public static final String GETCAP_POST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<GetCapabilities service=\"CSW\" >\n" + "<AcceptVersions>\n" + "<Version>2.0.0</Version>\n" +
        		"<Version>2.0.2</Version>\n"
        + "</AcceptVersions>\n" + "</GetCapabilities>";

	public static final String DESCREC_SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
        + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
        + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" + "<soapenv:Body>\n"
        + "<DescribeRecord service=\"CSW\" version=\"2.0.2\">\n" + "</DescribeRecord>\n" +
        " </soapenv:Body>\n" + "</soapenv:Envelope>";

	public static final String DESCREC_POST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        + "<DescribeRecord service=\"CSW\" version=\"2.0.2\">\n" + "</DescribeRecord>";

}
