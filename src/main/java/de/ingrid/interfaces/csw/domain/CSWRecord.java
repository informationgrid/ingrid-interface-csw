/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;

/**
 * Representation of a record returned by a CSW server.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWRecord {

    /**
     * Initialize the record with the given DOM node.
     * 
     * @param elementSetName
     * @param node
     */
    public void initialize(ElementSetName elementSetName, Node node)
	    throws Exception;

    /**
     * Get the id of the record.
     * 
     * @return String
     */
    public String getId();

    /**
     * Get the elementset name of the record.
     * 
     * @return ElementSetName
     */
    public ElementSetName getElementSetName();

    /**
     * Get the DOM representation of the record.
     * 
     * @return Node
     */
    public Node getDocument();

}
