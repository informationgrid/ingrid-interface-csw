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
