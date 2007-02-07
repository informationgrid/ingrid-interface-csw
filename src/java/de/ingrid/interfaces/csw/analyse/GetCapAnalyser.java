/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.Properties;

import javax.xml.soap.SOAPBodyElement;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.exceptions.CSWVersionNegotiationFailedException;


/**
 * This class analyses a GetCapabilities request
 * @author rschaefer
 *
 */
public class GetCapAnalyser implements CSWAnalyser {
	/**
	 *
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 * @see com.gistec.ingeocsw.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
	 */
	public final boolean analyse(final SOAPBodyElement be) throws Exception {
		boolean getCapRequestValid = false;
		String opName = null;
		CommonAnalyser commonAnalyser = new CommonAnalyser(null);

		if (be == null) {
			throw new Exception("analyse: SOAPBodyElement is null.");
		}
		opName = be.getElementName().getLocalName();

		if (opName == null) {
			throw new Exception("analyse: opName is null.");
		}

		if (!opName.equals("GetCapabilities")) {
			Exception e = new CSWOperationNotSupportedException("Operation " + opName + " is not supported.", opName);
			throw e;
		}
		commonAnalyser.analyseService(be);
		Element elemAccVer = (Element) be.getFirstChild();

		//if this element is not present, take version 2.0
		if (elemAccVer != null) {
			NodeList nl = elemAccVer.getChildNodes();
			Element elemVerCurr = null;
			String version = null;
			boolean versionNegotiationSuccess = false;
			int nlLength = nl.getLength();

			for (int i = 0; i < nlLength; i++) {
				elemVerCurr = (Element) nl.item(i);
				version = elemVerCurr.getFirstChild().getNodeValue();

				if (version.equals("2.0.0")) {
					versionNegotiationSuccess = true;
					break;
				}
			}

			if (!versionNegotiationSuccess) {
				Exception e = new CSWVersionNegotiationFailedException("All requested versions are not '2.0.0'.");
				throw e;
			}
		}
		getCapRequestValid = true;
		return getCapRequestValid;
	}

	/**
	 * Checks if the given request parameters are correct for performing a
	 * GetCapabilities action.
	 * Currently, no check is made on the existence and correctness of the
	 * parameters 'REQUEST' and 'SERVICE'. This has to be done before.
	 * 
	 * Currently checks:
	 * - ACCEPTVERSIONS includes "2.0.0" (optional)
	 * The values are checked with case sensitivity.
	 * 
	 * @param reqParams The request parameters as a Properties object
	 * @return true if the check was ok
	 * @throws Exception if a check failed
	 * @see de.ingrid.interfaces.csw.analyse.CSWAnalyser#analyse(java.util.Properties)
	 */
	public final boolean analyse(final Properties reqParams)
	throws Exception {		
		// Check ACCEPTVERSIONS parameter - does it contain version 2.0.0?
		String param = reqParams.getProperty(ClientRequestParameters.ACCEPTVERSIONS, null);
		if (param != null && param.indexOf("2.0.0") == -1) {
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("Parameter 'ACCEPTVERSIONS' has an unsupported value.\n");
			errorMsg.append("Supported values: 2.0.0\n");
			throw new CSWVersionNegotiationFailedException(errorMsg.toString());
		}
		return true; // only if not cancelled by an exception before
	}
}
