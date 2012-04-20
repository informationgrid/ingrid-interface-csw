package de.ingrid.interfaces.csw.config.model.communication;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("socket")
public class CommunicationServerSocket {

    private Integer port;
    
    private Integer timeout;
    
    private String ip;

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getPort() {
        return port;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }
    
    
    
    
}
