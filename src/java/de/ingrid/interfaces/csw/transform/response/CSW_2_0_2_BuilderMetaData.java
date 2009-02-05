/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

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

    /**
     * Add a smXML character string to a parent element.
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
     * Add a smXML URL to a parent element.
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
     * Add a smXML Boolean to a parent element
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
     * Add a smXML real object to the parent.
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
			numberStr = "NaN";
		}
    	parent.addElement("gco:Real").addText(numberStr);
    	return parent;
    }
    
    /**
     * Add a smXML decimal object to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCODecimal(Element parent, String value) {
        String numberStr;
    	try {
			long n = Long.parseLong(value);
			numberStr = String.valueOf(n);
		} catch (NumberFormatException e) {
			numberStr = "NaN";
		}
    	parent.addElement("gco:Decimal").addText(numberStr);
        return parent;
    }

    /**
     * Add a smXML positive Integer to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addGCOPositiveInteger(Element parent, String value) {
        parent.addElement("gco:positiveInteger").addText(value);
        return parent;
    }
    
}
