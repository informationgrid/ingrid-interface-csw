/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
 * ==================================================
 * Licensed under the EUPL, Version 1.2 or – as soon they will be
 * approved by the European Commission - subsequent versions of the
 * EUPL (the "Licence");
 * 
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
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
package de.ingrid.interfaces.csw.server;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.interfaces.csw.migration.Migration;

/**
 * The CSW Servlet. Entry point for incoming requests. Dispatches the requests
 * according to method and content type and delegates all processing to
 * ServerFacade
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

    @Autowired
    private Migration migration;

    @PostConstruct
    private void migrate() {
        // execute migrations
        try {
            migration.migrate();
        } catch (IOException e) {
            throw new RuntimeException( "Migration failed!", e );
        }
    }

    public CSWServlet() {
        super();

        // prevent warnings WARNING: Couldn't flush system prefs:
        // java.util.prefs.BackingStoreException: /etc/.java/.systemPrefs/org
        // create failed.
        Properties p = new Properties();
        p.setProperty( "platform", "server" );
        org.geotoolkit.lang.Setup.initialize( p );
    }

    /**
     * Set the server facade
     * 
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
            throw new RuntimeException( "CSWServlet is not configured properly: serverFacade is not set." );
        }
        try {
            this.serverFacade.handleGetRequest( request, response );
        } catch (Exception ex) {
            throw new ServletException( "GET failed: " + ex.getMessage(), ex );
        }
    }

    /**
     * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            if (request.getHeader( "Keep-Alive" ) != null && request.getContentLength() == -1) {
                //Log.debug( "Ignore keep-alive request." );
            } else if (request.getContentType().toLowerCase().indexOf( "application/soap+xml" ) != -1) {
                this.serverFacade.handleSoapRequest( request, response );
            } else {
                this.serverFacade.handlePostRequest( request, response );
            }
        } catch (Exception ex) {
            throw new ServletException( "POST failed: " + ex.getMessage(), ex );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.servlet.GenericServlet#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        serverFacade.destroy();
    }

}
