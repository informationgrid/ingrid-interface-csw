package de.ingrid.interfaces.csw2.tools;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Diese Klasse stellt Hilfsfunktionen fuer XML zur Verfuegung  
 */
public final class XMLTools {

	private static Log log = LogFactory.getLog(XMLTools.class);

	/**
	 * Constructor
	 */
	private XMLTools() {}

	/**
	 * Create a an empty DOM document. Uses the default DOM implementation.
	 * @return Document 
	 * @throws Exception e
	 */
	public static Document create() throws Exception {

		javax.xml.parsers.DocumentBuilder builder = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			builder = dbf.newDocumentBuilder();
		} catch (Exception e) {
			log.error("Error creating document: " + e.getMessage(), e);
			throw e;
		}
		return builder.newDocument();
	}

	/**
	 * Parse an XML document provided by a reader into a DOM Document. 
	 * Uses the default DOM implementation.
	 * @param reader Reader
	 * @return Document
	 * @throws IOException
	 */
	public static Document parse(final Reader reader) throws Exception {

        if (log.isDebugEnabled()) {
        	log.debug("entering parse(reader)...");
        }

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setIgnoringComments(true);
        //factory.setValidating(true);
		//factory.setIgnoringElementContentWhitespace(true);
		
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new IOException("Unable to initialize DocumentBuilder: "+e.getMessage());
		}

		Document doc = null;
		try {
			doc = builder.parse(new InputSource(reader));
		} catch (SAXException e) {
			log.error("parse(reader) SAXException: "+e, e);
			throw e;
		} catch (IOException e) {
			log.error("parse(reader) IOException: "+e, e);
			throw e;
		}

        if (log.isDebugEnabled()) {
			log.debug("exiting parse(reader) returning document: "+
					doc.getDocumentElement().toString());
        }
		return doc;
	}

	/**
	 * Create a string representation of a DOM Document
	 * @param document
	 * @return String
	 * @throws TransformerException
	 */
	public static String toString(Document document) throws TransformerException {
        return XMLTools.toString(document.getDocumentElement());
	}
	
	/**
	 * Create a string representation of a DOM Node
	 * @param node
	 * @return String
	 * @throws TransformerException
	 */
	public static String toString(Node node) throws TransformerException {
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(new DOMSource(node), streamResult);
        return stringWriter.toString();
	}

	/**
	 * TODO: review methods below and check if they are used (remove if not).
	 */
	
	/**
	 * Returns the attribute value of the given node.
	 *
	 *
	 * @param node Node
	 * @param attrName String
	 *
	 * @return String

	 */
	public static String getAttrValue(final Node node, final String attrName) {

		// get attr name and dtype
		NamedNodeMap atts = node.getAttributes();

		if (atts == null) {
			return null;
		}

		Attr a = (Attr) atts.getNamedItem(attrName);

		if (a != null) {
			return a.getValue();
		}

		return null;
	}

	/**
	 * copies one node to another node (of a different dom document).
	 * @param source Node
	 * @param dest Node
	 * @return Node
	 */
	public static Node copyNode(final Node source, final Node dest) {
		if (source.getNodeType() == Node.TEXT_NODE) {
			Text tn = dest.getOwnerDocument().createTextNode(
					source.getNodeValue());
			return tn;
		} else {
			NamedNodeMap attr = source.getAttributes();

			if (attr != null) {
				for (int i = 0; i < attr.getLength(); i++) {
					((Element) dest).setAttribute(attr.item(i).getNodeName(),
							(String) attr.item(i).getNodeValue());
				}
			}

			NodeList list = source.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {

				if (!(list.item(i) instanceof Text)) {
					Element en = dest.getOwnerDocument().createElementNS(list.item(i).getNamespaceURI(),
							list.item(i).getNodeName());
					if (list.item(i).getNodeValue() != null) {
						en.setNodeValue(list.item(i).getNodeValue());
					}

					Node n = copyNode(list.item(i), en);
					dest.appendChild(n);
				} else {
					Text tn = dest.getOwnerDocument().createTextNode(
							list.item(i).getNodeValue());
					dest.appendChild(tn);
				}
			}
		}
		return dest;

	}
	
	/**
	 * inserts a node into a dom element (of a different dom document)
	 * 
	 * @param source Node 
	 * @param dest Node
	 * @return Node
	 */
	public static Node insertNodeInto(final Node source, final Node dest) {

		Document dDoc = null;
		Document sDoc = source.getOwnerDocument();
		if (dest instanceof Document) {
			dDoc = (Document) dest;
		} else {
			dDoc = dest.getOwnerDocument();
		}

		if (dDoc.equals(sDoc)) {
			dest.appendChild(source);
		} else {

			Element element = dDoc.createElement(source.getNodeName());
			dest.appendChild(element);

			copyNode(source, element);

		}

		return dest;

	}

	
	
	/**
	 * returns the first child element of the submitted node
	 * 
	 * @param node Node
	 * @return Element
	 */
	public static Element getFirstElement(final Node node) {

		NodeList nl = node.getChildNodes();
		Element element = null;

		if (nl != null && nl.getLength() > 0) {

			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i) instanceof Element) {
					element = (Element) nl.item(i);
					break;
				}
			}

		}

		return element;
	}

	
	

	/**
	 * 
	 * removes all direct child nodes of the submitted node
	 * with the also submitted name
	 *
	 * @param node Node
	 * @param nodeName String
	 * @return Node
	 */
	public static Node removeNamedChildNodes(final Node node, final String nodeName) {

		NodeList nl = node.getChildNodes();

		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i).getNodeName().equals(nodeName)) {
					node.removeChild(nl.item(i));
				}
			}
		}

		return node;
	}


	
	/**
	 * returns the first child element of the submitted node
	 * @param node Node
	 * @param name String
	 * @return Element
	 */
	public static synchronized Element getNamedChild(final Node node, final String name) {
	

		NodeList nl = node.getChildNodes();
		Element element = null;
		Element elemReturn = null;

		if (nl != null && nl.getLength() > 0) {

			for (int i = 0; i < nl.getLength(); i++) {
				if (nl.item(i) instanceof Element) {
					element = (Element) nl.item(i);
					if (element.getNodeName().equals(name)) {
						elemReturn = element;
						break;
					}
				}
			}
		}

		
		return elemReturn;
	}

	
	
	/**
	 * extracts the local name from a node name
	 * @param ndName String
	 * @return String
	 */
	public static String toLocalName(final String ndName) {
		
		String nodeName = ndName;
		
		int pos = nodeName.lastIndexOf(':');
		
		if (pos > -1) {
		
			nodeName = nodeName.substring(pos + 1, nodeName.length());
		
		}
		
		return nodeName;
	}
}
