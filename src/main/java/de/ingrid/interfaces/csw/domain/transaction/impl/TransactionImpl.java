/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.transaction.impl;

import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.transaction.CSWTransaction;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class TransactionImpl implements CSWTransaction {

    /** Request id xpath **/
    private static String REQUEST_ID_PARAM_XPATH = "/csw:Transaction/@requestId";

    private String requestId;
    private String catalog;
    private Node content;

    /**
     * Constructor
     * @param catalog
     * @param content
     */
    public TransactionImpl(String catalog, Node content) {
        this.catalog = catalog;
        this.content = content;
        XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());
        this.requestId = xpath.getString(content, REQUEST_ID_PARAM_XPATH);
    }

    @Override
    public String getRequestId() {
        return this.requestId;
    }

    @Override
    public String getCatalog() {
        return this.catalog;
    }

    @Override
    public Node getContent() {
        return this.content;
    }
}
