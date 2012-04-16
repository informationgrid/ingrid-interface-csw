/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import java.io.File;
import java.util.Scanner;

public final class TestRequests {

	public static final String GETCAP_SOAP = "src/test/resources/requests/get_capabilities_soap.xml";
	public static final String GETCAP_POST = "src/test/resources/requests/get_capabilities_post.xml";

	public static final String DESCREC_SOAP = "src/test/resources/requests/describe_record_soap.xml";
	public static final String DESCREC_POST = "src/test/resources/requests/describe_record_post.xml";

	public static final String GETRECORDBYID_SOAP = "src/test/resources/requests/get_record_by_id_soap.xml";
	public static final String GETRECORDBYID_MULTIPLE_SOAP = "src/test/resources/requests/get_record_by_id_multiple_soap.xml";

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
