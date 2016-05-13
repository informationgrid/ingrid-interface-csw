/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2016 wemove digital solutions GmbH
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

import java.io.Serializable;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Representation of a record returned by a CSW server.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWRecord {

	private static final String ID_XPATH = "/gmd:MD_Metadata/gmd:fileIdentifier/gco:CharacterString";

	private static XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());

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
		// extract record id
		this.id = xpath.getString(node, ID_XPATH);
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
