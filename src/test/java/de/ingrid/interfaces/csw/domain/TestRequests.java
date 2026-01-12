/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
    public static final String GETRECORDBYID_SOAP_DEFAULT_ELEMENTSET = "src/test/resources/requests/get_record_by_id_soap_default_elementset.xml";

    public static final String GETRECBYIDINVALID_SOAP = "src/test/resources/requests/get_record_by_id_invalid_soap.xml";
    public static final String GETRECORDS_INVALID_MAXRECORDS = "src/test/resources/requests/get_records_invalid_maxrecords.xml";
    public static final String GETRECORDS_INVALID_STARTPOSITION = "src/test/resources/requests/get_records_invalid_startposition.xml";

    public static final String GETRECORDS_1_BRIEF_SOAP = "src/test/resources/requests/get_records_1_brief_soap.xml";
    public static final String GETRECORDS_1_SUMMARY_SOAP = "src/test/resources/requests/get_records_1_summary_soap.xml";
    public static final String GETRECORDS_1_FULL_SOAP = "src/test/resources/requests/get_records_1_full_soap.xml";
    public static final String GETRECORDS_2_FULL_SOAP = "src/test/resources/requests/get_records_2_full_soap.xml";
    public static final String GETRECORDS_2_FULL_SOAP_DESC = "src/test/resources/requests/get_records_2_full_soap_desc.xml";

    public static final String GETRECORDS_2_SOAP = "src/test/resources/requests/get_records_2_soap.xml";
    public static final String GETRECORDS_3_SOAP = "src/test/resources/requests/get_records_3_soap.xml";
    public static final String GETRECORDS_4_SOAP = "src/test/resources/requests/get_records_4_soap.xml";
    public static final String GETRECORDS_5_SOAP = "src/test/resources/requests/get_records_5_soap.xml";
    public static final String GETRECORDS_6_SOAP = "src/test/resources/requests/get_records_6_soap.xml";
    public static final String GETRECORDS_7_SOAP = "src/test/resources/requests/get_records_7_soap.xml";
    public static final String GETRECORDS_8_SOAP = "src/test/resources/requests/get_records_8_soap.xml";
    public static final String GETRECORDS_9_SOAP = "src/test/resources/requests/get_records_9_soap.xml";
    public static final String GETRECORDS_10_SOAP = "src/test/resources/requests/get_records_10_soap.xml";
    public static final String GETRECORDS_11_SOAP = "src/test/resources/requests/get_records_11_soap.xml";
    public static final String GETRECORDS_12_SOAP = "src/test/resources/requests/get_records_12_soap.xml";

    public static final String GETRECORDS_HITS_SOAP = "src/test/resources/requests/get_records_hits_soap.xml";
    public static final String GETRECORDS_OUTPUT_SCHEMA_OGC ="src/test/resources/requests/ogc_test/output_schema_getrecords.xml";
    public static final String GETRECORDS_ID_OUTPUT_SCHEMA_OGC ="src/test/resources/requests/ogc_test/output_schema_getrecords_id.xml";

    public static final String GETRECORDS_OGC = "src/test/resources/requests/ogc_test/soap_get_records_ogc.xml";
    public static final String GETRECORDSINVALID_1_SOAP = "src/test/resources/requests/get_records_invalid_1_soap.xml";
    public static final String GETRECORDSINVALID_2_SOAP = "src/test/resources/requests/get_records_invalid_2_soap.xml";
    public static final String GETRECORDSINVALID_3_SOAP = "src/test/resources/requests/get_records_invalid_3_soap.xml";
    public static final String GETRECORDSINVALID_4_SOAP = "src/test/resources/requests/get_records_invalid_4_soap.xml";
    public static final String GETRECORDSINVALID_5_SOAP = "src/test/resources/requests/get_records_invalid_5_soap.xml";
    public static final String GETRECORDSINVALID_6_SOAP = "src/test/resources/requests/get_records_invalid_6_soap.xml";
    public static final String GETRECORDSINVALID_7_SOAP = "src/test/resources/requests/get_records_invalid_7_soap.xml";

    public static final String TRANSACTION_SOAP = "src/test/resources/requests/transaction_soap.xml";

    /**
     * Get the content of a request defined in the given file.
     *
     * @param filePath
     * @return String
     * @throws Exception
     */
    public static String getRequest(String filePath) throws Exception {
        File requestFile = new File( filePath );
        Scanner scanner = null;
        String content = null;
        try {
            scanner = new Scanner( requestFile );
            scanner.useDelimiter( "\\A" );
            content = scanner.next();
            scanner.close();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return content;
    }
}
