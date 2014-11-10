/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 wemove digital solutions GmbH
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
