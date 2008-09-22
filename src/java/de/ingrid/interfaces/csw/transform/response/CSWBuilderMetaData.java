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
public abstract class CSWBuilderMetaData extends CSWBuilder {

    protected IngridHit hit;

    protected Element metaDataElement;

    protected IngridQuery query;
    
    protected SessionParameters session;
    
    protected String nsPrefix;
    
    public void setHit(IngridHit hit) {
        this.hit = hit;
    }
    
    public void setMetaDataElement(Element metaDataElement) {
        this.metaDataElement = metaDataElement;
    }

    public void setQuery(IngridQuery q) {
        this.query = q;
    }

    public void setSessionParameter(SessionParameters session) {
        this.session = session;
    }
    
    /**
     * Add a smXML character string to a parent element.
     * 
     * @param parent
     * @param value
     * @return The parent element.
     */
    protected Element addSMXMLCharacterString(Element parent, String value) {
        parent.addElement("smXML:CharacterString").addText(value.replaceAll("&(?![a-z]+;)", "&amp;"));
        return parent;
    }

    /**
     * Add a smXML URL to a parent element.
     * 
     * @param parent
     * @param value
     * @return The parent element.
     */
    protected Element addSMXMLUrl(Element parent, String value) {
        parent.addElement("smXML:URL").addText(value);
        return parent;
    }
    
    /**
     * Add a smXML Boolean to a parent element
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addSMXMLBoolean(Element parent, boolean value) {
        if (value) {
            parent.addElement("smXML:Boolean").addText("true");
        } else {
            parent.addElement("smXML:Boolean").addText("false");
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
    protected Element addSMXMLReal(Element parent, String value) {
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
    	parent.addElement("smXML:Real").addText(numberStr);
    	return parent;
    }
    
    /**
     * Add a smXML decimal object to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addSMXMLDecimal(Element parent, String value) {
        String numberStr;
    	try {
			long n = Long.parseLong(value);
			numberStr = String.valueOf(n);
		} catch (NumberFormatException e) {
			numberStr = "";
		}
    	parent.addElement("smXML:Decimal").addText(numberStr);
        return parent;
    }

    /**
     * Add a smXML positive Integer to the parent.
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addSMXMLPositiveInteger(Element parent, String value) {
        parent.addElement("smXML:positiveInteger").addText(value);
        return parent;
    }
    
    /**
     * Transform the dom4j document of this class into a w3c.dom.Document.
     * 
     * @return
     * @throws DocumentException
     */
    public static org.w3c.dom.Document transformtoDOM(Document doc) throws DocumentException {
      DOMWriter writer = new DOMWriter();
      return writer.write(doc);
    }

    /**
     * @return Returns the nsPrefix.
     */
    public String getNSPrefix() {
        return nsPrefix;
    }

    /**
     * @param nsPrefix The nsPrefix to set.
     */
    public void setNSPrefix(String nsPrefix) {
        this.nsPrefix = nsPrefix;
    }
    
    protected String getNSElementName(String ns, String elName) {
        if (ns != null) {
            return ns.concat(":").concat(elName);
        } else {
            return elName;
        }
    }
    
    protected String getISO639_2LanguageCode(String numberBasedLang) {
		if (numberBasedLang == null) {
			return null;
		}
    	if (numberBasedLang.equals("121")) {
    		return "ger";
    	} else if (numberBasedLang.equals("94")) {
    		return "eng";
    	} else {
    		return numberBasedLang;
    	}
    }
    
    
}
