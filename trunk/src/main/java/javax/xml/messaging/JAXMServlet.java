/*
 * $Id: JAXMServlet.java,v 1.15.2.1 2002/04/23 23:40:58 akv Exp $
 * $Revision: 1.15.2.1 $
 * $Date: 2002/04/23 23:40:58 $
 */

/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.xml.messaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import de.ingrid.interfaces.csw.CSWServlet;
import de.ingrid.interfaces.csw.tools.XMLTools;

/**
 * The superclass for components that
 * live in a servlet container that receives JAXM messages.
 * A <code>JAXMServlet</code> object is notified of a message's arrival
 * using the HTTP-SOAP binding. 
 * <P>
 * The <code>JAXMServlet</code> class is a support/utility class and is
 * provided purely as a convenience.  It is not a mandatory component, and 
 * there is no requirement that it be implemented or extended.
 * <P>
 * Note that when a component that receives messages extends
 * <code>JAXMServlet</code>, it also needs to implement either a
 * <code>ReqRespListener</code> object or a 
 * <code>OnewayListener</code> object,
 * depending on whether the component is written for a request-response
 * style of interaction or for a one-way (asynchronous) style of interaction.
 */
public abstract class JAXMServlet 
    extends HttpServlet
{
    
	private static Log log = LogFactory.getLog(JAXMServlet.class);    

	/**
    * The <code>MessageFactory</code> object that will be used internally
    * to create the <code>SOAPMessage</code> object to be passed to the
    * method <code>onMessage</code>. This new message will contain the data
    * from the message that was posted to the servlet.  Using the 
    * <code>MessageFactory</code> object that is the value for this field 
    * to create the new message ensures that the correct profile is used.
    */
    protected MessageFactory msgFactory = null;
 
   /**
    * Initializes this <code>JAXMServlet</code> object using the given 
    * <code>ServletConfig</code> object and initializing the
    * <code>msgFactory</code> field with a default
    * <code>MessageFactory</code> object.
    *
    * @param servletConfig the <code>ServletConfig</code> object to be
    *        used in initializing this <code>JAXMServlet</code> object
    */
    public void init(ServletConfig servletConfig) 
        throws ServletException
    {
        super.init(servletConfig);
        try {
            // Initialize it to the default.
            msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        } catch (SOAPException ex) {
            throw new ServletException("Unable to create message factory"+ex.getMessage());
        }
    }
    

    /**
     * Sets this <code>JAXMServlet</code> object's <code>msgFactory</code>
     * field with the given <code>MessageFactory</code> object.
     * A <code>MessageFactory</code> object for a particular profile needs to
     * be set before a message is received in order for the message to be 
     * successfully internalized.
     *
     * @param msgFactory the <code>MessageFactory</code> object that will
     *        be used to create the <code>SOAPMessage</code> object that
     *        will be used to internalize the message that was posted to 
     *        the servlet
     */
    public void setMessageFactory(MessageFactory msgFactory) {
        this.msgFactory = msgFactory;
    }

    /**
     * Returns a <code>MimeHeaders</code> object that contains the headers
     * in the given <code>HttpServletRequest</code> object.
     *
     * @param req the <code>HttpServletRequest</code> object that a
     *        messaging provider sent to the servlet
     * @return a new <code>MimeHeaders</code> object containing the headers
     *         in the message sent to the servlet
     */
    protected static
        MimeHeaders getHeaders(HttpServletRequest req) 
    {
        Enumeration en = req.getHeaderNames();
        MimeHeaders headers = new MimeHeaders();

        while (en.hasMoreElements()) {
            String headerName = (String)en.nextElement();
            String headerValue = req.getHeader(headerName);

            StringTokenizer values = new StringTokenizer(headerValue, ",");
            while (values.hasMoreTokens())
                headers.addHeader(headerName, values.nextToken().trim());
        }
        
        return headers;
    }

    /**
     * Sets the given <code>HttpServletResponse</code> object with the
     * headers in the given <code>MimeHeaders</code> object.
     * 
     * @param headers the <code>MimeHeaders</code> object containing the
     *        the headers in the message sent to the servlet
     * @param res the <code>HttpServletResponse</code> object to which the
     *        headers are to be written
     * @see #getHeaders
     */
    protected static
        void putHeaders(MimeHeaders headers, HttpServletResponse res) 
    {
        Iterator it = headers.getAllHeaders();
        while (it.hasNext()) {
            MimeHeader header = (MimeHeader)it.next();
            
            String[] values = headers.getHeader(header.getName());
            if (values.length == 1)
                res.setHeader(header.getName(), header.getValue());
            else 
                {
                    StringBuffer concat = new StringBuffer();
                    int i = 0;
                    while (i < values.length) {
                        if (i != 0)
                            concat.append(',');
                        concat.append(values[i++]);
                    }
                    
                    res.setHeader(header.getName(),
                                  concat.toString());
                }
        }
    }

    /**
     * Internalizes the given <code>HttpServletRequest</code> object
     * and writes the reply to the given <code>HttpServletResponse</code>
     * object.
     * <P>
     * Note that the value for the <code>msgFactory</code> field will be used to
     * internalize the message. This ensures that the message
     * factory for the correct profile is used.
     *
     * @param req the <code>HttpServletRequest</code> object containing the
     *        message that was sent to the servlet
     * @param resp the <code>HttpServletResponse</code> object to which the
     *        response to the message will be written
     * @throws ServletException if there is a servlet error
     * @throws IOException if there is an input or output error
     */
    public void doPost(HttpServletRequest req, 
                       HttpServletResponse resp)
        throws ServletException, IOException 
    {
	try {
            // Get all the headers from the HTTP request.
	    MimeHeaders headers = getHeaders(req);

            // Get the body of the HTTP request.
            InputStream is = req.getInputStream();
            
            // Now internalize the contents of a HTTP request and
            // create a SOAPMessage
	    SOAPMessage msg = msgFactory.createMessage(headers, is);
	    // remove whitespace text nodes from the soap part of the message
	    // this is a workaround for a bug with the Message.getSoapBody() method
	    // which fails if the soap:Body Element is followed my a whitespace in the
	    // textual xml representation of the SOAP message.
	    XMLTools.removeWhitespaceNodes(msg.getSOAPPart());
	    
	    SOAPMessage reply = null;

	    	// THIS IS AN UGLY HACK
	    	// but the whole solutions from GIStec is an ugly hack
	        // we are otherwise not be able to pass the request parameter to the CSW handler
	    	// but we need it to get the query extensions like partner, provider, iplug
	    	if (this instanceof CSWServlet)
        		reply = ((CSWServlet)this).onMessage(msg, req);
        	else if (this instanceof ReqRespListener) 
                reply = ((ReqRespListener) this).onMessage(msg);
            else if (this instanceof OnewayListener)
                ((OnewayListener) this).onMessage(msg);
            else
                throw new ServletException("JAXM component: "
                                           +this.getClass().getName()+" also has to"+
                                           " implement ReqRespListener or OnewayListener");

            if (reply != null) {
                reply.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "utf-8");
                reply.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
                
                // Need to saveChanges 'cos we're going to use the
                // MimeHeaders to set HTTP response information. These
                // MimeHeaders are generated as part of the save.

                if (reply.saveRequired()) {
                    reply.saveChanges(); 
                }

                resp.setStatus(HttpServletResponse.SC_OK);


                putHeaders(reply.getMimeHeaders(), resp);
                Node root = null;
                // Write out the message on the response stream.
                root = reply.getSOAPPart().getDocumentElement();                    
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "no");
                OutputStream os = resp.getOutputStream();
                transformer.transform(new DOMSource(root), new StreamResult(os));                    
                os.flush();
                    
            } else 
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
        catch(Exception ex) {
            log.error("JAXM POST failed.", ex);
        	throw new ServletException("JAXM POST failed "+ex.getMessage());
        }
    }
}
