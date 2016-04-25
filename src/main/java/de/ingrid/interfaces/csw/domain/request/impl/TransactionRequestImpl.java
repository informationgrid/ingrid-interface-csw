/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.request.impl;

import de.ingrid.interfaces.csw.domain.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.domain.request.TransactionRequest;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransaction;
import de.ingrid.interfaces.csw.domain.transaction.impl.TransactionImpl;

public class TransactionRequestImpl extends AbstractRequestImpl implements TransactionRequest {

    /** Request parameters **/
    private static final String CATALOG_PARAM_NAME = "catalog";

    private CSWTransaction transaction = null;

    @Override
    public void validate() throws CSWException {
        String catalog = this.getCatalog();
        if (catalog == null || catalog.isEmpty()) {
            throw new CSWMissingParameterValueException("Catalog is not specified or has no value", "Transaction");
        }
    }

    @Override
    public CSWTransaction getTransaction() throws CSWException {
        if (this.transaction == null) {
            // extract request body
            // NOTE: Transaction requests are only allowed in XML encoding
            CSWMessageEncoding encoding = this.getEncoding(); 
            if (encoding instanceof XMLEncoding) {
                this.transaction = new TransactionImpl(this.getCatalog(), ((XMLEncoding)encoding).getRequestBody());
            }
            else {
                throw new CSWException("Transaction requests must use XML encoding", "TransactionUnspecifiedError", "");
            }
        }
        return this.transaction;
    }

    /**
     * Get the name of the catalog on which the transaction should be executed
     * @return String
     */
    private String getCatalog() {
        return this.getEncoding().getRequest().getParameter(CATALOG_PARAM_NAME);
    }
}
