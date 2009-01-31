/*
 * Created on 29.09.2005
 *
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.Properties;

import javax.xml.soap.SOAPBodyElement;

import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;


/**
 *
 * This class analyses a DescribeRecord request
 * @author rschaefer
 *
 */
public class DescRecAnalyser implements CSWAnalyser {
	/**
	 *
	* @param be SOAPBodyElement
	* @return boolean
	* @throws Exception e
	* @see com.gistec.ingeocsw.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
	*/
	public final boolean analyse(final SOAPBodyElement be) throws Exception {
		boolean descRecRequestValid = false;
		String opName = null;

		CommonAnalyser commonAnalyser = new CommonAnalyser(null);

		if (be == null) {
			throw new Exception("analyse: SOAPBodyElement is null.");
		}
		opName = be.getElementName().getLocalName();

		if (opName == null) {
			throw new Exception("analyse: opName is null.");
		}

		if (!opName.equals("DescribeRecord")) {
			Exception e = new CSWOperationNotSupportedException("Operation " + opName + " is not supported.", opName);
			throw e;
		}
		commonAnalyser.analyseService(be);
		commonAnalyser.analyseVersion(be);
		commonAnalyser.analyseSchemaLanguage(be);
		descRecRequestValid = true;
		return descRecRequestValid;
	}

	/**
	 * Checks if the given request parameters are correct for performing a
	 * DescribeRecord action.
	 * Currently, no check is made on the existence and correctness of the
	 * parameters 'REQUEST' and 'SERVICE'. This has to be done before.
	 * 
	 * Currently checks:
	 * - VERSION
	 * 
	 * @param reqParams The request parameters as a Properties object
	 * @return true if the check was ok
	 * @throws Exception if a check failed
	 * @see de.ingrid.interfaces.csw.analyse.CSWAnalyser#analyse(java.util.Properties)
	 */
	public final boolean analyse(final Properties reqParams)
	throws Exception {
		// Check the value of the VERSION parameter. An exception is thrown in case
		// of an improper value, so no further checking is needed here.
		CommonAnalyser.analyseVersion(reqParams);
		return true; // only if not cancelled by an exception before
	}
}
