/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.transaction;

import org.w3c.dom.Node;

/**
 * Representation of a CSW transaction.
 * 
 * Each transaction might have a request id, that may be used by a client application 
 * to associate a user-defined identifier with the operation.
 * 
 * @see OpenGIS Catalogue Services Specification 2.0.2 - ISO Metadata
 *      Application Profile 8.2.3.1
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public interface CSWTransaction {

    /**
     * Get the request id
     * @return String
     */
    public String getRequestId();

    /**
     * Get the name of the catalog on which the transaction should be executed
     * @return String
     */
    public String getCatalog();

    /**
     * Get the content of the transaction
     * @return Node
     */
    public Node getContent();
}
