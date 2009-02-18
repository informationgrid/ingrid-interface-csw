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
 * Copyright © 2004 GIStec GmbH                                               *
 * ALL Rights Reserved.                                                       *
 *                                                                            *
 *+---------------------------------------------------------------------------*
 *                                                                            *
 * Author           : Ralf Schaefer                                            *
 * Erstellungsdatum : 25.05.2004                                              *
 * Version          : 1.0                                                     *
 * Beschreibung     : stellt eine MissingParameterValueException dar.         *
 *                                                                            *
 *                                                                            *
 *----------------------------------------------------------------------------*
 * aenderungen (Datum, Version, Author, Beschreibung)                          *
 *----------------------------------------------------------------------------*
 *            |         |          |                                          *
 *            |         |          |                                          *
 *----------------------------------------------------------------------------*
*/


package de.ingrid.interfaces.csw.exceptions;



/**
 * Diese Exception wird geworfen, wenn ein benoetigter Parameter in der Anfrage fehlt.
 *  
 * @author rschaefer
 */
public class CSWMissingParameterValueException extends CSWException {
    
    /**
     * Comment for <code>serialVersionUID</code>
     */
    static final long serialVersionUID = 0;
	
	
	{ super.setExceptionCode("MissingParameterValue"); }
	
	
	/**
     * Konstruktor
	 * @param mess String	
	 * @param loc String
	 */
	public CSWMissingParameterValueException(final String mess, final String loc) {
	    super(mess);
		super.setExceptionText(mess);
		super.setLocator(loc);
	 }

}
