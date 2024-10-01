/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2024 wemove digital solutions GmbH
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

public class CommunicationServer {
    
    private String name;
    
    private CommunicationServerSocket socket;
    
    private CommunicationServerSecurity security;
    
    private CommunicationMessages messages;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSocket(CommunicationServerSocket socket) {
        this.socket = socket;
    }

    public CommunicationServerSocket getSocket() {
        return socket;
    }

    public void setMessages(CommunicationMessages messages) {
        this.messages = messages;
    }

    public CommunicationMessages getMessages() {
        return messages;
    }

    public void setSecurity(CommunicationServerSecurity security) {
        this.security = security;
    }

    public CommunicationServerSecurity getSecurity() {
        return security;
    }

}
