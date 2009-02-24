/*
 * Copyright (c) 2006 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw;

// IMPORTS java.io
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.messaging.JAXMServlet;
import javax.xml.messaging.OnewayListener;
import javax.xml.messaging.ReqRespListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.AxisFault;
import org.apache.axis.Message;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.analyse.ClientRequestParameters;
import de.ingrid.interfaces.csw.analyse.CommonAnalyser;
import de.ingrid.interfaces.csw.analyse.DescRecAnalyser;
import de.ingrid.interfaces.csw.analyse.GetCapAnalyser;
import de.ingrid.interfaces.csw.analyse.GetRecAnalyser;
import de.ingrid.interfaces.csw.analyse.SessionParameters;
import de.ingrid.interfaces.csw.exceptions.CSWException;
import de.ingrid.interfaces.csw.tools.CSWInterfaceConfig;
import de.ingrid.interfaces.csw.tools.IOTools;
import de.ingrid.interfaces.csw.tools.SOAPTools;
import de.ingrid.interfaces.csw.tools.ServletTools;
import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw.tools.XPathUtils;
import de.ingrid.interfaces.csw.transform.RequestTransformer;
import de.ingrid.interfaces.csw.transform.response.CSWResponseTransformer;
import de.ingrid.interfaces.csw.utils.CswConfig;
import de.ingrid.utils.IBus;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.query.IngridQuery;

/**
 * servlet for the CSW. receives a SOAP request and returns a SOAP response.
 * 
 * @author rschaefer
 */
public class CSWServlet extends JAXMServlet implements ReqRespListener {
	/**
	 * the serialVersionUID
	 */
	static final long serialVersionUID = 0;

	/**
	 * the Catalogue Service Web
	 */
	private CSW csw = null;

	/**
	 * flag that shows if SOAP version 1.2 should be created
	 */
	private boolean createSOAP12 = true;

	private BusClient client = null;

	/**
	 * the log object
	 */
	private static Log log = LogFactory.getLog(CSWServlet.class);

	/*
	 * Static object used to retrieve the config data
	 */
	private static CSWInterfaceConfig cswConfig = CSWInterfaceConfig.getInstance();

	/**
	 * Initializing the servlet. This method is only called once when starting
	 * the web app.
	 * 
	 * @param servletConfig
	 *            servlet configuration
	 * @throws ServletException
	 *             exception
	 */
	public final void init(final ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
		IBus bus = null;
		System.setProperty("javax.xml.soap.MessageFactory", "org.apache.axis.soap.MessageFactoryImpl");
		System.setProperty("javax.xml.soap.SOAPFactory", "org.apache.axis.soap.SOAPFactoryImpl");

		if (log.isInfoEnabled()) {
			log.info("Starting InGrid CSW Interface (CSW Version "
					+ cswConfig.getString(CSWInterfaceConfig.CSW_VERSION) + ")");
			log.info("javax.xml.soap.MessageFactory: " + System.getProperty("javax.xml.soap.MessageFactory"));
			log.info("javax.xml.soap.SOAPFactory: " + System.getProperty("javax.xml.soap.SOAPFactory"));
		}

		try {
			if (log.isInfoEnabled()) {
				log.info("trying to connect to bus via JXTA client... ");
			}
			client = BusClientFactory.createBusClient();
			bus = (IBus) client.getNonCacheableIBus();
		} catch (Exception e) {
			log.error("init iBus communication: " + e.getMessage(), e);
		}

		if (bus != null) {
			if (log.isInfoEnabled()) {
				log.info("connect to bus: success.");
			}
			cswConfig.setIBus(bus);
		}
		logMemoryInfo("init");
	}

