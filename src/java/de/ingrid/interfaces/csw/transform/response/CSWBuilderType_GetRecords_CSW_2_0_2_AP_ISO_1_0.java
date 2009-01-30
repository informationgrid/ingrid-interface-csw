/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.GregorianCalendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.transform.response.adapter.DscEcsVersionMapperFactory;
import de.ingrid.interfaces.csw.utils.IPlugVersionInspector;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public class CSWBuilderType_GetRecords_CSW_2_0_2_AP_ISO_1_0 extends CSWBuilderType {

    private static Log log = LogFactory.getLog(CSWBuilderType_GetRecords_CSW_2_0_2_AP_ISO_1_0.class);
    
    public Element build() throws Exception {

        throw new Exception("not yet implemented");
    }

    /**
     * Adds the beginning of a CSW response for GetRecords requests to a
     * root element.
     * 
     * @param doc
     *            The document to add the elements to.
     * @param hits
     *            The hits of an Ingrid Query.
     * @return The 'SearchResults' element of the CSW response.
     */
    private Element getResponseHeader_GetRecords(Element parent, IngridHits hits) throws Exception {
        throw new Exception("not yet implemented");

    }
    
}
