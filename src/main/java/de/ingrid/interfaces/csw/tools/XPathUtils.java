package de.ingrid.interfaces.csw.tools;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathUtils {

	final protected static Log log = LogFactory.getLog(XPathUtils.class);

	private static XPath xpath = null;
	
	private static NamespaceContext namespaceContext = null;

	private XPathUtils() {}

	public static XPath getXPathInstance() {
		if (xpath == null) {
			xpath = createNewXPathInstance();
			xpath.setNamespaceContext(XPathUtils.getNamespaceContext());
		}

		return xpath;
	}
	
	public static NamespaceContext getNamespaceContext() {
		if (namespaceContext == null) {
			namespaceContext = new CSWNamespaceContext();
		}
		return namespaceContext;
	}

	private static XPath createNewXPathInstance() {
		return XPathFactory.newInstance().newXPath();
	}

	public static Integer getInt(Object source, String xpathExpression) {
		String value = getString(source, xpathExpression);

		if (value != null) {
			return Integer.valueOf(value);

		} else {
			return null;
		}
	}

	public static Double getDouble(Object source, String xpathExpression) {
		String value = getString(source, xpathExpression);

		if (value != null) {
			return Double.valueOf(value);

		} else {
			return null;
		}
	}

	public static Long getLong(Object source, String xpathExpression) {
		String value = getString(source, xpathExpression);

		if (value != null) {
			return Long.valueOf(value);

		} else {
			return null;
		}
	}

	public static boolean nodeExists(Object source, String xpathExpression) {
		try {
			if (source != null) {
				XPath xpath = getXPathInstance();
				Boolean exists = (Boolean) xpath.evaluate(xpathExpression, source, XPathConstants.BOOLEAN);
				return exists;
			}

		} catch (XPathExpressionException ex) {
			// Log the exception and continue.
			log.error("Error evaluating xpath expression: '"+xpathExpression+"'", ex);
		}

		// Source document was null. Return false
		return false;
	}

	public static String getString(Object source, String xpathExpression) {
		try {
			if (source != null) {
				XPath xpath = getXPathInstance();
				Node node = (Node) xpath.evaluate(xpathExpression, source, XPathConstants.NODE);
				if (node != null) {
					return node.getTextContent();
				}
			}

		} catch (XPathExpressionException ex) {
			// Log the exception and continue.
			log.error("Error evaluating xpath expression: '"+xpathExpression+"'", ex);
		}

		// Something went wrong. Either the source document was null or the string for xpathExpression could not be found
		// In either case return null
		return null;
	}

	public static Node getNode(Object source, String xpathExpression) {
		try {
			if (source != null) {
				XPath xpath = getXPathInstance();
				Node node = (Node) xpath.evaluate(xpathExpression, source, XPathConstants.NODE);
				return node;
			}

		} catch (XPathExpressionException ex) {
			// Log the exception and continue.
			log.error("Error evaluating xpath expression: '"+xpathExpression+"'", ex);
		}

		// Something went wrong. Either the source document was null or the xpathExpression could not be found
		// In either case return null
		return null;
	}

	public static NodeList getNodeList(Object source, String xpathExpression) {
		try {
			if (source != null) {
				XPath xpath = getXPathInstance();
				NodeList nodeList = (NodeList) xpath.evaluate(xpathExpression, source, XPathConstants.NODESET);
				return nodeList;
			}

		} catch (XPathExpressionException ex) {
			// Log the exception and continue.
			log.error("Error evaluating xpath expression: '"+xpathExpression+"'", ex);
		}

		// Something went wrong. Either the source document was null or the xpathExpression could not be found
		// In either case return null
		return null;
	}
}
