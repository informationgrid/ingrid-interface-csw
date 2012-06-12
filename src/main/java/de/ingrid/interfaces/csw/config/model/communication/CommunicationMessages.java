package de.ingrid.interfaces.csw.config.model.communication;

public class CommunicationMessages {

    private Integer queueSize;
    
    private Integer handleTimeout;
    
    private Long maximumSize;
    
    private Integer threadCount;


    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setHandleTimeout(Integer handleTimeout) {
        this.handleTimeout = handleTimeout;
    }

    public Integer getHandleTimeout() {
        return handleTimeout;
    }
    
    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    public Long getMaximumSize() {
        return maximumSize;
    }

    public void setThreadCount(Integer threadCount) {
        this.threadCount = threadCount;
    }

    public Integer getThreadCount() {
        return threadCount;
    }
    
    
}
