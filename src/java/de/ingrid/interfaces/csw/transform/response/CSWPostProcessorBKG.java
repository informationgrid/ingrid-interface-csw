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
			HashMap map = new HashMap();
			map.put( "iso19115full", "http://schemas.opengis.net/iso19115full");
			map.put( "smXML", "http://metadata.dgiwg.org/smXML");
			  
			XPath xpath = new Dom4jXPath( "//iso19115full:contact/smXML:CI_ResponsibleParty[//smXML:CI_RoleCode[@codeListValue=\"Point of Contact\"]]");
			xpath.setNamespaceContext( new SimpleNamespaceContext( map));
			  
			List list = xpath.selectNodes( in);			
			
			if (log.isDebugEnabled()) {
				log.debug("Search for '//iso19115full:contact/smXML:CI_ResponsibleParty[//smXML:CI_RoleCode[@codeListValue=\"Point of Contact\"]]' ... found " + list.size() + "elements.");
			}

			XPath xpath2 = new Dom4jXPath( "//iso19115full:identificationInfo");
			xpath2.setNamespaceContext( new SimpleNamespaceContext( map));
			
			Element e = (Element)xpath2.selectSingleNode(in);
			if (log.isDebugEnabled()) {
				log.debug("get '//iso19115full:identificationInfo':" + e.getName());
			}
			for (Iterator it = list.iterator(); it.hasNext(); ) {
	            e.addElement("smXML:pointOfContact").add((Element) it.next());
	    		if (log.isDebugEnabled()) {
	    			log.debug("added pointOfContact:" + e.asXML());
	    		}
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
