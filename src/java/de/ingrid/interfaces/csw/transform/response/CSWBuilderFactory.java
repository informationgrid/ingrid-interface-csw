/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderFactory {

    /**
     * Returns a CSW respnse builder for a metadata CSW object according to the parameters.
     * 
     * @param elementSetName The element set of the response. (brief, summary, full). Applies only for GetRecords requests.
     * @param profile The profile of the response ('CSW_2_0_DE_1_0_1'). Each profile can have a separate Builder to encapsulate the differences to the 'common' CSW2.0 resonse.
     * @return
     */
    public static CSWBuilderMetaData getBuilderMetadata(String elementSetName, String profile) {
        if (elementSetName.equalsIgnoreCase("brief") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderMetadata_brief_DE_1_0_1();
        } else if (elementSetName.equalsIgnoreCase("summary") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderMetadata_summary_DE_1_0_1();
        } else if (elementSetName.equalsIgnoreCase("full") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderMetadata_full_DE_1_0_1();
        }

        throw new IllegalArgumentException("No CSW metadata response builder found for elementSetName:" + elementSetName + ", profile:" + profile);
    }

    
    /**
     * Returns a CSW respnse builder according to the parameters.
     * 
     * @param resultType The type of the requested result. Can be 'hits' or 'results'. Applies only for GetRecords requests.
     * @param requestType Type of the request ('GetRecords', 'GetRecordById')
     * @param profile The profile of the response ('CSW_2_0_DE_1_0_1'). Each profile can have a separate Builder to encapsulate the differences to the 'common' CSW2.0 resonse.
     * @return
     */
    public static CSWBuilderType getBuilderType(String resultType, String requestType,
            String profile) {
        if (resultType.equalsIgnoreCase("hits") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderType_Hits_DE_1_0_1();
        } else if (resultType.equalsIgnoreCase("results") && requestType.equalsIgnoreCase("GetRecords") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderType_GetRecords_DE_1_0_1();
        } else if (resultType.equalsIgnoreCase("results") && requestType.equalsIgnoreCase("GetRecordById") && profile.equalsIgnoreCase("CSW_2_0_DE_1_0_1")) {
            return new CSWBuilderType_GetRecordById_DE_1_0_1();
        }

        throw new IllegalArgumentException("No CSW response builder found for resultType:" + resultType
                + ", requestType:" + requestType
                + ", profile:" + profile);
    }
    
}
