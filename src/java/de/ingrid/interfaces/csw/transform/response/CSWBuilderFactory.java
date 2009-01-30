/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import de.ingrid.interfaces.csw.analyse.SessionParameters;

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
    public static CSWBuilderMetaData getBuilderMetadata(SessionParameters session) {
        if (session.getElementSetName().equalsIgnoreCase("brief") && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderMetadata_brief_DE_1_0_1();
        } else if (session.getElementSetName().equalsIgnoreCase("summary") && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderMetadata_summary_DE_1_0_1();
        } else if (session.getElementSetName().equalsIgnoreCase("full") && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderMetadata_full_DE_1_0_1();
        } else if (session.getElementSetName().equalsIgnoreCase("brief") && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderMetadata_brief_CSW_2_0_2_AP_ISO_1_0();
        } else if (session.getElementSetName().equalsIgnoreCase("summary") && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderMetadata_summary_CSW_2_0_2_AP_ISO_1_0();
        } else if (session.getElementSetName().equalsIgnoreCase("full") && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderMetadata_full_CSW_2_0_2_AP_ISO_1_0();
        }

        throw new IllegalArgumentException("No CSW metadata response builder found for elementSetName:" + session.getElementSetName() + ", version:" + session.getVersion());
    }

    
    /**
     * Returns a CSW response builder according to the parameters.
     * 
     * @param session The session of the request. Contains several parameter of the CSW request
     * @return
     */
    public static CSWBuilderType getBuilderType(SessionParameters session) {
        if (session.getResultType().equalsIgnoreCase("hits") && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderType_Hits_DE_1_0_1();
        } else if (session.getResultType().equalsIgnoreCase("results") && session.isOperationIsGetRecs() && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderType_GetRecords_DE_1_0_1();
        } else if (session.getResultType().equalsIgnoreCase("results") && session.isOperationIsGetRecById() && session.getVersion().equalsIgnoreCase("2.0.0")) {
            return new CSWBuilderType_GetRecordById_DE_1_0_1();
        } else if (session.getResultType().equalsIgnoreCase("hits") && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderType_Hits_CSW_2_0_2_AP_ISO_1_0();
        } else if (session.getResultType().equalsIgnoreCase("results") && session.isOperationIsGetRecs() && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderType_GetRecords_CSW_2_0_2_AP_ISO_1_0();
        } else if (session.getResultType().equalsIgnoreCase("results") && session.isOperationIsGetRecById() && session.getVersion().equalsIgnoreCase("2.0.2")) {
            return new CSWBuilderType_GetRecordById_CSW_2_0_2_AP_ISO_1_0();
        }

        throw new IllegalArgumentException("No CSW response builder found for resultType:" + session.getResultType()
                + ", requestType:" + session.getRequestOperation()
                + ", version:" + session.getVersion().equalsIgnoreCase("2.0.0"));
    }
    
}
