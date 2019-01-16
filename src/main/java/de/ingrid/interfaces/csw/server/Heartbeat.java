/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2019 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.server;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.config.ApplicationProperties;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.iplug.HeartBeatPlug;
import de.ingrid.iplug.PlugDescriptionFieldFilters;
import de.ingrid.utils.IngridCall;
import de.ingrid.utils.IngridDocument;
import de.ingrid.utils.IngridHit;
import de.ingrid.utils.IngridHitDetail;
import de.ingrid.utils.IngridHits;
import de.ingrid.utils.PlugDescription;
import de.ingrid.utils.metadata.DefaultMetadataInjector;
import de.ingrid.utils.processor.impl.QueryExtensionPreProcessor;
import de.ingrid.utils.processor.impl.StatisticPostProcessor;
import de.ingrid.utils.query.IngridQuery;
import de.ingrid.utils.xml.PlugdescriptionSerializer;

@Service
public class Heartbeat extends HeartBeatPlug {
	
	private static Log log = LogFactory.getLog(Heartbeat.class);
    
    private ConfigurationProvider confProvider;

    @Autowired
    public Heartbeat(ConfigurationProvider cProvider) throws Exception {
        super( 60000, new PlugDescriptionFieldFilters( ), new DefaultMetadataInjector[0], new QueryExtensionPreProcessor[0], new StatisticPostProcessor[0] );
        this.confProvider = cProvider;
        Communication cswtCommunication = cProvider.getConfiguration().getCswtCommunication();
        if (cswtCommunication == null) return;
        try {
        	setupCommunication();
        } catch (Exception ex) {
        	log.error("Communication could not be setup. Missing communication.xml?", ex);
        }
        
    }
    
    public void setupCommunication() throws Exception {
        Communication cswtCommunication = confProvider.getConfiguration().getCswtCommunication();
        File pdFile = new File("./conf/plugdescription.xml");
        PlugDescription plugDescription = new PlugDescription();
        plugDescription.setProxyServiceURL( cswtCommunication.getClient().getName() );
        plugDescription.setIplugAdminGuiPort( ApplicationProperties.getInteger( ConfigurationKeys.SERVER_INTERFACE_PORT, 80 ) );
        plugDescription.addToList(IngridQuery.RANKED, "ignore"); 
        PlugdescriptionSerializer pdSerializer = new PlugdescriptionSerializer();
        pdSerializer.serialize(plugDescription, pdFile);
        BusClient busClient = BusClientFactory.createBusClient(new File("./conf/communication.xml"), this);
        busClient.start();
        configure( pdSerializer.deSerialize( pdFile ) );
    }

    @Override
    public IngridHits search(IngridQuery query, int start, int length) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IngridHitDetail getDetail(IngridHit hit, IngridQuery query, String[] requestedFields) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IngridHitDetail[] getDetails(IngridHit[] hits, IngridQuery query, String[] requestedFields) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IngridDocument call(IngridCall targetInfo) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

}
