package de.ingrid.interfaces.csw.tools;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

public class SoapNamespaceContext implements NamespaceContext {

	public static String NAMESPACE_URI_SOAP = "http://www.w3.org/2003/05/soap-envelope";

	@Override
	public String getNamespaceURI(String prefix) {
		if (prefix.equals("soapenv")) {
			return NAMESPACE_URI_SOAP;
		} else {
			return XMLConstants.NULL_NS_URI;
		}
	}

	@Override
	public String getPrefix(String namespaceURI) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<?> getPrefixes(String namespaceURI) {
		throw new UnsupportedOperationException();
	}
}
