/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2022 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.1 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * **************************************************#
 */
/*
 * Copyright (c) 2012 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw.server.cswt;

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
public class CSWTServlet extends HttpServlet {

	/**
	 * The serial number
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * The server facade that handles all requests
	 */
	@Autowired
	private ServerFacadeCSWT serverFacade;

	public CSWTServlet() {
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
	public void setServerFacade(ServerFacadeCSWT serverFacade) {
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
