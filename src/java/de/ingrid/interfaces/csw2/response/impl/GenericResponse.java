/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.response.impl;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw2.encoding.CSWMessageEncoding;
import de.ingrid.interfaces.csw2.response.CSWResponse;

public class GenericResponse implements CSWResponse {

	protected CSWMessageEncoding encoding = null;
	protected Document content = null;

	@Override
	public void initialize(CSWMessageEncoding encoding) {
		this.encoding = encoding;
	}

	@Override
	public void setContent(Document document) {
		this.content  = document;
	}

	@Override
	public void serialize() throws Exception {
		if (this.encoding == null)
			throw new RuntimeException("This response instance is not initialized properly: No encoding set.");
		if (this.content == null)
			throw new RuntimeException("This response instance is not initialized properly: No content set.");
		
		HttpServletResponse response = this.encoding.getResponse();
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		response.getOutputStream().write(XMLTools.toString(this.content).getBytes());
		response.getOutputStream().flush();		
	}
}
