package de.ingrid.interfaces.csw.config.model.communication;

import java.util.List;

public class CommunicationClient {
    
    private List<CommunicationServer> connections;
    
    private String name;

    public void setConnections(List<CommunicationServer> connections) {
        this.connections = connections;
    }

    public List<CommunicationServer> getConnections() {
        return connections;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    

}
