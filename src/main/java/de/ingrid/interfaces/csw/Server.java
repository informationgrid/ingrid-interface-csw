/*
 * Copyright (c) 2007 wemove digital solutions. All rights reserved.
 */
package de.ingrid.interfaces.csw;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.servlet.ServletHandler;

import de.ingrid.interfaces.csw.utils.CswConfig;


/**
 * TODO Describe your created type (class, etc.) here.
 *
 * @author joachim@wemove.com
 */
public class Server {

    private final static Log log = LogFactory.getLog(Server.class);

    /**
     * @param args
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception {
    	if (log.isInfoEnabled()) {
    		log.info("starting csw interface...");
    	}
        
        HttpServer server=new HttpServer();
          
        // Create a port listener
        SocketListener listener=new SocketListener();
        listener.setPort(CswConfig.getInstance().getInt(CswConfig.SERVER_PORT, 80));
        server.addListener(listener);

        // Create a context 
        HttpContext context = new HttpContext();
        context.setContextPath("/");
        server.addContext(context);
          
        // Create a servlet container
        ServletHandler servlets = new ServletHandler();
        context.addHandler(servlets);
        
        // Map a servlet onto the container
        servlets.setAutoInitializeServlets(true);
        servlets.addServlet("csw","/csw","de.ingrid.interfaces.csw.CSWServlet");
        servlets.initialize(context);

        // Start the http server
        server.start();
    	if (log.isInfoEnabled()) {
    		log.info("... started wainting for requests.");
    	}
    }

}
