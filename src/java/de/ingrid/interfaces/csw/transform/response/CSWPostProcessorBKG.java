/**
 * 
 */
package de.ingrid.interfaces.csw.transform.response;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom4j.Dom4jXPath;

/**
 * @author joachim
 *
 */
public class CSWPostProcessorBKG implements CSWPostProcessor {

	private static Log log = LogFactory.getLog(CSWPostProcessorBKG.class);
	
	/* (non-Javadoc)
	 * @see de.ingrid.interfaces.csw.transform.response.CSWPostProcessor#process(org.dom4j.Document)
	 */
	public Document process(Document in) {

		if (log.isDebugEnabled()) {
			log.debug("input:" + in.asXML());
		}
		try {
			HashMap namespaces = new HashMap();
			namespaces.put( "iso19115full", "http://schemas.opengis.net/iso19115full");
			namespaces.put( "smXML", "http://metadata.dgiwg.org/smXML");
			
			
			// *************************************************************************************
			// copy point of contact address to iso19115full:identificationInfo/smXML:pointOfContact
			// *************************************************************************************
			XPath srcXPath = new Dom4jXPath( "//iso19115full:contact/smXML:CI_ResponsibleParty[//smXML:CI_RoleCode[@codeListValue=\"Point of Contact\"]]");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			  
			List srcElementList = srcXPath.selectNodes( in);			

			XPath dstXPath = new Dom4jXPath( "//iso19115full:identificationInfo");
			dstXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			Element dstElement = ((Element)dstXPath.selectSingleNode(in)).addElement("smXML:pointOfContact");
			
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElementCopy = ((Element) it.next()).createCopy();
				dstElement.add(srcElementCopy);
	        }
			
			// add value of //smXML:MD_SpatialRepresentationTypeCode[@codeValue] as Text Tag to smXML:MD_SpatialRepresentationTypeCode
			srcXPath = new Dom4jXPath( "//smXML:MD_SpatialRepresentationTypeCode");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            srcElement.addText(srcElement.attributeValue("codeListValue"));
	        }
			
		} catch (Exception e) {
			log.error("Error processing document.", e);
		}
		if (log.isDebugEnabled()) {
			log.debug("output:" + in.asXML());
		}
		return in;
	}

}
