/*
 * Copyright (c) 2009 wemove digital solutions. All rights reserved.
 */

package de.ingrid.interfaces.csw2.encoding.impl;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.tools.XMLTools;
import de.ingrid.interfaces.csw2.exceptions.CSWException;

/**
 * DefaultEncoding implements common methods that may be used
 * by subclasses, e.g. formatting XML responses.
 * 
 * @author ingo herwig <ingo@wemove.com>
 */
public abstract class DefaultEncoding extends AbstractEncoding {

	@Override
	public void writeResponse(Document document) throws Exception {
		
		// set the MimeHeaders of the response
		HttpServletResponse response = this.getResponse();
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		
		String documentStr = XMLTools.toString(document);
		
		// write out the response on the response stream
		OutputStream os = response.getOutputStream();
		os.write(documentStr.getBytes());
		os.flush();
	}

	@Override
	public void reportError(Exception e) throws Exception {
		
		Document errorXmlMsg = null;
		if (e instanceof CSWException) {
			errorXmlMsg = ((CSWException)e).toXmlExceptionReport();
		} else {
			errorXmlMsg = new CSWException(e.getMessage(), "NoApplicableCode", null).toXmlExceptionReport();
		}
		this.writeResponse(errorXmlMsg);
	}

}