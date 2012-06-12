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
