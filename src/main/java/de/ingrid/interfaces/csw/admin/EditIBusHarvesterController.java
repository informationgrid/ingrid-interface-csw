package de.ingrid.interfaces.csw.admin;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;

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
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import de.ingrid.ibus.client.BusClient;
import de.ingrid.ibus.client.BusClientFactory;
import de.ingrid.interfaces.csw.admin.command.IBusHarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.command.Identificable;
import de.ingrid.interfaces.csw.admin.command.RequestDefinitionCommandObject;
import de.ingrid.interfaces.csw.admin.validation.IBusHarvesterValidator;
import de.ingrid.interfaces.csw.config.ApplicationProperties;
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
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.domain.constants.ConfigurationKeys;
import de.ingrid.interfaces.csw.domain.encoding.impl.XMLEncoding;
import de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester;
import de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester.IBusClosableLock;
import de.ingrid.interfaces.csw.index.IsoIndexManager;
import de.ingrid.interfaces.csw.search.CSWRecordResults;
import de.ingrid.interfaces.csw.search.impl.LuceneSearcher;
import de.ingrid.interfaces.csw.tools.FileUtils;
import de.ingrid.utils.IBus;
import de.ingrid.utils.PlugDescription;

@Controller
@SessionAttributes("harvester")
public class EditIBusHarvesterController {

    public static final String TEMPLATE_EDIT_HARVESTER = "/edit_ibus_harvester.html";
    public static final String TEMPLATE_EDIT_HARVESTER_2 = "/edit_ibus_harvester_2.html";
    public static final String TEMPLATE_EDIT_HARVESTER_3 = "/edit_ibus_harvester_3.html";
    public static final String TEMPLATE_EDIT_HARVESTER_4 = "/edit_ibus_harvester_4.html";

    @Autowired
    ConfigurationProvider cProvider = null;

    @Autowired
    LuceneSearcher searcher;

    DocumentBuilderFactory df = null;

    XMLEncoding encoding = null;

    public static final String IPLUG_QUERY = "<GetRecords outputFormat=\"text/xml\" outputSchema=\"csw:profile\"\n"
            + "            requestId=\"csw:1\" resultType=\"results\" startPosition=\"1\" maxRecords=\"1\"\n"
            + "            xmlns=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\" version=\"2.0.2\">\n"
            + "            <Query typeNames=\"csw:service,csw:dataset\">\n"
            + "                <ElementSetName typeNames=\"\">brief</ElementSetName>\n"
            + "                <Constraint version=\"1.1.0\"> \n"
            + "                    <Filter xmlns=\"http://www.opengis.net/ogc\">\n"
            + "                        <PropertyIsEqualTo>\n"
            + "                            <PropertyName>iplug</PropertyName>\n"
            + "                            <Literal>PATTERN_PLUG_ID</Literal>\n"
            + "                        </PropertyIsEqualTo>\n" + "                    </Filter>\n"
            + "                </Constraint></Query>\n" + "        </GetRecords>";

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep1 _validatorStep1 = null;

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep2 _validatorStep2 = null;

    @Autowired
    private final IBusHarvesterValidator.IBusHarvesterValidatorStep4 _validatorStep4 = null;

    @Autowired
    private IsoIndexManager indexManager;

    final private static Log log = LogFactory.getLog(EditIBusHarvesterController.class);

    public EditIBusHarvesterController() {
        df = DocumentBuilderFactory.newInstance();
        df.setNamespaceAware(true);
        encoding = new XMLEncoding();
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.GET)
    public String step1Get(final HttpSession session, final ModelMap modelMap,
            @RequestParam(value = "id", required = false) final Integer id) throws Exception {

        if (id != null && id >= 0) {
            List<HarvesterConfiguration> hConfigs = cProvider.getConfiguration().getHarvesterConfigurations();
            HarvesterConfiguration hConfig = hConfigs.get(id);
            if (hConfig.getWorkingDirectory() == null) {
                hConfig.setWorkingDirectory(new File(FileUtils.encodeFileName(hConfig.getName())).getAbsolutePath());
            }
            modelMap.addAttribute("id", id);
            if (hConfig.getClassName().equals(IBusHarvester.class.getName())) {
                IBusHarvesterCommandObject commandObject = new IBusHarvesterCommandObject(hConfig);
                commandObject.setId(id);
                // put into session
                session.setAttribute("harvester", commandObject);
                modelMap.addAttribute("harvester", commandObject);
            }
        } else if (session.getAttribute("harvester") == null) {
            modelMap.addAttribute("errorKey", "harvester.type.notfound");
            modelMap.addAttribute("harvester", new IBusHarvesterCommandObject());
        } else {
            modelMap.addAttribute("harvester", session.getAttribute("harvester"));
        }
        return "/edit_ibus_harvester";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.POST)
    public String step1Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (WebUtils.hasSubmitParameter(request, "back")) {
            return "redirect:" + ManageHarvesterController.TEMPLATE_LIST_HARVESTER;
        }

