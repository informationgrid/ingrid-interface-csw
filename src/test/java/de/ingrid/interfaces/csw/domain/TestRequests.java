/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import java.io.File;
import java.util.Scanner;

public final class TestRequests {

	public static final String GETCAP_SOAP = "src/test/resources/requests/get_capabilities_soap.xml";
	public static final String GETCAP_POST = "src/test/resources/requests/get_capabilities_post.xml";

	public static final String GETCAPINVALID1 = "src/test/resources/requests/get_capabilities_invalid_1_soap.xml";
	public static final String GETCAPINVALID2 = "src/test/resources/requests/get_capabilities_invalid_2_soap.xml";
	public static final String GETCAPINVALID3 = "src/test/resources/requests/get_capabilities_invalid_3_soap.xml";

	public static final String DESCREC_SOAP = "src/test/resources/requests/describe_record_soap.xml";
	public static final String DESCREC_POST = "src/test/resources/requests/describe_record_post.xml";

	public static final String GETRECORDBYID_SOAP = "src/test/resources/requests/get_record_by_id_soap.xml";
	public static final String GETRECORDBYID_MULTIPLE_SOAP = "src/test/resources/requests/get_record_by_id_multiple_soap.xml";

	public static final String GETRECBYIDINVALID_SOAP = "src/test/resources/requests/get_record_by_id_invalid_soap.xml";

	public static final String GETRECORDS_1_BRIEF_SOAP = "src/test/resources/requests/get_records_1_brief_soap.xml";
	public static final String GETRECORDS_1_SUMMARY_SOAP = "src/test/resources/requests/get_records_1_summary_soap.xml";
	public static final String GETRECORDS_1_FULL_SOAP = "src/test/resources/requests/get_records_1_full_soap.xml";

	public static final String GETRECORDS_2_SOAP = "src/test/resources/requests/get_records_2_soap.xml";
	public static final String GETRECORDS_3_SOAP = "src/test/resources/requests/get_records_3_soap.xml";
	public static final String GETRECORDS_4_SOAP = "src/test/resources/requests/get_records_4_soap.xml";
	public static final String GETRECORDS_5_SOAP = "src/test/resources/requests/get_records_5_soap.xml";
	public static final String GETRECORDS_6_SOAP = "src/test/resources/requests/get_records_6_soap.xml";
	public static final String GETRECORDS_7_SOAP = "src/test/resources/requests/get_records_7_soap.xml";
    public static final String GETRECORDS_8_SOAP = "src/test/resources/requests/get_records_8_soap.xml";
    public static final String GETRECORDS_9_SOAP = "src/test/resources/requests/get_records_9_soap.xml";
    public static final String GETRECORDS_10_SOAP = "src/test/resources/requests/get_records_10_soap.xml";

	public static final String GETRECORDS_HITS_SOAP = "get_records_hits_soap.xml";

	public static final String GETRECORDSINVALID_1_SOAP = "src/test/resources/requests/get_records_invalid_1_soap.xml";
	public static final String GETRECORDSINVALID_2_SOAP = "src/test/resources/requests/get_records_invalid_2_soap.xml";
	public static final String GETRECORDSINVALID_3_SOAP = "src/test/resources/requests/get_records_invalid_3_soap.xml";
	public static final String GETRECORDSINVALID_4_SOAP = "src/test/resources/requests/get_records_invalid_4_soap.xml";
	public static final String GETRECORDSINVALID_5_SOAP = "src/test/resources/requests/get_records_invalid_5_soap.xml";
	public static final String GETRECORDSINVALID_6_SOAP = "src/test/resources/requests/get_records_invalid_6_soap.xml";
	public static final String GETRECORDSINVALID_7_SOAP = "src/test/resources/requests/get_records_invalid_7_soap.xml";

	/**
	 * Get the content of a request defined in the given file.
	 * @param filePath
	 * @return String
	 * @throws Exception
	 */
	public static String getRequest(String filePath) throws Exception {
		File requestFile = new File(filePath);
		return new Scanner(requestFile).useDelimiter("\\A").next();
	}
}