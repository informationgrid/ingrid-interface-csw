/*
 * Copyright (c) 1997-2007 by wemove GmbH
 */
package de.ingrid.interfaces.csw.transform.response;

import org.dom4j.Element;

import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * TODO Describe your created type (class, etc.) here.
 * 
 * @author joachim@wemove.com
 */
public abstract class CSWBuilderType extends CSWBuilder {

    protected IngridHits hits;

    protected Element metaDataElement;

    protected IngridQuery query;
    
    protected SessionParameters session;
    
    protected String nsPrefix;

    public void setHits(IngridHits hits) {
        this.hits = hits;
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
    protected Element addCharacterString(Element parent, String value) {
        parent.addElement("smXML:CharacterString").addText(value);
        return parent;
    }

    /**
     * Add a smXML Boolean to a parent element
     * 
     * @param parent
     * @param value
     * @return
     */
    protected Element addBoolean(Element parent, boolean value) {
        if (value) {
            parent.addElement("smXML:Boolean").addText("true");
        } else {
            parent.addElement("smXML:Boolean").addText("false");
        }
        return parent;
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
    

}
