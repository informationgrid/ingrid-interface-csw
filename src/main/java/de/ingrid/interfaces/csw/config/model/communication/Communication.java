package de.ingrid.interfaces.csw.config.model.communication;

public class Communication {
    
    private CommunicationClient client;

    private CommunicationServer server;
    
    private CommunicationMessages messages;

    public void setClient(CommunicationClient client) {
        this.client = client;
    }

    public CommunicationClient getClient() {
        return client;
    }

    public void setMessages(CommunicationMessages messages) {
        this.messages = messages;
    }

    public CommunicationMessages getMessages() {
        return messages;
    }

    public void setServer(CommunicationServer server) {
        this.server = server;
    }

    public CommunicationServer getServer() {
        return server;
    }

}
