/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author ingo herwig <ingo@wemove.com>
 */
public class CSWServlet extends HttpServlet {

	/**
	 * The serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public final void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException {

		try {
			ServerFacade.handleGetRequest(request, response);
		} catch (Exception ex) {
			throw new ServletException("GET failed: "+ex.getMessage(), ex);
		}
	}

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			if (request.getContentType().toLowerCase().indexOf("application/soap+xml") != -1) {
				
				ServerFacade.handleSoapRequest(request, response);
			}
			else if (request.getContentType().toLowerCase().indexOf("application/xml") != -1) {

				ServerFacade.handlePostRequest(request, response);
			}
			else {
				throw new ServletException("Unsupported Content Type in POST request: "+request.getContentType());
			}
		} catch (Exception ex) {
			throw new ServletException("POST failed: "+ex.getMessage(), ex);
		}
	}
}