        if (_validatorStep1.validate(errors).hasErrors()) {
            return "/edit_ibus_harvester";
        }

        // transform to absolute path
        harvester.setWorkingDirectory((new File(harvester.getWorkingDirectory())).getAbsolutePath());
        RecordCacheConfiguration rcc = new RecordCacheConfiguration();
        rcc.setCachePath(new File(harvester.getWorkingDirectory(), "records").getAbsoluteFile());
        harvester.setCacheConfiguration(rcc);

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
    public String step2Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (WebUtils.hasSubmitParameter(request, "back")) {
            return "redirect:" + TEMPLATE_EDIT_HARVESTER;
        }

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

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_3, method = RequestMethod.GET)
    public String step3Get(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvesterParam, final Errors errors,
            @RequestParam(value = "id", required = false) final Integer id)
            throws Exception {

        IBusHarvesterCommandObject harvester = harvesterParam; 
        
        // setup the IBus client
        File file = new File(harvester.getCommunicationXml());
        BusClient client = null;
        try {
            client = BusClientFactory.createBusClient(file);
            // lock iBus client so it is not closed by accident
            IBusClosableLock.INSTANCE.lock(this.getClass().getName());
            if (!client.allConnected()) {
                client.start();
            }
            IBus bus = client.getNonCacheableIBus();
            PlugDescription[] allPlugDescriptions = bus.getAllIPlugs();

            String[] allowedDatatypes = ApplicationProperties.get(ConfigurationKeys.HARVESTER_IBUS_DATATYPES_ALLOW, "")
                    .split(",");
            String[] deniedDatatypes = ApplicationProperties.get(ConfigurationKeys.HARVESTER_IBUS_DATATYPES_DENY, "")
                    .split(",");

            List<PlugDescription> filteredDatatypesList = new ArrayList<PlugDescription>();

            for (PlugDescription pd : allPlugDescriptions) {
                if (filteredDatatypesList.contains(pd)) {
                    continue;
                }
                boolean deny = false;
                for (String deniedDatatype : deniedDatatypes) {
                    if (deniedDatatype.length() > 0 && pd.containsDataType(deniedDatatype)) {
                        deny = true;
                        break;
                    }
                }
                if (deny) {
                    continue;
                }
                if (allowedDatatypes.length > 0) {
                    for (String allowedDatatype : allowedDatatypes) {
                        if (allowedDatatype.length() > 0 && pd.containsDataType(allowedDatatype)) {
                            filteredDatatypesList.add(pd);
                            break;
                        }
                    }
                } else {
                    filteredDatatypesList.add(pd);
                }
            }

            session.setAttribute("allPlugDescriptions", filteredDatatypesList);

            List<PlugDescription> availableIPlugs = new ArrayList<PlugDescription>();
            List<RequestDefinitionCommandObject> enabledIPlugs = new ArrayList<RequestDefinitionCommandObject>();

            if (harvester.getRequestDefinitions() == null) {
                // just copy all plugs
                availableIPlugs = filteredDatatypesList;
            } else {
                for (PlugDescription pd : filteredDatatypesList) {
                    boolean isEnabled = false;
                    for (RequestDefinition rd : harvester.getRequestDefinitions()) {
                        if (rd.getPlugId() != null && rd.getPlugId().equalsIgnoreCase(pd.getPlugId())) {
                            isEnabled = true;
                            RequestDefinitionCommandObject rdco = new RequestDefinitionCommandObject(rd);
                            rdco.setDataSourceName(pd.getDataSourceName());
                            rdco.setIsCurrentlyRegistered(true);
                            String q = IPLUG_QUERY.replaceAll("PATTERN_PLUG_ID", rdco.getPlugId());
                            Document queryDocument = df.newDocumentBuilder()
                                    .parse(new InputSource(new StringReader(q)));
                            try {
                                CSWRecordResults results = searcher.search((new XMLEncoding()).getQuery(queryDocument
                                        .getDocumentElement()));
                                rdco.setIndexedRecords(results.getTotalHits());
                            } catch (Exception e) {
                                log.error("Error, searching index. Rebuild Index!", e);
                            }
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
                        if (rdco.getPlugId().equalsIgnoreCase(rd.getPlugId())) {
                            isCurrentlyRegistered = true;
                            break;
                        }
                    }
                    if (!isCurrentlyRegistered) {
                        RequestDefinitionCommandObject rdco = new RequestDefinitionCommandObject(rd);
                        rdco.setIsCurrentlyRegistered(false);
                        String q = IPLUG_QUERY.replaceAll("PATTERN_PLUG_ID", rdco.getPlugId());
                        Document queryDocument = df.newDocumentBuilder().parse(new InputSource(new StringReader(q)));
                        CSWRecordResults results = searcher.search((new XMLEncoding()).getQuery(queryDocument
                                .getDocumentElement()));
                        rdco.setIndexedRecords(results.getTotalHits());
                        enabledIPlugs.add(rdco);
                    }
                }
            }

            modelMap.addAttribute("enabledIPlugs", enabledIPlugs);
            modelMap.addAttribute("availableIPlugs", availableIPlugs);

        } catch (Exception e) {
            log.error("Error accessing iPlugs.", e);

        } finally {
            if (client != null && IBusClosableLock.INSTANCE.isLockedBy(this.getClass().getName())) {
                client.shutdown();
                // unlock iBus client for close
                IBusClosableLock.INSTANCE.unlock();
            }
        }

        return "/edit_ibus_harvester_3";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_3, method = RequestMethod.POST)
    public String step3Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors,
            @RequestParam(value = "enable", required = false) final String enable,
            @RequestParam(value = "disable", required = false) final String disable,
            @RequestParam(value = "edit", required = false) final String edit) throws Exception {

        if (enable != null && enable.length() > 0) {
            RequestDefinition rd = new RequestDefinition();
            rd.setPlugId(enable);
            rd.setQueryString("iplugs:\"" + enable + "\" ranking:score");
            if (harvester.getRequestDefinitions() == null) {
                harvester.setRequestDefinitions(new ArrayList<RequestDefinition>());
            }
            harvester.getRequestDefinitions().add(rd);

            updateAndSaveConfiguration((HarvesterConfiguration) harvester);

        } else if (disable != null && disable.length() > 0) {
            int idx = -1;
            for (RequestDefinition rd : harvester.getRequestDefinitions()) {
                if (rd.getPlugId().equals(disable)) {
                    idx = harvester.getRequestDefinitions().indexOf(rd);
                    break;
                }
            }
            if (idx > -1) {
                harvester.getRequestDefinitions().remove(idx);
            }
            updateAndSaveConfiguration((HarvesterConfiguration) harvester);
            indexManager.removeDocumentsByQuery("iplug:\"" + disable + "\"");

        } else if (edit != null && edit.length() > 0) {
            return "redirect:" + EditIBusHarvesterController.TEMPLATE_EDIT_HARVESTER_4 + "?plugid=" + edit;
        } else if (WebUtils.hasSubmitParameter(request, "back")) {
            return "redirect:" + TEMPLATE_EDIT_HARVESTER_2;
        }

        return "redirect:" + TEMPLATE_EDIT_HARVESTER_3;
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_4, method = RequestMethod.GET)
    public String step4Get(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester, final Errors errors,
            @RequestParam("plugid") final String plugId) throws Exception {

        for (RequestDefinition rd : harvester.getRequestDefinitions()) {
            if (rd.getPlugId().equals(plugId)) {
                modelMap.addAttribute("rd", rd);
                return "/edit_ibus_harvester_4";
            }
        }

        return "/edit_ibus_harvester_3";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER_4, method = RequestMethod.POST)
    public String step4Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final IBusHarvesterCommandObject harvester,
            @ModelAttribute("rd") final RequestDefinition rd, final Errors errors) throws Exception {

        if (WebUtils.hasSubmitParameter(request, "back")) {
            return "redirect:" + TEMPLATE_EDIT_HARVESTER_3;
        }

        if (_validatorStep4.validate(errors).hasErrors()) {
            return "/edit_ibus_harvester_4";
        }

        for (RequestDefinition def : harvester.getRequestDefinitions()) {
            if (def.getPlugId().equals(rd.getPlugId())) {
                BeanUtils.copyProperties(rd, def);
                break;
            }
        }

        updateAndSaveConfiguration((HarvesterConfiguration) harvester);

        return "redirect:" + TEMPLATE_EDIT_HARVESTER_3;
    }

    private void updateAndSaveConfiguration(HarvesterConfiguration harvester) throws IOException {
        Configuration configuration = cProvider.getConfiguration();
        List<HarvesterConfiguration> hConfigs = configuration.getHarvesterConfigurations();
        hConfigs.set(((Identificable) harvester).getId(), harvester);
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