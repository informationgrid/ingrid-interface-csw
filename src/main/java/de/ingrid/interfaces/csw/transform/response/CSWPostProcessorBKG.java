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
import org.dom4j.Namespace;
import org.dom4j.QName;
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
			namespaces.put( "iso19115summary", "http://schemas.opengis.net/iso19115summary");
			namespaces.put( "iso19115brief", "http://schemas.opengis.net/iso19115brief");
			namespaces.put( "iso19119", "http://schemas.opengis.net/iso19119");
			namespaces.put( "smXML", "http://metadata.dgiwg.org/smXML");
			namespaces.put( "gml", "http://www.opengis.net/gml");
			// *************************************************************************************
			// copy point of contact address to iso19115full:identificationInfo/smXML:pointOfContact
			// *************************************************************************************
			XPath srcXPath = new Dom4jXPath( "//iso19115full:contact/smXML:CI_ResponsibleParty[smXML:role/smXML:CI_RoleCode[@codeListValue=\"Point of Contact\"]]");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			List srcElementList = srcXPath.selectNodes( in);			
			XPath dstXPath = new Dom4jXPath( "//iso19115full:identificationInfo");
			dstXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			Object result = dstXPath.selectSingleNode(in);
			if (result != null) {
				Element dstElement = ((Element)result).addElement("smXML:pointOfContact");
			
				for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
		            Element srcElementCopy = ((Element) it.next()).createCopy();
					dstElement.add(srcElementCopy);
		        }
			}
			
			// *************************************************************************************
			// add value of //smXML:MD_SpatialRepresentationTypeCode[@codeValue] as Text Tag to smXML:MD_SpatialRepresentationTypeCode
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//smXML:MD_SpatialRepresentationTypeCode");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            srcElement.addText(srcElement.attributeValue("codeListValue"));
	        }
			
			// *************************************************************************************
			// copy values of gml:TimePeriod/gml:beginPosition/gml:TimeInstant/gml:timePosition to gml:TimePeriod/gml:begin
			// and gml:TimePeriod/gml:endPosition/gml:TimeInstant/gml:timePosition to gml:TimePeriod/gml:end
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//gml:TimePeriod");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            srcElement.addElement("gml:begin").addText(srcElement.valueOf("gml:beginPosition/gml:TimeInstant/gml:timePosition"));
	            srcElement.addElement("gml:end").addText(srcElement.valueOf("gml:endPosition/gml:TimeInstant/gml:timePosition"));
	        }

			// *************************************************************************************
			// copy values of //smXML:status/smXML:MD_ProgressCode@codeListValue to //smXML:status/smXML:MD_ProgressCode
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//smXML:status/smXML:MD_ProgressCode");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            srcElement.addText(srcElement.attributeValue("codeListValue"));
	        }
			
			// *************************************************************************************
			// copy  //smXML:MD_Distribution/smXML:distributor/smXML:MD_Distributor/smXML:distributorFormat/smXML:MD_Format 
			// to //smXML:MD_Distribution/smXML:distributionFormat/smXML:MD_Format
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//smXML:MD_Distribution");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            try {
		            Element elToCopy = srcElement.element("distributor").element("MD_Distributor").element("distributorFormat").element("MD_Format").createCopy();
		            srcElement.addElement("smXML:distributionFormat").add(elToCopy);
	            } catch (Exception e) {
	            	if (log.isDebugEnabled()) {
	            		log.debug("No element 'MD_Format' found below //smXML:MD_Distribution!");
	            	}
	            }
	        }
			
			// *************************************************************************************
			// copy values of //smXML:MD_MediumNameCode@codeListValue to //smXML:MD_MediumNameCode
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//smXML:MD_MediumNameCode");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes(in);
			for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
	            Element srcElement = (Element) it.next();
	            srcElement.addText(srcElement.attributeValue("codeListValue"));
	        }
			
			// *************************************************************************************
			// copy distributor address to //smXML:MD_Distributor
			// *************************************************************************************
			srcXPath = new Dom4jXPath( "//iso19115full:contact/smXML:CI_ResponsibleParty[smXML:role/smXML:CI_RoleCode[@codeListValue=\"Distributor\"]]");
			srcXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			srcElementList = srcXPath.selectNodes( in);			
			dstXPath = new Dom4jXPath( "//smXML:MD_Distributor");
			dstXPath.setNamespaceContext( new SimpleNamespaceContext( namespaces));
			result = dstXPath.selectSingleNode(in);
			if (result != null) {
				Element dstElement = ((Element)result).addElement("smXML:distributorContact");
			
				for (Iterator it = srcElementList.iterator(); it.hasNext(); ) {
		            Element srcElementCopy = ((Element) it.next()).createCopy();
					dstElement.add(srcElementCopy);
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
