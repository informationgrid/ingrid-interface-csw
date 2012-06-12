package de.ingrid.interfaces.csw.admin;

import java.security.Principal;

import org.mortbay.jetty.Request;
import org.mortbay.jetty.security.HashUserRealm;

import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;

public class IngridHashUserRealm extends HashUserRealm {

    @Override
    public Principal authenticate(String username, Object credentials, Request request) {
        synchronized (this) {
            Principal user = super.getPrincipal(username);

            if (user == null) {
                this.put("admin", ApplicationProperties.get(ConfigurationKeys.INGRID_ADMIN_PASSWORD));
                user = super.getPrincipal(username);
            }
        }
        return super.authenticate(username, credentials, request);
    }
}
