/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2015 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.domain.encoding.impl;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;

import de.ingrid.interfaces.csw.domain.exceptions.CSWException;
import de.ingrid.interfaces.csw.tools.StringUtils;

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

		String documentStr = StringUtils.nodeToString(document);

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
		    String message = e.getMessage();
		    if (e.getCause() != null) message += e.getCause().getMessage();
			errorXmlMsg = new CSWException(message, "NoApplicableCode", null).toXmlExceptionReport();
		}
		this.writeResponse(errorXmlMsg);
	}

}
