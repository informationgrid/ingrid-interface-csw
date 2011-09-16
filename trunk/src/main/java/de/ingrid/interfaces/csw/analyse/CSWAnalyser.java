/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.Properties;

import org.w3c.dom.Element;

/**
 * interface for OGC CSW analysers
 * @author rschaefer
 */
public interface CSWAnalyser {
	/**
	 * analyse the SOAP message
	 * and collect some values.
	 * return true if ok.
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	boolean analyse(final Element be) throws Exception;

	/**
	 * Checks if the given request parameters are correct.
	 * 
	 * @param reqParams The request parameters as a Properties object
	 * @return true if the check was ok
	 * @throws Exception if a check failed
	 */
	boolean analyse(final Properties reqParams) throws Exception;
}