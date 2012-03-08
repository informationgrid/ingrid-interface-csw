package de.ingrid.interfaces.csw.admin;

import java.io.IOException;
import java.security.Principal;
import java.util.Properties;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.HashUserRealm;

public class IngridHashUserRealm extends HashUserRealm {

    Properties config = new Properties();

    public IngridHashUserRealm() {
        try {
            config.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            throw new RuntimeException("Missing configuration.");
        }
    }

    public Principal authenticate(String username, Object credentials, Request request)

    {
        synchronized (this)

        {
            Principal user = super.getPrincipal(username);

            if (user == null) {
                put("admin", config.getProperty("ingrid.admin.password"));
                user = super.getPrincipal(username);
            }

        }
        return super.authenticate(username, credentials, request);
    }
}
