package de.ingrid.interfaces.csw.admin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.admin.command.IBusHarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.command.RequestDefinitionCommandObject;
import de.ingrid.interfaces.csw.admin.validation.IBusHarvesterValidator;
import de.ingrid.interfaces.csw.config.CommunicationProvider;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.RequestDefinition;
import de.ingrid.interfaces.csw.config.model.communication.Communication;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationClient;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationMessages;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServer;
import de.ingrid.interfaces.csw.config.model.communication.CommunicationServerSocket;
import de.ingrid.interfaces.csw.harvest.impl.IBusHarvester;
import de.ingrid.utils.IBus;
import de.ingrid.utils.PlugDescription;
import edu.emory.mathcs.backport.java.util.Arrays;

@Controller
@SessionAttributes("harvester")
public class EditHarvesterController {

    public static final String TEMPLATE_EDIT_HARVESTER = "/edit_ibus_harvester.html";
    public static final String TEMPLATE_EDIT_HARVESTER_2 = "/edit_ibus_harvester_2.html";
    public static final String TEMPLATE_EDIT_HARVESTER_3 = "/edit_ibus_harvester_3.html";

    ConfigurationProvider cProvider = new ConfigurationProvider();

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep1 _validatorStep1 = null;

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep2 _validatorStep2 = null;

    final private static Log log = LogFactory.getLog(EditHarvesterController.class);

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.GET)
    public String step1Get(final HttpSession session, final ModelMap modelMap, @RequestParam("id") final Integer id)
            throws Exception {

        List<HarvesterConfiguration> hConfigs = cProvider.getConfiguration().getHarvesterConfigurations();
        HarvesterConfiguration hConfig = hConfigs.get(id);
        modelMap.addAttribute("id", id);
        if (hConfig.getClassName().equals(IBusHarvester.class.getName())) {
            IBusHarvesterCommandObject commandObject = new IBusHarvesterCommandObject(hConfig);
            commandObject.setId(id);
            modelMap.addAttribute("harvester", commandObject);
            return "/edit_ibus_harvester";
        } else {
            modelMap.addAttribute("errorKey", "harvester.type.notfound");
        }
        return "/edit_ibus_harvester";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.POST)
    public String step1Post(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (_validatorStep1.validate(errors).hasErrors()) {
            return "/edit_ibus_harvester";
        }

        // put into session
        session.setAttribute("harvester", harvester);

        // transform to absolute path
        harvester.setWorkingDirectory((new File(harvester.getWorkingDirectory())).getAbsolutePath());

        Configuration configuration = cProvider.getConfiguration();
        List<HarvesterConfiguration> hConfigs = configuration.getHarvesterConfigurations();
        HarvesterConfiguration config = hConfigs.get(harvester.getId());
        BeanUtils.copyProperties(harvester, config);
        cProvider.write(configuration);

        return "redirect:" + TEMPLATE_EDIT_HARVESTER_2;

    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_2, method = RequestMethod.GET)
    public String step2Get(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        CommunicationProvider communicationProvider = new CommunicationProvider();
        communicationProvider.setWorkingDirectory(new File(harvester.getWorkingDirectory()));
        File communicationConfigFile = communicationProvider.getConfigurationFile();

        if (communicationConfigFile.exists()) {
            Communication communication = communicationProvider.getConfiguration();
            bindCommunication(harvester, communication);
        }

        return "/edit_ibus_harvester_2";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_2, method = RequestMethod.POST)
    public String step2Post(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (_validatorStep2.validate(errors).hasErrors()) {
            return "/edit_ibus_harvester_2";
        }
        try {
            Communication communication = createCommunication(harvester);
            CommunicationProvider communicationProvider = new CommunicationProvider();
            communicationProvider.setWorkingDirectory(new File(harvester.getWorkingDirectory()));
            communicationProvider.write(communication);
            harvester.setCommunicationXml(communicationProvider.getConfigurationFile().getAbsolutePath());
        } catch (Exception e) {
            log.error("Error creating communication configuration.", e);
            errors.reject("harvester.ibus.communication.couldnotcreate");
            return "/edit_ibus_harvester_2";
        }

        return "redirect:" + TEMPLATE_EDIT_HARVESTER_3;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_3, method = RequestMethod.GET)
    public String step3Get(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        // setup the IBus client
        File file = new File(harvester.getCommunicationXml());
        BusClient client = null;
        try {
            client = BusClientFactory.createBusClient(file);
            if (!client.allConnected()) {
                client.start();
            }
            IBus bus = client.getNonCacheableIBus();
            PlugDescription[] allPlugDescriptions = bus.getAllIPlugs();

            List<PlugDescription> availableIPlugs = new ArrayList<PlugDescription>();
            List<RequestDefinitionCommandObject> enabledIPlugs = new ArrayList<RequestDefinitionCommandObject>();

            if (harvester.getRequestDefinitions() == null) {
                // just copy all plugs
                availableIPlugs = Arrays.asList(allPlugDescriptions);
            } else {
                for (PlugDescription pd : allPlugDescriptions) {
                    boolean isEnabled = false;
                    for (RequestDefinition rd : harvester.getRequestDefinitions()) {
                        if (rd.getProxyId().equalsIgnoreCase(pd.getPlugId())) {
                            isEnabled = true;
                            RequestDefinitionCommandObject rdco = new RequestDefinitionCommandObject(rd);
                            rdco.setDataSourceName(pd.getDataSourceName());
                            rdco.setIsCurrentlyRegistered(true);
                            enabledIPlugs.add(rdco);
                            break;
                        }
                    }
                    if (!isEnabled) {
                        availableIPlugs.add(pd);
                    }
                }
                for (RequestDefinition rd : harvester.getRequestDefinitions()) {
                    boolean isCurrentlyRegistered = false;
                    for (RequestDefinitionCommandObject rdco : enabledIPlugs) {
                        if (rdco.getProxyId().equalsIgnoreCase(rd.getProxyId())) {
                            isCurrentlyRegistered = true;
                            break;
                        }
                    }
                    if (!isCurrentlyRegistered) {
                        RequestDefinitionCommandObject rdco = new RequestDefinitionCommandObject(rd);
                        rdco.setIsCurrentlyRegistered(false);
                        enabledIPlugs.add(rdco);
                    }
                }
            }

            modelMap.addAttribute("enabledIPlugs", enabledIPlugs);
            modelMap.addAttribute("availableIPlugs", availableIPlugs);

        } catch (Exception e) {
            log.error("Error accessing iPlugs.", e);

        }

        return "/edit_ibus_harvester_3";
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