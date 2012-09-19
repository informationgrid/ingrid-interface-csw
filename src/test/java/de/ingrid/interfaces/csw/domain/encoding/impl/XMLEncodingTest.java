/**
 * 
 */
package de.ingrid.interfaces.csw.domain.encoding.impl;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.domain.TestRequests;
import de.ingrid.interfaces.csw.domain.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.xml.Csw202NamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * @author joachim
 * 
 */
public class XMLEncodingTest {

    /** Tool for evaluating xpath **/
    private XPathUtils xpath = new XPathUtils(new Csw202NamespaceContext());

    /**
     * Test method for
     * {@link de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding#getQuery(org.w3c.dom.Node)}
     * .
     */
    @Test
    public void testGetQueryNode() throws Exception {
        XMLEncoding encoding = new XMLEncoding();
        String requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_INVALID_MAXRECORDS);
        Node requestBody = this.xpath.getNode(StringUtils.stringToDocument(requestStr), "//csw:GetRecords");
        encoding.setRequestBody(requestBody);
        try {
            encoding.getQuery();
            fail("Must throw CSWInvalidParameterValueException!");
        } catch (CSWInvalidParameterValueException e) {
        }

        encoding = new XMLEncoding();
        requestStr = TestRequests.getRequest(TestRequests.GETRECORDS_INVALID_STARTPOSITION);
        requestBody = this.xpath.getNode(StringUtils.stringToDocument(requestStr), "//csw:GetRecords");
        encoding.setRequestBody(requestBody);
        try {
            encoding.getQuery();
            fail("Must throw CSWInvalidParameterValueException!");
        } catch (CSWInvalidParameterValueException e) {
        }
    
    }

}
