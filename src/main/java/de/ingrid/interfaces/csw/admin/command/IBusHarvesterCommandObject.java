/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2018 wemove digital solutions GmbH
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
/**
 * 
 */
package de.ingrid.interfaces.csw.admin.command;

import org.springframework.beans.BeanUtils;

import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.IBusHarvesterConfiguration;

/**
 * @author joachim
 * 
 */
public class IBusHarvesterCommandObject extends IBusHarvesterConfiguration implements Identificable {

    private String iBusIp;

    private Integer iBusPort;

    private String iBusProxyId;

    private String clientProxyId;

    private Integer id;

    public IBusHarvesterCommandObject() {
        super();
    }
    
    public IBusHarvesterCommandObject(HarvesterConfiguration config) {
        super();
        BeanUtils.copyProperties(config, this);
    }

    public String getiBusIp() {
        return iBusIp;
    }

    public void setiBusIp(String iBusIp) {
        this.iBusIp = iBusIp;
    }

    public Integer getiBusPort() {
        return iBusPort;
    }

    public void setiBusPort(Integer iBusPort) {
        this.iBusPort = iBusPort;
    }

    public String getiBusProxyId() {
        return iBusProxyId;
    }

    public void setiBusProxyId(String iBusProxyId) {
        this.iBusProxyId = iBusProxyId;
    }

    public String getClientProxyId() {
        return clientProxyId;
    }

    public void setClientProxyId(String clientProxyId) {
        this.clientProxyId = clientProxyId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getId() {
        return id;
    }

}
