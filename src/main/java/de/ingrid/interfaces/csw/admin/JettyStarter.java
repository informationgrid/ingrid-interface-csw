/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.admin;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.BasicAuthenticator;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.server.CSWServlet;
import de.ingrid.interfaces.csw.server.cswt.CSWTServlet;

/**
 * This class starts a Jetty server where the webapp will be executed.
 * @author André Wallat
 *
 */
public class JettyStarter {
    private static final Log log = LogFactory.getLog(JettyStarter.class);

    private static String DEFAULT_WEBAPP_DIR    = "webapp";

    private static int    DEFAULT_JETTY_PORT    = 8082;


    public static void main(String[] args) throws Exception {
        init();
    }
    
    private static void init() throws Exception {
        String webappDir = ApplicationProperties.get(ConfigurationKeys.SERVER_WEBAPP, DEFAULT_WEBAPP_DIR);
        WebAppContext webAppContext = new WebAppContext(webappDir, "/");
        int port = Integer.getInteger("jetty.port", ApplicationProperties.getInteger(ConfigurationKeys.SERVER_PORT, DEFAULT_JETTY_PORT));

        log.info("==================================================");
        log.info("Start server using directory \"" + webappDir + "\" at port: " + port);
        log.info("==================================================");

        Server server = new Server(port);
        // fix slow startup time on virtual machine env.
        HashSessionIdManager hsim = new HashSessionIdManager();
        hsim.setRandom(new Random());
        server.setSessionIdManager(hsim);
        
        Handler[] handlers = new Handler[2];
        handlers[0] = basicSecurityHandler();
        handlers[1] = webAppContext;
        server.setHandlers(handlers);
        server.start();
        
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(webAppContext
                .getServletContext(), "org.springframework.web.servlet.FrameworkServlet.CONTEXT.springapp");
        CSWServlet cswServlet = (CSWServlet) wac.getBean("CSWServlet");
        CSWTServlet cswtServlet = (CSWTServlet) wac.getBean("CSWTServlet");

        // the contexts are hardcoded here, but with apache proxies, a different access URL
        // can be generated, to define the correct URL in the getCapabilities document
        // the config file has to be edited (server.interface.path)
        webAppContext.addServlet(new ServletHolder(cswServlet), "/csw");
        webAppContext.addServlet(new ServletHolder(cswtServlet), "/csw-t");
        server.join();
        
    }
    
    private static SecurityHandler basicSecurityHandler() {
        SecurityHandler csh = new SecurityHandler();
        csh.setAuthenticator( new BasicAuthenticator());
        HashUserRealm userRealm = new BasicHashUserRealm("UserRealm");
        try {
            userRealm.setConfig( ApplicationProperties.get( "realm.properties.path" ) );
        } catch (IOException e) {
            log.error("Error getting properties", e);
        }
        csh.setUserRealm( userRealm  );
        
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);
        
        ConstraintMapping[] cm = new ConstraintMapping[1];
        cm[0] = new ConstraintMapping();
        cm[0].setConstraint(constraint);
        cm[0].setPathSpec("/csw-t");
                
        csh.setConstraintMappings( cm );
        return csh;
    }
    
}
