/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

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

	public static final String GETRECORDBYID_SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
			+ " <soapenv:Body>\n"
			+ "<GetRecordById service=\"CSW\" version=\"2.0.0\">\n"
			+ "<Id>04068592-709f-3c7a-85de-f2d68e585fca</Id>\n" + "<ElementSetName>full</ElementSetName>\n" + "</GetRecordById>\n"
			+ "</soapenv:Body>\n" + "</soapenv:Envelope>";

	public static final String GETRECORDBYID_MULTIPLE_SOAP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<soapenv:Envelope xmlns:soapenv=\"http://www.w3.org/2003/05/soap-envelope\"\n"
			+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n"
			+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
			+ " <soapenv:Body>\n"
			+ "<GetRecordById service=\"CSW\" version=\"2.0.0\">\n"
			+ "<Id>04068592-709f-3c7a-85de-f2d68e585fca, 16f6d74b-f5b7-3efb-ae3c-0128549b8ac6</Id>\n" + "<ElementSetName>full</ElementSetName>\n" + "</GetRecordById>\n"
			+ "</soapenv:Body>\n" + "</soapenv:Envelope>";

}
