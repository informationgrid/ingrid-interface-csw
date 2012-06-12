package de.ingrid.interfaces.csw.domain.exceptions;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Von dieser Klasse werden alle moeglichen Ausnahmen des Catalog Service abgeleitet
 *
 * @author rschaefer
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWException extends Exception {

	static final long serialVersionUID = 0;

	private String code = "NoApplicableCode";
	private String locator = null;

	/**
	 * Constructor
	 * @param message String
	 */
	public CSWException(final String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param message String
	 * @param cause Throwable
	 */
	public CSWException(final String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message
	 * @param code
	 * @param locator
	 */
	public CSWException(final String message, final String code, final String locator) {
		super(message);
		this.code = code;
		this.locator = locator;
	}

	/**
	 * Get the exception code
	 * @return String
	 */
	public final String getCode() {
		return this.code;
	}

	/**
	 * Set the exception code
	 * @param code The code to set
	 */
	public final void setCode(final String code) {
		this.code = code;
	}

	/**
	 * Get the exception locator
	 * @return String
	 */
	public final String getLocator() {
		return this.locator;
	}

	/**
	 * Set the exception locator
	 * @param loc The locator to set
	 */
	public final void setLocator(final String loc) {
		this.locator = loc;
	}

	/**
	 * Conversions to XML format
	 */

	/**
	 * Creates an error message in XML structure that complies to the schema which
	 * is defined for OWS exception reports, version 0.3.1 (see also
	 * http://schemas.opengis.net/ows/0.3.1/owsExceptionReport.xsd)

	 * @return The complete XML document containing the error report
	 * @throws Exception
	 */
	public Document toXmlExceptionReport() throws Exception {
		Document reportDoc = this.createDocument();

		Element rootElement = reportDoc.createElementNS("http://www.opengis.net/ows", "ExceptionReport");
		reportDoc.appendChild(rootElement);

		Attr versionAttribute = reportDoc.createAttribute("version");
		versionAttribute.setNodeValue("1.0.0");
		rootElement.setAttributeNode(versionAttribute);

		Attr languageAttribute = reportDoc.createAttribute("language");
		languageAttribute.setNodeValue("en");
		rootElement.setAttributeNode(languageAttribute);

		Element exceptionElement = reportDoc.createElementNS("http://www.opengis.net/ows", "Exception");
		rootElement.appendChild(exceptionElement);

		Attr exceptionCodeAttribute = reportDoc.createAttribute("exceptionCode");
		exceptionCodeAttribute.setNodeValue(this.code);
		exceptionElement.setAttributeNode(exceptionCodeAttribute);

		if (this.locator != null) {
			Attr locatorAttribute = reportDoc.createAttribute("locator");
			locatorAttribute.setNodeValue(this.locator);
			exceptionElement.setAttributeNode(locatorAttribute);
		}
		exceptionElement.setTextContent(this.getMessage());

		return reportDoc;
	}

	/**
	 * Creates an error message in SOAP Fault XML structure that complies to the schema which
	 * is defined by the W3C (see also http://www.w3.org/TR/soap12-part1/#soapfault)

	 * @return The complete XML document containing the error report
	 * @throws Exception
	 */
	public Document toSoapExceptionReport() throws Exception {
		Document reportDoc = this.createDocument();

		Element faultElement = reportDoc.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Fault");
		reportDoc.appendChild(faultElement);

		Element codeElement = reportDoc.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Code");
		faultElement.appendChild(codeElement);
		Element codeValueElement = reportDoc.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Value");
		// this is not a valid code (see http://www.w3.org/TR/soap12-part1/#faultcodes)
		codeValueElement.setTextContent("unknown");
		codeElement.appendChild(codeValueElement);

		Element reasonElement = reportDoc.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Reason");
		faultElement.appendChild(reasonElement);
		Element reasonTextElement = reportDoc.createElement("Text");
		reasonTextElement.setTextContent("exceptionText: "+this.getMessage()+
				" exceptionCode: "+this.code+" locator: "+this.locator);
		Attr langAttribute = reportDoc.createAttribute("xml:lang");
		langAttribute.setNodeValue("en-US");
		reasonTextElement.setAttributeNode(langAttribute);
		reasonElement.appendChild(reasonTextElement);

		// add the xml report as Detail
		Element detailElement = reportDoc.createElementNS("http://www.w3.org/2003/05/soap-envelope", "Detail");
		detailElement.appendChild(reportDoc.adoptNode(this.toXmlExceptionReport().getLastChild()));
		faultElement.appendChild(detailElement);

		return reportDoc;
	}

	/**
	 * Create an xml document
	 * @return Document
	 * @throws Exception
	 */
	protected Document createDocument() throws Exception {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbf.newDocumentBuilder();
		Document document = docBuilder.newDocument();
		return document;
	}
}