	/**
	 * This method handles SOAP 1.2 requests via http-POST. It called by the
	 * servlet engine. The resulting message is sent to the client.
	 * 
	 * @param message
	 *            SOAPMessage
	 * @return soapResponseMessage SOAPMessage
	 */
	public final SOAPMessage onMessage(final SOAPMessage message) {
		if (log.isDebugEnabled()) {
			log.debug("javax.xml.soap.MessageFactory: " + System.getProperty("javax.xml.soap.MessageFactory"));
			log.debug("javax.xml.soap.SOAPFactory: " + System.getProperty("javax.xml.soap.SOAPFactory"));
			try {
				log.debug("incoming SOAP:" + ((Message) message).getSOAPPartAsString());
			} catch (Exception e) {}
		}

		createSOAP12 = true;
		// Message soapRequestMessage = (Message) message.;
		SOAPMessage soapResponseMessage = null;

		try {
			if (!message.getSOAPPart().getEnvelope().getNamespaceURI().equalsIgnoreCase(
					"http://www.w3.org/2003/05/soap-envelope")) {
				throw new Exception("Only SOAP 1.2 is supported.");
			}
			csw = new CSW();
			soapResponseMessage = csw.doSoapRequest(message);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			try {
				if (e instanceof CSWException) {
					soapResponseMessage = SOAPTools.createExceptionReport(e.getMessage(), ((CSWException) e)
							.getExceptionCode(), ((CSWException) e).getLocator(), createSOAP12);
				} else {
					soapResponseMessage = SOAPTools.createExceptionReport(e.getMessage(), "NoApplicableCode", null,
							createSOAP12);
				}
			} catch (Exception se) {
				log.error(se.getMessage());
			}
		}

		try {
			if (soapResponseMessage == null) {
				Exception e = new Exception("No response was received.");
				soapResponseMessage = SOAPTools.createExceptionReport(e.getMessage(), "NoApplicableCode", null, true);
				throw e;
			}
		} catch (Exception se) {
			log.error(se.getMessage());
		}
		// * for debugging only:
		try {
			log.debug("outgoing SOAP:\n" + ((Message) soapResponseMessage).getSOAPPartAsString());
		} catch (AxisFault e) {
			log.error("AxisFault while outputting SOAP response:" + e, e);
		}
		// */

		return soapResponseMessage;
	}

