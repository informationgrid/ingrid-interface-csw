/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.domain;

import java.io.Serializable;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.enums.ElementSetName;

/**
 * Representation of a record returned by a CSW server.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWRecord {

	/**
	 * The record id
	 */
	private Serializable id;

	/**
	 * The element set name of the record
	 */
	private ElementSetName elementSetName;

	/**
	 * The XML content of the record
	 */
	private Node node;

	/**
	 * Initialize the record with the given DOM node.
	 * 
	 * @param elementSetName
	 * @param node
	 */
	public CSWRecord(ElementSetName elementSetName, Node node) {
		this.elementSetName = elementSetName;
		this.node = node;
		// TODO implement id extraction from record
	}

	/**
	 * Create a proxy record from a given id and element set name.
	 * The proxy does not contain the XML content, but may be used to
	 * identify the real subject.
	 * 
	 * @param id
	 * @param elementSetName
	 */
	public static CSWRecord getProxy(Serializable id, ElementSetName elementSetName) {
		CSWRecord proxy = new CSWRecord(elementSetName, null);
		proxy.id = null;
		return proxy;
	}

	/**
	 * Get the id of the record.
	 * 
	 * @return String
	 */
	public Serializable getId() {
		return this.id;
	}

	/**
	 * Get the element set name of the record.
	 * 
	 * @return ElementSetName
	 */
	public ElementSetName getElementSetName() {
		return this.elementSetName;
	}

	/**
	 * Get the DOM representation of the record.
	 * 
	 * @return Node
	 */
	public Node getDocument() {
		return this.node;
	}
}
