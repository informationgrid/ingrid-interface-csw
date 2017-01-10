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
