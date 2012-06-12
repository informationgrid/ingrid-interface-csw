package de.ingrid.interfaces.csw.admin;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.HashSessionIdManager;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.server.CSWServlet;

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
        if (!System.getProperties().containsKey("jetty.webapp"))
            log.warn("Property 'jetty.webapp' not defined! Using default webapp directory, which is '"+DEFAULT_WEBAPP_DIR+"'.");
        
        init();
    }
    
    private static void init() throws Exception {
        WebAppContext webAppContext = new WebAppContext(System.getProperty("jetty.webapp", DEFAULT_WEBAPP_DIR), "/");
        
        Server server = new Server(Integer.getInteger("jetty.port", ApplicationProperties.getInteger(ConfigurationKeys.SERVER_PORT, DEFAULT_JETTY_PORT)));
        // fix slow startup time on virtual machine env.
        HashSessionIdManager hsim = new HashSessionIdManager();
        hsim.setRandom(new Random());
        server.setSessionIdManager(hsim);
        server.setHandler(webAppContext);

        server.start();

        WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(webAppContext
                .getServletContext(), "org.springframework.web.servlet.FrameworkServlet.CONTEXT.springapp");
        CSWServlet cswServlet = (CSWServlet) wac.getBean("CSWServlet");
        
        webAppContext.addServlet(new ServletHolder(cswServlet), "/csw");
        
    }

}
