/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2023 wemove digital solutions GmbH
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
package de.ingrid.interfaces.csw.admin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.admin.command.IBusHarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.validation.IBusHarvesterValidator;
import de.ingrid.interfaces.csw.config.CommunicationProvider;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationClient;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationMessages;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServer;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServerSocket;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;
import de.ingrid.interfaces.csw.server.Heartbeat;

@Controller
@SessionAttributes("harvester")
public class CSWTIBusController {

    public static final String TEMPLATE_CSWT_COMMUNICATION = "/ibus_cswt.html";

    @Autowired
    ConfigurationProvider cProvider = null;

    @Autowired
    LuceneSearcher searcher;
    
    @Autowired
    Heartbeat hearbeat;

    DocumentBuilderFactory df = null;

    XMLEncoding encoding = null;

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep2 _validatorStep2 = null;

    final private static Log log = LogFactory.getLog(CSWTIBusController.class);

    public CSWTIBusController() {
        df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        encoding = new XMLEncoding();
    }

    @RequestMapping(value = TEMPLATE_CSWT_COMMUNICATION, method = RequestMethod.GET)
    public String step2Get(final HttpSession session, final ModelMap modelMap) throws Exception {
        CommunicationProvider communicationProvider = new CommunicationProvider();
        communicationProvider.setWorkingDirectory(new File("."));
        File communicationConfigFile = communicationProvider.getConfigurationFile();

        IBusHarvesterCommandObject harvester = new IBusHarvesterCommandObject();
        if (communicationConfigFile.exists()) {
            Communication communication = communicationProvider.getConfiguration();
            try {
            	bindCommunication(harvester, communication);
            } catch (Exception ex) {
            	log.error("Error during binding of Communication", ex);
            }
        }
        modelMap.addAttribute("harvester", harvester);

        return "/ibus_cswt";
    }

    @RequestMapping(value = TEMPLATE_CSWT_COMMUNICATION, method = RequestMethod.POST)
    public String step2Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (_validatorStep2.validate(errors).hasErrors()) {
            return "/ibus_cswt";
        }
        try {
            Communication communication = createCommunication(harvester);
            CommunicationProvider communicationProvider = new CommunicationProvider();
            communicationProvider.setWorkingDirectory(new File("."));
            communicationProvider.write(communication);
            harvester.setCommunicationXml(communicationProvider.getConfigurationFile().getAbsolutePath());
            updateAndSaveConfiguration(communication);
            
            
            BusClient busClient = BusClientFactory.getBusClient();
            if (busClient == null) {
                this.hearbeat.setupCommunication();
            } else {
                busClient.restart();
            }
            
        } catch (Exception e) {
            log.error("Error creating communication configuration.", e);
            errors.reject("harvester.ibus.communication.couldnotcreate");
            return "/ibus_cswt";
        }

        return "redirect:" + TEMPLATE_CSWT_COMMUNICATION;
    }


    private void updateAndSaveConfiguration(Communication communication) throws IOException {
        Configuration configuration = cProvider.getConfiguration();
        configuration.setCswtCommunication( communication );
        if (log.isDebugEnabled()) {
            log.debug("Save configuration to: " + cProvider.getConfigurationFile());
        }
        cProvider.write(configuration);
    }

    private Communication createCommunication(IBusHarvesterCommandObject harvester) {
        Communication communication = new Communication();
        CommunicationClient client = new CommunicationClient();
        client.setName(harvester.getClientProxyId());
        CommunicationServer server = new CommunicationServer();
        server.setName(harvester.getiBusProxyId());
        CommunicationServerSocket socket = new CommunicationServerSocket();
        socket.setIp(harvester.getiBusIp());
        socket.setPort(harvester.getiBusPort());
        socket.setTimeout(10000);
        server.setSocket(socket);
        CommunicationMessages serverMessages = new CommunicationMessages();
        serverMessages.setMaximumSize(1024L * 1024L);
        serverMessages.setThreadCount(100);
        server.setMessages(serverMessages);
        List<CommunicationServer> connections = new ArrayList<CommunicationServer>();
        connections.add(server);
        client.setConnections(connections);
        communication.setClient(client);
        CommunicationMessages messages = new CommunicationMessages();
        messages.setHandleTimeout(120);
        messages.setQueueSize(100);
        communication.setMessages(messages);

        return communication;
    }

    private void bindCommunication(IBusHarvesterCommandObject harvester, Communication communication) {

        harvester.setClientProxyId(communication.getClient().getName());
        harvester.setiBusIp(communication.getClient().getConnections().get(0).getSocket().getIp());
        harvester.setiBusPort(communication.getClient().getConnections().get(0).getSocket().getPort());
        harvester.setiBusProxyId(communication.getClient().getConnections().get(0).getName());
    }

}
