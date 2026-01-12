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
/**
 *
 */
package de.ingrid.interfaces.csw.tools;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.domain.constants.Namespace;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author joachim
 *
 */
public class OGCFilterTools {

    /** Tool for evaluating xpath **/
    private static XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    private static final String CSW_NAMESPACE = Namespace.CSW_2_0_2.getQName().getNamespaceURI();
    private static final String OGC_NAMESPACE = Namespace.OGC.getQName().getNamespaceURI();

    /**
     * Add a constraint with method PropertyIsEqual to the Query. If an
     * ogc:Filter/ogc:And construct already exists, add the constraint there.
     *
     * @param queryNode
     * @param key
     * @param value
     */
    public static void addPropertyIsEqual(Node queryNode, String key, String value) {

        Document doc = queryNode.getOwnerDocument();

        if (!xpath.nodeExists(queryNode, "csw:Constraint")) {
            queryNode.appendChild(doc.createElementNS(CSW_NAMESPACE, "Constraint"));
        }

        if (xpath.nodeExists(queryNode, "csw:Constraint") && !xpath.nodeExists(queryNode, "csw:Constraint/ogc:Filter")) {
            Node constraintNode = xpath.getNode(queryNode, "csw:Constraint");
            constraintNode.appendChild(doc.createElementNS(OGC_NAMESPACE, "Filter"));
        }

        if (xpath.nodeExists(queryNode, "csw:Constraint/ogc:Filter")) {
            Node dstNode;
            if (xpath.nodeExists(queryNode, "csw:Constraint/ogc:Filter/ogc:And")) {
                dstNode = xpath.getNode(queryNode, "csw:Constraint/ogc:Filter/ogc:And");
            } else {
                Node filter = xpath.getNode(queryNode, "csw:Constraint/ogc:Filter");
                NodeList children = filter.getChildNodes();
                if (children.getLength() > 0) {
                    dstNode = filter.appendChild(doc.createElementNS(OGC_NAMESPACE, "And"));
                    OGCFilterTools.moveNodes(children, dstNode);
                } else {
                    dstNode = filter;
                }
            }
            Node propertyIsEqualTo = dstNode.appendChild(doc.createElementNS(OGC_NAMESPACE, "PropertyIsEqualTo"));
            propertyIsEqualTo.appendChild(doc.createElementNS(OGC_NAMESPACE, "PropertyName"))
            .appendChild(doc.createTextNode(key));
            propertyIsEqualTo.appendChild(doc.createElementNS(OGC_NAMESPACE, "Literal")).appendChild(
                    doc.createTextNode(value));
        }
    }

    /**
     * Move a NodeList to a new existing node within a DOM tree.
     *
     *
     * @param srcList
     * @param dstNode
     */
    public static void moveNodes(NodeList srcList, Node dstNode) {
        for (int i = 0; i < srcList.getLength(); i++) {
            if (dstNode != srcList.item(0)) {
                Node n = srcList.item(0).getParentNode().removeChild(srcList.item(0));
                dstNode.appendChild(n);
            }
        }
    }

}
