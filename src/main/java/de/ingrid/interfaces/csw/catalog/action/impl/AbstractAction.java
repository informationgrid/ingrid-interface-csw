/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2026 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.catalog.action.impl;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.catalog.action.Action;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * Base class for actions defining common behavior.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class AbstractAction implements Action {

    /** Verbose response xpath **/
    private static String HANDLE_PARAM_XPATH = "@handle";

    private XPathUtils xpath = null;
    private Node node = null;
    private String handle = null;

    /**
     * Constructor
     * @param node
     */
    public AbstractAction(Node node) {
        this.node = node;
        this.xpath = new XPathUtils(new Csw202NamespaceContext());

        this.handle = xpath.getString(node, HANDLE_PARAM_XPATH);
    }

    @Override
    public String getHandle() {
        return this.handle;
    }

    /**
     * Get the node describing the action
     * @return Node
     */
    protected Node getNode() {
        return this.node;
    }
}
