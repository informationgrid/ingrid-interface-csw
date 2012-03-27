/*----------------------------------------------------------------------------*
 *          @@@@@      @@@       @@@@@                                        *
 *      @@@@@@@@@@@    @@@@    @@@@@@@@        @                              *
 *     @@@@@@@@@@@@    @@@@   @@@@@@@@@     @@@@                              *
 *    @@@@@            @@@@  @@@@           @@@@                              *
 *   @@@@@             @@@@  @@@@@        @@@@@@@@@   @@@@@@@@      @@@@@@@   *
 *   @@@@    @@@@@@@   @@@@   @@@@@@@     @@@@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *   @@@@   @@@@@@@@   @@@@    @@@@@@@@     @@@@    @@@@    @@@   @@@@        *
 *   @@@@    @@@@@@@   @@@@      @@@@@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *   @@@@@      @@@@   @@@@         @@@@    @@@@    @@@@@@@@@@@@ @@@@         *
 *    @@@@@     @@@@   @@@@   @     @@@@    @@@@    @@@@      @   @@@@        *
 *     @@@@@@@@@@@@@   @@@@   @@@@@@@@@@    @@@@@@@  @@@@@@@@@@   @@@@@@@@@   *
 *       @@@@@@@@@@@   @@@@   @@@@@@@@       @@@@@@   @@@@@@@@@     @@@@@@@   *
 *                            Neue Wege mit GIS                               *
 *                                                                            *
 * Fraunhoferstr. 5                                                           *
 * D-64283 Darmstadt                                                          *
 * info@gistec-online.de                          http://www.gistec-online.de *
 *----------------------------------------------------------------------------*
 *                                                                            *
 * Copyright  2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schaefer                                            *
 * Erstellungsdatum : 06.06.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     : Die Klasse stellt eine Ausnahme im Filterencoding dar.  *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * aenderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
 */



package de.ingrid.interfaces.csw.domain.exceptions;

/**
 * Die Klasse stellt eine Ausnahme im Filterencoding dar.
 * @author rschaefer
 */
public class CSWFilterException extends CSWException {

	/**
	 * Comment for <code>serialVersionUID</code>
	 */
	static final long serialVersionUID = 0;



	/**
	 * Comment for <code>message</code>
	 */
	@SuppressWarnings("unused")
	private String message = "CSWFilterException";


	/**
	 * Konstruktor
	 * @param mess String
	 */
	public CSWFilterException(final String mess) {
		super(mess);
		this.message = mess;
	}


	/*
    public String toString() {
    	return message + "\n" + getLocalizedMessage();
    }
	 */
}

