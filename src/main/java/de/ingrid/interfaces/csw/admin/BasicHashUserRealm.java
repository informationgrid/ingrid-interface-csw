package de.ingrid.interfaces.csw.admin;

import java.security.Principal;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.HashUserRealm;

public class BasicHashUserRealm extends HashUserRealm {

    public BasicHashUserRealm(String name) {
        super(name);
    }
    
    @Override
    public Principal authenticate(String username, Object credentials, Request request) {
        if (request.getPathInfo().startsWith( "/csw" )) {
            return super.authenticate( username, credentials, request );
        } else {
            return null;
        }
    }
}
