/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.Properties;

import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPElementFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;


/**
 * This class analyses a GetRecords request
 * @author rschaefer
 *
 */
public class GetRecAnalyser implements CSWAnalyser {
	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(GetRecAnalyser.class);

	/**
	 * stores values of the request
	 */
	private SessionParameters sessionParameters = null;

	/**
	 * Static object used to retrieve the config data
	 */
	private static CSWInterfaceConfig cswConfig = CSWInterfaceConfig.getInstance();

	/**
	 * constructor
	 * @param sessionParams SessionParameters
	 */
	public GetRecAnalyser(final SessionParameters sessionParams) {
		this.sessionParameters = sessionParams;
	}

	/**
	 * default constructor
	 */
	public GetRecAnalyser() {
	}

	/**
	 *
	* @param be SOAPBodyElement
	* @return boolean
	* @throws Exception e
	* @see com.gistec.ingeocsw.analyse.CSWAnalyser#analyse(javax.xml.soap.SOAPBodyElement)
	*/
	public final boolean analyse(final Element be) throws Exception {
		boolean getRecordsRequestValid = false;
		String opName = null;
		String startPosition = null;
		int startPositionInt = 1;
		String maxRecords = null;
		int maxRecordsInt = 0;
		CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);

		if (be == null) {
			throw new Exception("analyse: SOAPBodyElement is null.");
		}
		opName = be.getLocalName();

		if (opName == null) {
			throw new Exception("analyse: opName is null.");
		}
		
		if (!opName.equals(ClientRequestParameters.GETRECORDS)) {
			Exception e = new CSWOperationNotSupportedException("Operation '" + opName + "' is not supported.", opName);
			throw e;
		}
		commonAnalyser.analyseService(be);
		sessionParameters.setVersion(commonAnalyser.analyseVersion(be));
		commonAnalyser.analyseResultType(be);

		commonAnalyser.analyseOutputFormat(be);
		commonAnalyser.analyseOutputSchema(be);
		startPosition = be.getAttribute("startPosition");

		if (startPosition != null && !startPosition.equals("")) {
			startPositionInt = Integer.parseInt(startPosition);
			if (startPositionInt < 1) {
				Exception e =
					new CSWInvalidParameterValueException("Attribute 'startPosition' is invalid.", "startPosition");
				throw e;
			} else {
				sessionParameters.setStartPosition(startPositionInt);
			}
		}
		maxRecords = be.getAttribute("maxRecords");

