



/*
 * Created on 24.05.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package de.ingrid.interfaces.csw.exceptions;

/**
 * Von dieser Klasse werden alle moeglichen Ausnahmen des Catalog Service abgeleitet
 *
 * @author rschaefer
 */
public class CSWException extends Exception {
	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	static final long serialVersionUID = 0;

	/**
	 * Der exceptionCode des Exception reports
	 */
	private String exceptionCode = "NoApplicableCode";

	/**
	 * Der locator des Exception reports
	 */
	private String locator = null;

	/**
	 * Der exceptionText des Exception reports
	 */
	private String exceptionText = "CSWException";

	/**
	 * Konstruktor
	 * @param message String
	 */
	public CSWException(final String message) {
		super(message);
	}

	/**
	 * @return Returns the exceptionCode.
	 */
	public final String getExceptionCode() {
		return exceptionCode;
	}
	/**
	 * @param excCode The exceptionCode to set.
	 */
	public final void setExceptionCode(final String excCode) {
		this.exceptionCode = excCode;
	}
	/**
	 * @return Returns the exceptionText.
	 */
	public final String getExceptionText() {
		return exceptionText;
	}
	/**
	 * @param excText The exceptionText to set.
	 */
	public final void setExceptionText(final String excText) {
		this.exceptionText = excText;
	}
	/**
	 * @return Returns the locator.
	 */
	public final String getLocator() {
		return locator;
	}
	/**
	 * @param loc The locator to set.
	 */
	public final void setLocator(final String loc) {
		this.locator = loc;
	}
	
	/**
	 * Creates an error message in XML structure that complies to the schema which
	 * is defined for OWS exception reports, version 0.3.1 (see also
	 * http://schemas.opengis.net/ows/0.3.1/owsExceptionReport.xsd)
	 * 
	 * @param excText A descriptive exception text. If null or empty, it is not included.
	 * @param excCode The mandatory exception code.
	 * @param excLocator An optional error location text. If null or empty, it is not included.
	 * @return The complete XML text containing the error report
	 */
	public static String createXmlExceptionReport(String excText, String excCode, String excLocator) {
		StringBuffer result = new StringBuffer();
		
		result.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		result.append("<ExceptionReport xmlns=\"http://www.opengis.net/ows\"\n");
		result.append("xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n");
		result.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
		result.append("xsi:schemaLocation=\"http://www.opengis.net/ows owsCommon.xsd\"\n");
		result.append("version=\"2.0.0\" language=\"en\">\n");
		result.append("<Exception exceptionCode=\"" + excCode + "\"\n");
		if (excLocator != null && !excLocator.equals("")) {
			result.append("locator=\"" + excLocator + "\"\n");
		}
		result.append(">\n");
		result.append(excText + "\n");
		result.append("</Exception>\n");
		result.append("</ExceptionReport>\n");
		
		return result.toString();
	}
}
