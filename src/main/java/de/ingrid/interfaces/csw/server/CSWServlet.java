/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.server;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The CSW Servlet. Entry point for incoming requests.
 * Dispatches the requests according to method and content type
 * and delegates all processing to ServerFacade
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
@Service
public class CSWServlet extends HttpServlet {

	/**
	 * The serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The server facade that handles all requests
	 */
	@Autowired
	private ServerFacade serverFacade;

	public CSWServlet() {
	    super();
	    // prevent warnings WARNING: Couldn't flush system prefs: 
	    // java.util.prefs.BackingStoreException: /etc/.java/.systemPrefs/org 
	    // create failed. 
	    Properties p = new Properties();
	    p.setProperty( "platform", "server" );
	    org.geotoolkit.lang.Setup.initialize( p);
	}
	
	
	/**
	 * Set the server facade
	 * @param serverFacade
	 */
	public void setServerFacade(ServerFacade serverFacade) {
		this.serverFacade = serverFacade;
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {
		if (this.serverFacade == null) {
			throw new RuntimeException("CSWServlet is not configured properly: serverFacade is not set.");
		}
		try {
			this.serverFacade.handleGetRequest(request, response);
		} catch (Exception ex) {
			throw new ServletException("GET failed: "+ex.getMessage(), ex);
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
		    if (request.getHeader("Keep-Alive") != null && request.getContentLength() == -1) {
		        Log.debug("Ignore keep-alive request.");
		    } else if (request.getContentType().toLowerCase().indexOf("application/soap+xml") != -1) {
				this.serverFacade.handleSoapRequest(request, response);
			} else {
				this.serverFacade.handlePostRequest(request, response);
			}
		} catch (Exception ex) {
			throw new ServletException("POST failed: "+ex.getMessage(), ex);
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		serverFacade.destroy();
	}
	
	
}
