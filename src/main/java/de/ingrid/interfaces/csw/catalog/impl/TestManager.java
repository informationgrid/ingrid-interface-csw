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
package de.ingrid.interfaces.csw.catalog.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.catalog.Manager;
import de.ingrid.interfaces.csw.catalog.action.Action;
import de.ingrid.interfaces.csw.catalog.action.ActionResult;
import de.ingrid.interfaces.csw.catalog.action.impl.DeleteAction;
import de.ingrid.interfaces.csw.catalog.action.impl.InsertAction;
import de.ingrid.interfaces.csw.catalog.action.impl.UpdateAction;
import de.ingrid.interfaces.csw.domain.constants.ActionName;
import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransaction;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransactionResult;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

//@Service
public class TestManager implements Manager {

    /** Verbose response xpath **/
    //private static String VERBOSE_RESPONSE_PARAM_XPATH = "/csw:Transaction/@verboseResponse";

    /** Operations xpath **/
    private static String ACTIONS_XPATH = "/csw:Transaction/child::*";

    final protected static Log log = LogFactory.getLog(TestManager.class);

    private XPathUtils xpath = null;

    /**
     * Constructor
     */
    public TestManager() {
        this.xpath = new XPathUtils(new Csw202NamespaceContext());
    }

    @Override
    public CSWTransactionResult process(CSWTransaction transaction) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Processing transaction '"+transaction.getRequestId()+"' on catalog '"+transaction.getCatalog()+"'");
        }

        Node content = transaction.getContent();

        // check if a verbose response is requested (defaults to true)
        // TODO verboseResponse is not used yet
        //String verboseResponseStr = xpath.getString(content, VERBOSE_RESPONSE_PARAM_XPATH);
        //boolean verboseResponse = verboseResponseStr == null || "true".equals(verboseResponseStr.toLowerCase());

        // execute actions
        // NOTE: this class does not execute the actions inside a transaction
        int inserts = 0;
        int updates = 0;
        int deletes = 0;
        List<ActionResult> insertResults = new ArrayList<ActionResult>();
        NodeList actionNodes = xpath.getNodeList(content, ACTIONS_XPATH);
        for (int i=0, count=actionNodes.getLength(); i<count; i++) {
            Node actionNode = actionNodes.item(i);
            Action action = null;
            try {
                action = this.getAction(actionNode);
                ActionResult result = action.execute();
                switch (action.getName()) {
                    case INSERT:
                        insertResults.add(result);
                        inserts++;
                        break;
                    case UPDATE:
                        updates++;
                        break;
                    case DELETE:
                        deletes++;
                        break;
                }
            } catch (Exception e) {
                String code = "Transaction"+ (action != null ? action.getName() : "UnspecifiedError");
                throw new CSWException(e.getMessage(), code, this.getLocator(transaction, action));
            }
        }

        // construct result
        CSWTransactionResult result = new CSWTransactionResult(transaction.getRequestId());
        result.setNumberOfInserts(inserts);
        result.setNumberOfUpdates(updates);
        result.setNumberOfDeletes(deletes);
        result.setInsertResults(insertResults);
        result.setSuccessful( true );
        return result;
    }

    /**
     * Get the Action instance for the given node.
     * @param actionNode
     * @return Action
     * @throws Exception
     */
    protected Action getAction(Node actionNode) throws Exception {
        ActionName actionName = ActionName.getByName(actionNode.getLocalName());
        switch (actionName) {
            case INSERT:
                return new InsertAction(actionNode);
            case UPDATE:
                return new UpdateAction(actionNode);
            case DELETE:
                return new DeleteAction(actionNode);
        }
        return null;
    }

    /**
     * Get the exception locator for the given transaction and action.
     * @param transaction
     * @param action
     * @return String
     */
    protected String getLocator(CSWTransaction transaction, Action action) {
        String locator = transaction.getRequestId();
        if (action != null) {
            locator += " "+action.getHandle();
        }
        return locator;
    }
}