		if (maxRecords != null && !maxRecords.equals("")) {
			maxRecordsInt = Integer.parseInt(maxRecords);

			if (maxRecordsInt < 1) {
				Exception e =
					new CSWInvalidParameterValueException("Attribute 'maxRecords' must be 1 or above.", "maxRecords");
				throw e;
			} else if (maxRecordsInt > Integer.parseInt(cswConfig.getString(CSWInterfaceConfig.MAX_RECORDS))) {
				// Silently limit the maxRecords request parameter to the value we configured
				maxRecordsInt = Integer.parseInt(cswConfig.getString(CSWInterfaceConfig.MAX_RECORDS));
				sessionParameters.setMaxRecords(maxRecordsInt);
				/*
				Exception e =
					new CSWInvalidParameterValueException("Attribute 'maxRecords' is invalid because " +
						" its value is greater than " +
						cswConfig.getString(CSWInterfaceConfig.MAX_RECORDS) +
						" (value of CSW configuration).", "maxRecords");
				throw e;
				*/
			} else {
				sessionParameters.setMaxRecords(maxRecordsInt);
			}
		} else {
			// Set the default value if parameter was not specified
			sessionParameters.setMaxRecords(10);
		}
		analyseQuery(be);
		commonAnalyser.analyseElementSetName(be);
		analyseConstraint(be);
		analyseFilter(be);
		getRecordsRequestValid = true;
		return getRecordsRequestValid;
	}

	/**
	 * analyse the query element,
	 * return true if ok.
	 * @param be SOAPBodyElement
	 * @return queryIsValid boolean
	 * @throws Exception e
	 */
	private boolean analyseQuery(final Element be) throws Exception {
		boolean queryIsValid = false;
		Element elemQuery = null;
		String typeNames = null;
		NodeList nl = be.getElementsByTagNameNS("http://www.opengis.net/cat/csw/2.0.2", "Query");

		if (nl != null && nl.getLength() > 0) {
			elemQuery = (Element) nl.item(0);
		}

		if (elemQuery != null) {
			typeNames = elemQuery.getAttribute("typeNames");
		} else {
			Exception e = new CSWMissingParameterValueException("Element 'Query' is missing.", "Query");
			throw e;
		}

		if (typeNames == null || typeNames.length() == 0) {
			Exception e =
				new CSWMissingParameterValueException("Attribute 'typeNames' is missing.", "typeNames");
			throw e;
		} else {
			//analyse typeNames: csw:dataset,csw:datasetcollection,csw:service,csw:application
			CommonAnalyser commonAnalyser = new CommonAnalyser(this.sessionParameters);

			if (!commonAnalyser.analyseTypeNames(typeNames)) {
				Exception e =
					new CSWInvalidParameterValueException("Attribute 'typeNames' is invalid.", "typeNames");
				throw e;
			}
		}
		return queryIsValid;
	}

	/**
	 * analyse the constraint element,
	 * return true if ok.
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	private boolean analyseConstraint(final Element be) throws Exception {
		Element elemConstraint = null;
		String constraintLangVersion = null;
		NodeList nl = be.getElementsByTagNameNS("http://www.opengis.net/cat/csw/2.0.2", "Constraint"); 

		if (nl != null || nl.getLength() != 0) {
			elemConstraint = (Element) nl.item(0);
		}

		if (elemConstraint != null) {
			constraintLangVersion = elemConstraint.getAttribute("version");
		} else {
			// no constraint element is given, this is a valid request though
			return false;
/*			Exception e =
				new CSWMissingParameterValueException("Element 'Constraint' is missing.", "Constraint");
			throw e;
*/			
		}

		if (constraintLangVersion != null) {
			//allow only Filter encoding 1.1.0
			if (!constraintLangVersion.equals("1.1.0")) {
				Exception e =
					new CSWInvalidParameterValueException("Attribute 'version' of Element 'Constraint' is not '1.0.0'.", "version");
				throw e;
			}
		} else {
			Exception e =
				new CSWMissingParameterValueException("Attribute 'version' of Element 'Constraint' is missing.", "version");
			throw e;
		}
		return true;
	}

	/**
	 * analyse the filter element,
	 * return true if ok.
	 * Allow only OGC-Filter. CqlText is not supported yet.
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	private boolean analyseFilter(final Element be) throws Exception {
		Element soapElementFilter = null;
		NodeList nl = be.getElementsByTagNameNS("http://www.opengis.net/ogc", "Filter");

		if (nl != null || nl.getLength() != 0) {
			soapElementFilter = (Element) nl.item(0);
		}

		if (soapElementFilter != null) {
			// test if namespace is correct
			String nameSpaceURI = soapElementFilter.getNamespaceURI();

			if (nameSpaceURI != null &&
				!"".equals(nameSpaceURI) &&
				!Namespaces.OGC.equals(nameSpaceURI)) {
				Exception e =
					new CSWOperationNotSupportedException("Filter within namespace URI '" + nameSpaceURI +
						"' is not supported.", "Filter");
				throw e;
				}
			sessionParameters.setSoapElementFilter(soapElementFilter);
		} else {
			// no filter is set, this query is valid though.
			sessionParameters.setSoapElementFilter(null);
			return false;
		}
		return true;
	}

	/**
	 * Checks if the given request parameters are correct for performing a
	 * GetRecords action.
	 * Currently, no check is made on the existence and correctness of the
	 * parameters 'REQUEST' and 'SERVICE'. This has to be done before.
	 * 
	 * Currently checks:
	 * - VERSION (mandatory)
	 * - TYPENAMES (mandatory): Service + Dataset + Datasetcollection + Application
	 * - RESULTTYPE (optional): HITS, RESULTS, VALIDATE
	 *             if HITS: ignore ELEMENT(SET)NAME
	 *             if RESULTS: ELEMENT(SET)NAME (optional)
	 * - ELEMENT(SET)NAME (optional): brief, summary, full
	 * - CONSTRAINT (optional)
	 *             - CONSTRAINTLANGUAGE (mandatory with CONSTRAINT): CQL_TEXT, FILTER
	 *             - CONSTRAINT_LANGUAGE_VERSION (mandatory with CONSTRAINT)
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
		
		// Check TYPENAMES parameter
		String param = reqParams.getProperty(ClientRequestParameters.TYPENAMES, null);
		if (param == null || param.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.TYPENAMES + "' is not specified or has no value", ClientRequestParameters.TYPENAMES);
		} else {
			// The value must be a comma-separated string with at least one valid value
			String[] tnValues = param.toUpperCase().split(","); // needed for case insensitve comparison
			for (int i=0; i<tnValues.length; i++) {
				if (!ClientRequestParameters.TYPENAMES_VALUES.contains(tnValues[i])) {
					StringBuffer errorMsg = new StringBuffer();
					errorMsg.append("Parameter '" + ClientRequestParameters.TYPENAMES + "' has an unsupported value.\n");
					errorMsg.append("Supported values: at least one of ");
					for (int j=0; j<ClientRequestParameters.TYPENAMES_VALUES.size(); j++) {
						errorMsg.append("'" + ClientRequestParameters.TYPENAMES_VALUES.get(j) + "' ");
					}
					errorMsg.append("\n");
					throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.TYPENAMES);
				}
			}
		}
		
		// Check RESULTTYPE parameter
		param = reqParams.getProperty(ClientRequestParameters.RESULTTYPE, null);
		// This parameter is optional, so a check is only necessary if specified
		if (param != null) {
			if (param.equals("")) {
				throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.RESULTTYPE + "' has no value", ClientRequestParameters.RESULTTYPE);
			} else {
				param = param.toUpperCase(); // needed for case insensitve comparison
				if (!ClientRequestParameters.RESULTTYPE_VALUES.contains(param)) {
					StringBuffer errorMsg = new StringBuffer();
					errorMsg.append("Parameter '" + ClientRequestParameters.RESULTTYPE + "' has an unsupported value.\n");
					errorMsg.append("Supported values: one of ");
					for (int j=0; j<ClientRequestParameters.RESULTTYPE_VALUES.size(); j++) {
						errorMsg.append("'" + ClientRequestParameters.RESULTTYPE_VALUES.get(j) + "' ");
					}
					errorMsg.append("\n");
					throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.RESULTTYPE);
				}
				// Check ELEMENTNAME parameter
				// Only one parameter, ELEMENTNAME or ELEMENTSETNAME, need to be specified (if specified at all) - not both.
				// A check on both parameters is necessary to find illegal values.
				param = reqParams.getProperty(ClientRequestParameters.ELEMENTNAME, null);
				
				// This parameter is optional, so a check is only necessary if specified
				if (param != null) {
					if (param.equals("")) {
						throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.ELEMENTNAME + "' has no value", ClientRequestParameters.ELEMENTNAME);
					} else {
						param = param.toUpperCase(); // needed for case insensitve comparison
						if (!ClientRequestParameters.ELEMENTNAME_VALUES.contains(param)) {
							StringBuffer errorMsg = new StringBuffer();
							errorMsg.append("Parameter '" + ClientRequestParameters.ELEMENTNAME + "' has an unsupported value.\n");
							errorMsg.append("Supported values: one of ");
							for (int j=0; j<ClientRequestParameters.ELEMENTNAME_VALUES.size(); j++) {
								errorMsg.append("'" + ClientRequestParameters.ELEMENTNAME_VALUES.get(j) + "' ");
							}
							errorMsg.append("\n");
							throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.ELEMENTNAME);
						}
					}
				}
				// Check ELEMENTSETNAME parameter
				param = reqParams.getProperty(ClientRequestParameters.ELEMENTSETNAME, null);
				// This parameter is optional, so a check is only necessary if specified
				if (param != null) {
					if (param.equals("")) {
						throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.ELEMENTSETNAME + "' has no value", ClientRequestParameters.ELEMENTSETNAME);
					} else {
						param = param.toUpperCase(); // needed for case insensitve comparison
						if (!ClientRequestParameters.ELEMENTNAME_VALUES.contains(param)) {
							StringBuffer errorMsg = new StringBuffer();
							errorMsg.append("Parameter '" + ClientRequestParameters.ELEMENTSETNAME + "' has an unsupported value.\n");
							errorMsg.append("Supported values: one of ");
							for (int j=0; j<ClientRequestParameters.ELEMENTNAME_VALUES.size(); j++) {
								errorMsg.append("'" + ClientRequestParameters.ELEMENTNAME_VALUES.get(j) + "' ");
							}
							errorMsg.append("\n");
							throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.ELEMENTSETNAME);
						}
					}
				}
			}
		}
		
		
		
		// Check CONSTRAINT parameter
		param = reqParams.getProperty(ClientRequestParameters.CONSTRAINT, null);
		// This parameter is optional, so a check is only necessary if specified
		if (param != null) {
			if (param.equals("")) {
				throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.CONSTRAINT + "' has no value", ClientRequestParameters.CONSTRAINT);
			} else {
				// Check conditional CONSTRAINT*** parameters
				param = reqParams.getProperty(ClientRequestParameters.CONSTRAINTLANGUAGE, null);
				if (param == null || param.equals("")) {
					throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.CONSTRAINTLANGUAGE + "' is not specified or has no value", ClientRequestParameters.CONSTRAINTLANGUAGE);
				} else {
					param = param.toUpperCase(); // needed for case insensitve comparison
					if (!ClientRequestParameters.CONSTRAINTLANGUAGE_VALUES.contains(param)) {
						StringBuffer errorMsg = new StringBuffer();
						errorMsg.append("Parameter '" + ClientRequestParameters.CONSTRAINTLANGUAGE + "' has an unsupported value.\n");
						errorMsg.append("Supported values: one of ");
						for (int j=0; j<ClientRequestParameters.CONSTRAINTLANGUAGE_VALUES.size(); j++) {
							errorMsg.append("'" + ClientRequestParameters.CONSTRAINTLANGUAGE_VALUES.get(j) + "' ");
						}
						errorMsg.append("\n");
						throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.CONSTRAINTLANGUAGE);
					}
				}
				param = reqParams.getProperty(ClientRequestParameters.CONSTRAINTLANGUAGEVERSION, null);
				if (param == null || param.equals("")) {
					throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.CONSTRAINTLANGUAGEVERSION + "' is not specified or has no value", ClientRequestParameters.CONSTRAINTLANGUAGEVERSION);
				}
			}
		}
		return true; // only if not cancelled by an exception before
	}
}
