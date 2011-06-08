/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.analyse;

import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.xml.soap.SOAPBodyElement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.ingrid.interfaces.csw.CSW;
import de.ingrid.interfaces.csw.exceptions.CSWInvalidParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWMissingParameterValueException;
import de.ingrid.interfaces.csw.exceptions.CSWOperationNotSupportedException;



/**
 * This class does an analysis of different elements of OGC requests and puts some of
 * the values into the sessionParameters.
 * @author rschaefer
 *
 */
public final class CommonAnalyser {
	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(CommonAnalyser.class);

	/**
	 * stores some values of the requests
	 */
	private SessionParameters sessionParameters = null;

	/**
	 * constructor
	 * @param sessionParams SessionParameters
	 */
	public CommonAnalyser(final SessionParameters sessionParams) {
		this.sessionParameters = sessionParams;
	}

	/**
	 * analyse the name of the operation, return true if ok
	 * @param opName String
	 * @return boolean
	 * @throws Exception e
	 */
	
	public boolean analyseOperationName(final String opName) throws Exception {
		boolean opNameIsValid = false;

		if (opName.equals(ClientRequestParameters.GETCAPABILITIES)) {
			opNameIsValid = true;
			sessionParameters.setOperationIsGetCap(true);
		} else if (opName.equals(ClientRequestParameters.GETRECORDS)) {
			opNameIsValid = true;
			sessionParameters.setOperationIsGetRecs(true);
		} else if (opName.equals(ClientRequestParameters.GETRECORDBYID)) {
			opNameIsValid = true;
			sessionParameters.setOperationIsGetRecById(true);
		} else if (opName.equals(ClientRequestParameters.DESCRIBERECORD)) {
			opNameIsValid = true;
			sessionParameters.setOperationIsDescRec(true);
		}
		return opNameIsValid;
	}
	
