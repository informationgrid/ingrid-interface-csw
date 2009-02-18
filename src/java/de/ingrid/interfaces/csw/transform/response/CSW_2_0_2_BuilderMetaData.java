/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.DOMWriter;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class CSW_2_0_2_BuilderMetaData extends CSWBuilderMetaData {

    private static Log log = LogFactory.getLog(CSW_2_0_2_BuilderMetaData.class);
	
	
    /**
     * Add a gco character string to a parent element.
     * 
     * @param parent
     * @param value
     * @return The parent element.
     */
    protected Element addGCOCharacterString(Element parent, String value) {
        parent.addElement("gco:CharacterString").addText(value.replaceAll("&(?![a-z]+;)", "&amp;"));
        return parent;
    }

    /**
     * Add a gco local name to a parent element.
     * 
     * @param parent
     * @param value
     * @return The parent element.
     */
    protected Element addGCOLocalName(Element parent, String value) {
        parent.addElement("gco:LocalName").addText(value.replaceAll("&(?![a-z]+;)", "&amp;"));
        return parent;
    }
    
    /**
     * Add a gco URL to a parent element.
     * 
     * @param parent
     * @param value
     * @return The parent element.
     */
    protected Element addGMDUrl(Element parent, String value) {
        parent.addElement("gmd:URL").addText(value);
        return parent;
    }
    
    /**
     * Add a gco Boolean to a parent element
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCOBoolean(Element parent, boolean value) {
        if (value) {
            parent.addElement("gco:Boolean").addText("true");
        } else {
            parent.addElement("gco:Boolean").addText("false");
        }
        return parent;
    }
    
    /**
     * Add a gco real object to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCOReal(Element parent, String value) {
        String numberStr;
    	try {
			double n = Double.parseDouble(value);
			if (Double.isNaN(n)) {
				numberStr = "NaN";
			} else if (Double.isInfinite(n)) {
				numberStr = "INF";
			} else {
				numberStr = String.valueOf(n);
			}
		} catch (NumberFormatException e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not convert to Double: " + value, e);
			}
			numberStr = "NaN";
		}
    	parent.addElement("gco:Real").addText(numberStr);
    	return parent;
    }
    
    /**
     * Add a gco decimal object to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCODecimal(Element parent, String value) {
        String numberStr;
    	try {
			double n = Double.parseDouble(value);
			numberStr = String.valueOf(n);
		} catch (NumberFormatException e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not convert to Double: " + value, e);
			}
			numberStr = "NaN";
		}
    	parent.addElement("gco:Decimal").addText(numberStr);
        return parent;
    }

    /**
     * Add a gco Integer to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCOInteger(Element parent, String value) {
        String numberStr;
    	try {
			int n = Integer.parseInt(value);
			numberStr = String.valueOf(n);
		} catch (NumberFormatException e) {
			if (log.isDebugEnabled()) {
				log.debug("Could not convert to Integer: " + value, e);
			}
			numberStr = "NaN";
		}
        parent.addElement("gco:Integer").addText(numberStr);
        return parent;
    }
    
}