	/**
	 * This method handles KVP requests. Supported requests are: -
	 * GetCapabilities - DescribeRecord - GetRecord - GetRecordById
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) {
		/*
		 * Recognize type of request. In case of an unknown one an error message
		 * is sent.
		 */
		try {
			response.setContentType("text/xml");
			response.setCharacterEncoding("UTF-8");
			Properties reqParams = ServletTools.createPropertiesFromRequest(request, true); // all
																							// key
																							// names
																							// uppercase

			// Check the value of the REQUEST parameter. An exception is thrown
			// in case
			// of an improper value, so no further checking is needed here.
			String operation = CommonAnalyser.analyseOperation(reqParams);

			// Check the value of the SERVICE parameter. An exception is thrown
			// in case
			// of an improper value, so no further checking is needed here.
			// We don't need the value of the service parameter, so ignore it.
			CommonAnalyser.analyseService(reqParams);

			if (operation.equals(ClientRequestParameters.GETCAPABILITIES)) {
				doGet_GetCapabilities(reqParams, response);
			} else if (operation.equalsIgnoreCase(ClientRequestParameters.DESCRIBERECORD)) {
				doGet_DescribeRecord(reqParams, response);
			} else if (operation.equalsIgnoreCase(ClientRequestParameters.GETRECORDS)) {
				doGet_GetRecords(reqParams, response);
			} else if (operation.equalsIgnoreCase(ClientRequestParameters.GETRECORDBYID)) {
				doGet_GetRecordById(reqParams, response);
			}
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			String errorXmlMsg = null;
			if (e instanceof CSWException) {
				errorXmlMsg = CSWException.createXmlExceptionReport(e.getMessage(), ((CSWException) e)
						.getExceptionCode(), ((CSWException) e).getLocator());
			} else {
				errorXmlMsg = CSWException.createXmlExceptionReport(e.getMessage(), "NoApplicableCode", null);
			}
			try {
				response.getOutputStream().print(errorXmlMsg);
			} catch (IOException ioe) {
				log.error("Unable to send error XML message to client: " + ioe.getMessage());
			}
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			if (req.getContentType().equalsIgnoreCase("application/soap+xml")) {
				super.doPost(req, resp);
			} else if (req.getContentType().toLowerCase().startsWith("application/xml")) {
	            // Get the body of the HTTP request.
	            
				DocumentBuilderFactory df = DocumentBuilderFactory.newInstance();
				df.setNamespaceAware(true);
				Document inDoc = df.newDocumentBuilder().parse(req.getInputStream());
	            CSW csw = new CSW();
	            Document responseDoc = csw.doPostRequest(inDoc);
	    		if (log.isDebugEnabled()) {
	    			log.debug("CSW response: " + XMLTools.toString(responseDoc));
	    		}

	    		resp.getOutputStream().print(XMLTools.toString(responseDoc));
	    		resp.getOutputStream().flush();
			}
		} catch (Exception ex) {
			throw new ServletException("POST failed " + ex.getMessage(), ex);
		}
	}

	/**
	 * Performs a GetCapabilities kvp request and directly returns the result in
	 * case of success. If an error occurs, the method throws a CSW*Exception
	 * that must be handled by the caller.
	 * 
	 * @param reqParams
	 *            The parameters of the HTTP GET request
	 * @param response
	 *            The http response object needed for getting the output stream
	 * @throws Exception
	 */
	private void doGet_GetCapabilities(final Properties reqParams, final HttpServletResponse response) throws Exception {
		log.debug("enter");
		GetCapAnalyser getCapAnalyser = new GetCapAnalyser();
		if (getCapAnalyser.analyse(reqParams)) {
			URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_GETCAPABILITIES));
			Reader reader = new InputStreamReader(url.openStream());
			Document doc = XMLTools.parse(reader);

			// try to replace the interface URLs
			NodeList nodes = XPathUtils.getNodeList(doc, "//ows:Operation/*/ows:HTTP/*/@xlink:href");
			// get host
			String host = CswConfig.getInstance().getString(CswConfig.SERVER_INTERFACE_HOST, null);
			if (host == null) {
				log.info("The interface host address is not specified, use local hosts address instead.");
				try {
					InetAddress addr = InetAddress.getLocalHost();
					host = addr.getHostAddress();
				} catch (UnknownHostException e) {
					log.error("Unable to get interface host address.", e);
					throw e;
				}
			}
			// get port
			String port = CswConfig.getInstance().getString(CswConfig.SERVER_INTERFACE_PORT, "80");
			// replace interface host and port
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				String s = nodes.item(idx).getTextContent();
				s = s.replaceAll(CswConfig.KEY_INTERFACE_HOST, host);
				s = s.replaceAll(CswConfig.KEY_INTERFACE_PORT, port);
				nodes.item(idx).setTextContent(s);
			}

			response.getOutputStream().write(XMLTools.toString(doc).getBytes());
		}
		log.debug("leaving");
	}

	/**
	 * Performs a DescribeRecord kvp request and directly returns the result in
	 * case of success. If an error occurs, the method throws a CSW*Exception
	 * that must be handled by the caller.
	 * 
	 * @param reqParams
	 *            The parameters of the HTTP GET request
	 * @param response
	 *            The http response object needed for getting the output stream
	 * @throws Exception
	 */
	private void doGet_DescribeRecord(final Properties reqParams, final HttpServletResponse response) throws Exception {
		log.debug("enter");
		DescRecAnalyser descRecAnalyser = new DescRecAnalyser();
		if (descRecAnalyser.analyse(reqParams)) {
			URL url = new URL(cswConfig.getUrlPath(CSWInterfaceConfig.FILE_DESCRIBERECORD));
			IOTools.writeInputToOutputStream(url.openStream(), response.getOutputStream());
		}
		log.debug("leaving");
	}

	/**
	 * Performs a GetRecords kvp request and directly returns the result in case
	 * of success. If an error occurs, the method throws a CSW*Exception that
	 * must be handled by the caller.
	 * 
	 * TODO: The method is not yet finished. At the moment, it can only check
	 * _IF_ the KVP request is valid, but it does NOT process the query and
	 * return a reply.
	 * 
	 * @param reqParams
	 *            The parameters of the HTTP GET request
	 * @param response
	 *            The http response object needed for getting the output stream
	 * @throws Exception
	 */
	private void doGet_GetRecords(final Properties reqParams, final HttpServletResponse response) throws Exception {
		log.debug("enter");
		GetRecAnalyser getRecAnalyser = new GetRecAnalyser();
		if (getRecAnalyser.analyse(reqParams)) {
			StringBuffer result = new StringBuffer();
			result.append("<?xml version=\"1.0\" encoding=\""
					+ cswConfig.getString(CSWInterfaceConfig.RESPONSE_ENCODING) + "\"?>\n");
			result.append("<RobMeldung>Anfrage ist ok</RobMeldung>\n");
			response.getOutputStream().print(result.toString());
		}
		log.debug("leaving");
	}

	/**
	 * Performs a GetRecordById kvp request and directly returns the result in
	 * case of success. If an error occurs, the method throws a CSW*Exception
	 * that must be handled by the caller.
	 * 
	 * TODO: The method is not yet finished. At the moment, it can only check
	 * _IF_ the KVP request is valid, but it does NOT process the query and
	 * return a reply.
	 * 
	 * @param reqParams
	 *            The parameters of the HTTP GET request
	 * @param response
	 *            The http response object needed for getting the output stream
	 * @throws Exception
	 */
	private void doGet_GetRecordById(final Properties reqParams, final HttpServletResponse response) throws Exception {

		SessionParameters sessionParameters = new SessionParameters();
		sessionParameters.setElementSetName(reqParams.getProperty("elementSetName", "brief"));
		sessionParameters.setOutputSchema(reqParams.getProperty("outputSchema"));
		sessionParameters.setVersion(reqParams.getProperty("version","2.0.2"));
		sessionParameters.setOperationIsGetRecById(true);
		ArrayList<String> idList = new ArrayList<String>();
		idList.add(reqParams.getProperty("ID"));
		sessionParameters.setIdsList(idList);

		IngridQuery ingridQuery = null;
		RequestTransformer requestTransformer = new RequestTransformer();
		ingridQuery = requestTransformer.transform(sessionParameters.getIdsList());

		sessionParameters.setResultType("results");

		// forward the request/query to the ibus
		IngridHits hits = CSW.doBusRequest(ingridQuery, sessionParameters);

		CSWResponseTransformer responseTransformer = new CSWResponseTransformer();
		Document responseDoc = responseTransformer.transform(hits, ingridQuery, sessionParameters);
		
		if (log.isDebugEnabled()) {
			log.debug("CSW response: " + XMLTools.toString(responseDoc));
		}

		response.getOutputStream().write(XMLTools.toString(responseDoc).getBytes("UTF-8"));
	}

	/**
	 * logs JVM memory information. Parameter string strMethodName is the name
	 * of the method where the logging occurs.
	 * 
	 * @param strMethodName
	 *            String name of the method
	 */
	private void logMemoryInfo(final String strMethodName) {
		Runtime runtime = Runtime.getRuntime();
		long longTotalMemory = runtime.totalMemory();
		log.debug("(" + strMethodName + ") JVM total memory: " + Long.toString(longTotalMemory));
		long longFreeMemory = runtime.freeMemory();
		log.debug("(" + strMethodName + ") JVM free memory: " + Long.toString(longFreeMemory));
		long longMaxMemory = runtime.maxMemory();
		log.debug("(" + strMethodName + ") JVM max memory: " + Long.toString(longMaxMemory));
	}

	public void destroy() {
	}
}