	/**
	 * Checks wether the mandatory parameter REQUEST was set to a proper value
	 * in the query. Returns the value of the parameter if yes, throws an exception otherwise.
	 * 
	 * @param reqParams Contains the request parameters and values
	 * @return true if the check was ok
	 * @throws CSW***Exception if an error occurred
	 */
	public static String analyseOperation(Properties reqParams) throws Exception {
		String result = reqParams.getProperty(ClientRequestParameters.REQUEST, null);
		
		if (result == null || result.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.REQUEST + "' is not specified or has no value", ClientRequestParameters.REQUEST);
		} else {
			if (!result.equals(ClientRequestParameters.GETCAPABILITIES) &&
					!result.equals(ClientRequestParameters.DESCRIBERECORD) &&
					!result.equals(ClientRequestParameters.GETRECORDS) &&
					!result.equals(ClientRequestParameters.GETRECORDBYID)) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter '" + ClientRequestParameters.REQUEST + "' has an unsupported value.\n");
				errorMsg.append("Supported values:\n");
				errorMsg.append(ClientRequestParameters.GETCAPABILITIES + "\n");
				errorMsg.append(ClientRequestParameters.DESCRIBERECORD + "\n");
				errorMsg.append(ClientRequestParameters.GETRECORDS + "\n");
				errorMsg.append(ClientRequestParameters.GETRECORDBYID + "\n");
				throw new CSWOperationNotSupportedException(errorMsg.toString(), ClientRequestParameters.REQUEST);
			}
		}
		return result;
	}

	/**
	 * analyse the id(s), return true if ok
	 * @param ids String
	 * @return boolean
	 */
	public boolean analyseIds(final String ids) {
		boolean idsIsValid = false;
		StringTokenizer stringTokenizer = new StringTokenizer(ids, ", ");
		ArrayList idsList = new ArrayList();
		while (stringTokenizer.hasMoreTokens()) {
			idsList.add(stringTokenizer.nextToken());
		}
		if (idsList.size() > 0) {
			idsIsValid = true;
			sessionParameters.setIdsList(idsList);
		}
		// no matter if it is invalid
		sessionParameters.setIds(ids);
		return idsIsValid;
	}

	/**
	 * analyse the type names, return true if ok
	 * @param typeNames String
	 * @return boolean
	 */
	public boolean analyseTypeNames(final String typeNames) {
		boolean typeNamesIsValid = false;
		String typeNameCurrent = null;
/*
 * The definition of the queried type is only to be defined via the type property in the filter query
 *
 */
		StringTokenizer stringTokenizer = new StringTokenizer(typeNames, ",");
		while (stringTokenizer.hasMoreTokens()) {
			typeNameCurrent = stringTokenizer.nextToken().trim();
			if (typeNameCurrent.equalsIgnoreCase("csw:dataset")) {
				typeNamesIsValid = true;
			} else if (typeNameCurrent.equalsIgnoreCase("csw:datasetcollection")) {
				typeNamesIsValid = true;
			} else if (typeNameCurrent.equalsIgnoreCase("csw:service")) {
				typeNamesIsValid = true;
			} else if (typeNameCurrent.equalsIgnoreCase("csw:application")) {
				typeNamesIsValid = true;
			} else if (typeNameCurrent.equalsIgnoreCase("gmd:MD_Metadata")) {
				typeNamesIsValid = true;
			}
		}
		//no matter if it is invalid
		sessionParameters.setTypeNames(typeNames);
		return typeNamesIsValid;
	}

	/**
	 * analyse the result type, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseResultType(final Element be) throws Exception {
		String resultType = null;
		resultType = be.getAttribute("resultType");
		if (resultType != null && resultType.length() > 0) {
			//TODO validate?
			//Validate the request and return an Acknowledgement message if it
			//passes. Continue processing the request asynchronously.
			if (resultType.equalsIgnoreCase("HITS") ||
				resultType.equalsIgnoreCase("RESULTS")
				// || resultType.equalsIgnoreCase("VALIDATE")
			) {
				sessionParameters.setResultType(resultType);
			} else {
				Exception e = new CSWInvalidParameterValueException("Attribute 'resultType' is invalid.", "resultType");
				throw e;
			}
		} else {
			sessionParameters.setResultType("HITS");
		}
		return true;
	}

	/**
	 * analyse the element (set) name, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseElementSetName(final Element be) throws Exception {
		String elementSetName = null;
		Element elemElementSetName = null;
		NodeList nl = be.getElementsByTagNameNS("http://www.opengis.net/cat/csw/2.0.2", "ElementSetName");
		if (nl == null || nl.getLength() == 0) {
			nl = be.getElementsByTagName("ElementName");
		}
		if (nl != null && nl.getLength() != 0) {
			elemElementSetName = (Element) nl.item(0);
		}
		if (elemElementSetName != null) {
			//if (elemElementSetName.getNodeName().equals("ElementSetName") ||
			//	 elemElementSetName.getNodeName().equals("ElementName")) {
			if (elemElementSetName.getLocalName().equals("ElementSetName") ||
					elemElementSetName.getLocalName().equals("ElementName")) {
				elementSetName = elemElementSetName.getFirstChild().getNodeValue();
				if (elementSetName != null) {
					if (elementSetName.equalsIgnoreCase("full") ||
						elementSetName.equalsIgnoreCase("brief") ||
						elementSetName.equalsIgnoreCase("summary")
					) {
						sessionParameters.setElementSetName(elementSetName);
					} else {
						Exception e = new CSWInvalidParameterValueException("Value of 'ElementSetName' is invalid.", "ElementSetName");
						throw e;
					}
				}
			}
		}
		return true;
	}

	/**
	 * analyse the output schema, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseOutputSchema(final Element be) throws Exception {
		String outputSchema = null;
		outputSchema = be.getAttribute("outputSchema");
		if (outputSchema != null) {
			if (outputSchema.equalsIgnoreCase("CSW:OGCCORE") ||
				outputSchema.equalsIgnoreCase("CSW:PROFILE") ||
				outputSchema.equalsIgnoreCase("http://www.isotc211.org/2005/gmd")) {
				sessionParameters.setOutputSchema(outputSchema);
			} else {
				Exception e = new CSWInvalidParameterValueException("Attribute 'outputSchema=" + outputSchema + "' is invalid.", "outputSchema");
				throw e;
			}
		}
		return true;
	}

	/**
	 * analyse the output format, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseOutputFormat(final Element be) throws Exception {
		String outputFormat = null;
		outputFormat = be.getAttribute("outputFormat");
		if (outputFormat == null || outputFormat.trim().length() == 0) {
		    outputFormat = "application/xml";
		} else if (!outputFormat.equalsIgnoreCase("application/xml")) {
			Exception e = new CSWInvalidParameterValueException("Attribute 'outputFormat' is not 'application/xml'. It is set to '" + outputFormat + "'", "outputFormat");
			throw e;
		}
		return true;
	}

	/**
	 * analyse the schema language, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseSchemaLanguage(final Element be) throws Exception {
		String schemaLanguage = null;
		schemaLanguage = be.getAttribute("schemaLanguage");
		if (schemaLanguage != null && !schemaLanguage.equalsIgnoreCase("XMLSCHEMA") && !schemaLanguage.equalsIgnoreCase("http://www.w3.org/XML/Schema")) {
			Exception e = new CSWInvalidParameterValueException("Attribute 'schemaLanguage' is not 'XMLSCHEMA'.", "schemaLanguage");
			throw e;
		}
		return true;
	}

	/**
	 * analyse the service, return true if ok
	 * @param be SOAPBodyElement
	 * @return boolean
	 * @throws Exception e
	 */
	public boolean analyseService(final Element be) throws Exception {
		String service = null;
		service = be.getAttribute("service");
		if (service == null) {
			Exception e = new CSWMissingParameterValueException("Attribute 'service' is missing.", "service");
			throw e;
		}
		if (!service.equals("CSW")) {
			Exception e = new CSWInvalidParameterValueException("Attribute 'service' is not 'CSW'.", "service");
			throw e;
		}
		return true;
	}
	
	/**
	 * Checks wether the mandatory parameter SERVICE was set to a proper value
	 * in the query. Returns the value of the parameter if yes, throws an exception otherwise.
	 * 
	 * @param reqParams Contains the request parameters and values
	 * @return true if the check was ok
	 * @throws CSW***Exception if an error occurred
	 */
	public static String analyseService(Properties reqParams) throws Exception {
		String result = reqParams.getProperty(ClientRequestParameters.SERVICE, null);
		
		if (result == null || result.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.SERVICE + "' is not specified or has no value", ClientRequestParameters.SERVICE);
		} else {
			if (!result.equals("CSW")) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter '" + ClientRequestParameters.SERVICE + "' has an unsupported value.\n");
				errorMsg.append("Supported values: CSW\n");
				throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.SERVICE);
			}
		}
		return result;
	}

	/**
	 * analyse the version, return version if ok
	 * @param be SOAPBodyElement
	 * @return String
	 * @throws Exception e
	 */
	public String analyseVersion(final Element be) throws Exception {
		String version = null;
		version = be.getAttribute("version");
		if (version == null) {
			Exception e = new CSWMissingParameterValueException("Attribute 'version' is missing.", "version");
			throw e;
		}
		if (!version.equals("2.0.2")) {
			Exception e = new CSWInvalidParameterValueException("Attribute 'version' is not '2.0.2'.", "version");
			throw e;
		}
		return version;
	}
	
	/**
	 * Checks wether the mandatory parameter VERSION was set to a proper value
	 * in the query. Returns the value of the parameter if yes, throws an exception otherwise.
	 * 
	 * Currently checks:
	 * - VERSION is 2.0.0 (mandatory)
	 * 
	 * @param reqParams Contains the request parameters and values
	 * @return true if the check was ok
	 * @throws CSW***Exception if an error occurred
	 */
	public static String analyseVersion(Properties reqParams) throws Exception {
		String result = reqParams.getProperty(ClientRequestParameters.VERSION, null);
		
		if (result == null || result.equals("")) {
			throw new CSWMissingParameterValueException("Parameter '" + ClientRequestParameters.VERSION + "' is not specified or has no value", ClientRequestParameters.VERSION);
		} else {
			if (!result.equals("2.0.0") && !result.equals("2.0.2")) {
				StringBuffer errorMsg = new StringBuffer();
				errorMsg.append("Parameter '" + ClientRequestParameters.VERSION + "' has an unsupported value.\n");
				errorMsg.append("Supported values: 2.0.0, 2.0.2\n");
				throw new CSWInvalidParameterValueException(errorMsg.toString(), ClientRequestParameters.VERSION);
			}
		}
		return result;
	}
}
