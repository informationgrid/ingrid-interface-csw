/**
 * 
 */
package de.ingrid.interfaces.csw2.tools;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * @author Administrator
 * TODO: use Namespace constants
 *
 */
public class CSWNamespaceContext implements NamespaceContext {

	/**
	 * @see javax.xml.namespace.NamespaceContext#getNamespaceURI(java.lang.String)
	 */
	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix == null) throw new NullPointerException("Null prefix");
        else if ("csw".equals(prefix)) return "http://www.opengis.net/cat/csw/2.0.2";
        else if ("ows".equals(prefix)) return "http://www.opengis.net/ows";
        else if ("ogc".equals(prefix)) return "http://www.opengis.net/ogc";
        else if ("xlink".equals(prefix)) return "http://www.w3.org/1999/xlink";
        else if ("gml".equals(prefix)) return "http://www.opengis.net/gml";
        else if ("gmd".equals(prefix)) return "http://www.isotc211.org/2005/gmd";
        else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
        else if ("soapenv".equals(prefix)) return "http://www.w3.org/2003/05/soap-envelope";
		
        return XMLConstants.NULL_NS_URI;
	}

	/**
	 * @see javax.xml.namespace.NamespaceContext#getPrefix(java.lang.String)
	 */
	@Override
	public String getPrefix(String arg0) {
		// This method isn't necessary for XPath processing.
		throw new UnsupportedOperationException();
	}

	/**
	 * @see javax.xml.namespace.NamespaceContext#getPrefixes(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Iterator getPrefixes(String arg0) {
		// This method isn't necessary for XPath processing.
		throw new UnsupportedOperationException();
	}

}
