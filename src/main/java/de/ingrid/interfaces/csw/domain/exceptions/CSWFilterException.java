package de.ingrid.interfaces.csw.domain.exceptions;

/**
 * Die Klasse stellt eine Ausnahme im Filterencoding dar.
 * @author rschaefer
 */
public class CSWFilterException extends CSWException {

	static final long serialVersionUID = 0;

	@SuppressWarnings("unused")
	private String message = "CSWFilterException";

	/**
	 * Constructor
	 * @param message
	 * @param code
	 * @param locator
	 */
	public CSWFilterException(final String message, final String code, final String locator) {
		super(message, code, locator);
	}

	/**
	 * Constructor
	 * @param message
	 */
	public CSWFilterException(final String message) {
		super(message);
		this.message = message;
	}
}

