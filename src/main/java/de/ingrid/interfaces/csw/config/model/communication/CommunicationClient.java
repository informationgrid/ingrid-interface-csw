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
package de.ingrid.interfaces.csw.config.model.communication;

import java.util.List;

public class CommunicationClient {

    private List<CommunicationServer> connections;

    private String name = DEFAULT_CLIENT_NAME;

    private static String DEFAULT_CLIENT_NAME = "interface-csw-" + System.currentTimeMillis();

    public void setConnections(List<CommunicationServer> connections) {
        this.connections = connections;
    }

    public List<CommunicationServer> getConnections() {
        return connections;
    }

    public void setName(String name) {
        if (name== null) {
            this.name = DEFAULT_CLIENT_NAME;
        } else {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

}
