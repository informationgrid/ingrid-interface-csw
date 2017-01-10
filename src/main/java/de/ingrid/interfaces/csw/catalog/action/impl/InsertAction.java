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
package de.ingrid.interfaces.csw.catalog.action.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.catalog.action.Action;
import de.ingrid.interfaces.csw.catalog.action.ActionResult;
import de.ingrid.interfaces.csw.domain.CSWRecord;
import de.ingrid.interfaces.csw.domain.constants.ActionName;
import de.ingrid.interfaces.csw.domain.constants.ElementSetName;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

public class InsertAction extends AbstractAction implements Action {

    final protected static Log log = LogFactory.getLog(InsertAction.class);

    /**
     * Constructor
     * @param node
     */
    public InsertAction(Node node) {
        super(node);
    }

    @Override
    public ActionName getName() {
        return ActionName.INSERT;
    }

    @Override
    public ActionResult execute() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("Execute insert action:\n"+StringUtils.nodeToString(this.getNode()));
        }
        // TODO implement insert action
        ActionResult result = new ActionResult(this);
        // NOTE: this test implementation returns a sample record
        result.addRecord(new CSWRecord(ElementSetName.BRIEF, getExampleRecord()));
        return result;
    }

    /**
     * TODO remove after implementing the real action
     */
    private Document getExampleRecord() throws Exception {
        XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());
        Node record = xpath.getNode(StringUtils.stringToDocument(StringUtils.nodeToString(this.getNode())), "/*/csw:Record");
        return StringUtils.stringToDocument(StringUtils.nodeToString(record));
    }
}
