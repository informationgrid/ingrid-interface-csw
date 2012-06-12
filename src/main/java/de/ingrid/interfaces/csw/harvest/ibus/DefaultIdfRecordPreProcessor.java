/**
 * 
 */
package de.ingrid.interfaces.csw.harvest.ibus;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.ingrid.interfaces.csw.tools.StringUtils;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.dsc.Record;
import de.ingrid.utils.idf.IdfTool;
import de.ingrid.utils.xml.IDFNamespaceContext;
import de.ingrid.utils.xpath.XPathUtils;

/**
 * <p>
 * Add data from the plugdescription to the idf data record, if present. It sets
 * certain attributes on the idf:html element:
 * </p>
 * <ul>
 * <li>partner: comma separated list of partner of the iplug</li>
 * <li>provider: comma separated list of provider of the iplug</li>
 * <li>iplug: the iplug proxy id</li>
 * </ul>
 * 
 * 
 * @author joachim@wemove.com
 * 
 */
public class DefaultIdfRecordPreProcessor implements IdfRecordPreProcessor {

    /** Tool for evaluating xpath **/
    private static XPathUtils xpath = new XPathUtils(new IDFNamespaceContext());

    final private static Log log = LogFactory.getLog(DefaultIdfRecordPreProcessor.class);

    /*
     * (non-Javadoc)
     * 
     * @see
     * de.ingrid.interfaces.csw.harvest.ibus.IdfRecordPreProcessor#process(de
     * .ingrid.utils.dsc.Record)
     */
    @Override
    public void process(Record record) {
        if (record.containsKey(IBusHarvester.PLUGDESCRIPTION)) {
            PlugDescription pd = (PlugDescription) record.get(IBusHarvester.PLUGDESCRIPTION);
            try {
                Document idfDoc = StringUtils.stringToDocument(IdfTool.getIdfDataFromRecord(record));
                Element htmlNode = (Element) xpath.getNode(idfDoc, "//idf:html");
                htmlNode.setAttribute("partner", StringUtils.join(pd.getPartners(), ","));
                htmlNode.setAttribute("provider", StringUtils.join(pd.getProviders(), ","));
                htmlNode.setAttribute("iplug", pd.getProxyServiceURL());
                record.put(IdfTool.KEY_DATA, StringUtils.nodeToString(idfDoc.getDocumentElement()));
                record.remove(IdfTool.KEY_COMPRESSED);
            } catch (Exception e) {
                log.error("Error manipulating idf record.", e);
            }
        }

    }

}
