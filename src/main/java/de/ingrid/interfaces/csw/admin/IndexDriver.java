/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2017 wemove digital solutions GmbH
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

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * This is a indexer driver to (re) create the index of the the iPlugs. It is
 * typically called from the index.sh script (see in iPlugs for detail).
 * 
 * Since we use heavy spring configuration, the jetty servlet container is
 * started to resolve the spring dependency nightmare.
 * 
 * @author joachim@wemove.com
 * 
 */
public class IndexDriver {

    private static final Log log = LogFactory.getLog(IndexDriver.class);

    private static String DEFAULT_WEBAPP_DIR = "webapp";

    private static int DEFAULT_JETTY_PORT = 8082;

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if (!System.getProperties().containsKey("jetty.webapp"))
            log.warn("Property 'jetty.webapp' not defined! Using default webapp directory, which is '"
                    + DEFAULT_WEBAPP_DIR + "'.");
        if (!System.getProperties().containsKey("jetty.port"))
            log.warn("Property 'jetty.port' not defined! Using default port, which is '" + DEFAULT_JETTY_PORT + "'.");

        WebAppContext webAppContext = new WebAppContext(System.getProperty("jetty.webapp", DEFAULT_WEBAPP_DIR), "/");

        Server server = new Server(Integer.getInteger("jetty.port", DEFAULT_JETTY_PORT));
        // fix slow startup time on virtual machine env.
        HashSessionIdManager hsim = new HashSessionIdManager();
        hsim.setRandom(new Random());
        server.setSessionIdManager(hsim);
        server.setHandler(webAppContext);
        server.start();
        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(webAppContext
                .getServletContext(), "org.springframework.web.servlet.FrameworkServlet.CONTEXT.springapp");
        IndexRunnable r = (IndexRunnable) wac.getBean("indexRunnable");
        r.run();
        System.out.println("Try to stopping the iPlug...");
        server.stop();
        System.out.println("iPlug is stopped.");
    }

}
