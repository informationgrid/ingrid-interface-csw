/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.catalog.Manager;
import de.ingrid.interfaces.csw.catalog.action.Action;
import de.ingrid.interfaces.csw.catalog.action.impl.DeleteAction;
import de.ingrid.interfaces.csw.catalog.action.impl.InsertAction;
import de.ingrid.interfaces.csw.catalog.action.impl.UpdateAction;
import de.ingrid.interfaces.csw.domain.constants.ActionName;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransaction;
import de.ingrid.interfaces.csw.domain.transaction.CSWTransactionResult;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;

@Service
public class TransactionManager implements Manager {
    
    private static final String DATA_PARAMETER = "data";

    /** Verbose response xpath **/
    //private static String VERBOSE_RESPONSE_PARAM_XPATH = "/csw:Transaction/@verboseResponse";

    /** Operations xpath **/
    // private static String ACTIONS_XPATH = "/csw:Transaction/child::*";

    final protected static Log log = LogFactory.getLog(TransactionManager.class);

    // private XPathUtils xpath = null;

    /**
     * Constructor
     */
    public TransactionManager() {
        // this.xpath = new XPathUtils(new Csw202NamespaceContext());
    }

    @Override
    public CSWTransactionResult process(CSWTransaction transaction) throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Processing transaction '"+transaction.getRequestId()+"' on catalog '"+transaction.getCatalog()+"'");
        }

        Node content = transaction.getContent();
        String contentString = StringUtils.nodeToString( content );

        // check if a verbose response is requested (defaults to true)
        // TODO verboseResponse is not used yet
        //String verboseResponseStr = xpath.getString(content, VERBOSE_RESPONSE_PARAM_XPATH);
        //boolean verboseResponse = verboseResponseStr == null || "true".equals(verboseResponseStr.toLowerCase());

        // execute actions
        CSWTransactionResult result = new CSWTransactionResult(transaction.getRequestId());
        
        BusClient busClient = BusClientFactory.getBusClient();
        if (busClient == null) {
            result.setSuccessful( false );
            result.setErrorMessage( "The connection to the iBus has not been configured." );
            return result;
        }
        
        HashMap<String,Object> map = new HashMap<String, Object>();
        // this information tells the iBus-client to not use the cache
        map.put( "disableCache", "cache: false" );
        map.put( DATA_PARAMETER, contentString );
        
        IBus bus = busClient.getNonCacheableIBus();
        IngridCall targetInfo = new IngridCall();
        targetInfo.setMethod( "importCSWDoc" );
        targetInfo.setParameter( map );
        targetInfo.setTarget( transaction.getCatalog() );
        IngridDocument response = bus.call( targetInfo  );
        IngridDocument responseResult = (IngridDocument) response.get( "result" );

        // construct result
        if (responseResult != null) {
            result.setNumberOfInserts(responseResult.getInt( "inserts" ));
            result.setNumberOfUpdates(responseResult.getInt( "updates" ));
            result.setNumberOfDeletes(responseResult.getInt( "deletes" ));
        }
        result.setSuccessful( response.getBoolean( "success" ) );
        result.setErrorMessage( response.getString( "error" ) );
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
