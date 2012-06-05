package de.ingrid.interfaces.csw.admin;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import de.ingrid.interfaces.csw.admin.command.HarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.validation.HarvesterValidator;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.IBusHarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.TestSuiteHarvesterConfiguration;
import de.ingrid.interfaces.csw.harvest.ibus.IBusHarvester;
import de.ingrid.interfaces.csw.harvest.impl.RecordCache;
import de.ingrid.interfaces.csw.harvest.impl.TestSuiteHarvester;
import de.ingrid.interfaces.csw.index.IsoIndexManager;
import de.ingrid.interfaces.csw.jobs.UpdateJob;
import de.ingrid.interfaces.csw.tools.FileUtils;

@Controller
public class ManageHarvesterController {

    public static final String TEMPLATE_LIST_HARVESTER = "/list_harvester.html";
    public static final Map<String, String> HARVESTER_TYPES = new LinkedHashMap<String, String>();

    static {
        HARVESTER_TYPES.put(IBusHarvester.class.getName(), "iBus harvester");
        HARVESTER_TYPES.put(TestSuiteHarvester.class.getName(), "GDI-DE test data harvester");
    }

    @Autowired
    ConfigurationProvider cProvider = null;
    
    @Autowired
    UpdateJob updateJob;
    
    @Autowired
    private IsoIndexManager indexManager;


    private final HarvesterValidator _validator;

    @Autowired
    public ManageHarvesterController(final HarvesterValidator validator) {
        _validator = validator;
    }

    @RequestMapping(value = TEMPLATE_LIST_HARVESTER, method = RequestMethod.GET)
    public String welcome(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final HarvesterCommandObject harvester) throws Exception {

        modelMap.addAttribute("harvesterConfigs", cProvider.reloadConfiguration().getHarvesterConfigurations());
        modelMap.addAttribute("harvesterTypes", HARVESTER_TYPES);

        return "/list_harvester";
    }

    @RequestMapping(value = TEMPLATE_LIST_HARVESTER, method = RequestMethod.POST)
    public String post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final HarvesterCommandObject harvester, final Errors errors,
            @RequestParam(value = "delete", required = false) final Integer delete,
            @RequestParam(value = "edit", required = false) final Integer edit) throws Exception {

        Configuration conf = cProvider.getConfiguration();
        List<HarvesterConfiguration> hConfigs = conf.getHarvesterConfigurations();

        if (WebUtils.hasSubmitParameter(request, "new")) {
            if (!_validator.validate(errors).hasErrors()) {
                if (harvester.getType().equals(IBusHarvester.class.getName())) {
                    IBusHarvesterConfiguration newHarvesterConfig = new IBusHarvesterConfiguration();
                    newHarvesterConfig.setName(harvester.getName());
                    hConfigs.add(newHarvesterConfig);
                    cProvider.write(conf);
                    // clear harvester model for new entries
                    harvester.setName("");
                    harvester.setType("");
                    return "redirect:" + EditIBusHarvesterController.TEMPLATE_EDIT_HARVESTER + "?id=" + (hConfigs.size() -1);
                    
                } else if (harvester.getType().equals(TestSuiteHarvester.class.getName())) {
                    TestSuiteHarvesterConfiguration newHarvesterConfig = new TestSuiteHarvesterConfiguration();
                    newHarvesterConfig.setName(harvester.getName());
                    if (newHarvesterConfig.getWorkingDirectory() == null) {
                        newHarvesterConfig.setWorkingDirectory(new File(FileUtils.encodeFileName(newHarvesterConfig.getName())).getAbsolutePath());
                    }
                    RecordCacheConfiguration rcc = new RecordCacheConfiguration();
                    rcc.setCachePath(new File(newHarvesterConfig.getWorkingDirectory(), "records").getAbsoluteFile());
                    newHarvesterConfig.setCacheConfiguration(rcc);

                    hConfigs.add(newHarvesterConfig);
                    cProvider.write(conf);
                    // clear harvester model for new entries
                    harvester.setName("");
                    harvester.setType("");
                    return "redirect:" + EditTestSuiteHarvesterController.TEMPLATE_EDIT_HARVESTER + "?id=" + (hConfigs.size() -1);
                }

            } else {
                modelMap.addAttribute("harvesterConfigs", cProvider.getConfiguration().getHarvesterConfigurations());
                modelMap.addAttribute("harvesterTypes", HARVESTER_TYPES);
                return "/list_harvester";
            }
        } else if (delete != null && delete >= 0 && delete < hConfigs.size()) {
            HarvesterConfiguration hcnf = hConfigs.get(delete.intValue());
            if (hcnf.getCacheConfiguration() != null) {
                RecordCache cacheInstance = conf.createInstance(hcnf.getCacheConfiguration());
                Set<Serializable> ids = cacheInstance.getCachedIds();
                indexManager.removeDocuments(ids);
            }
            hConfigs.remove(delete.intValue());
            cProvider.write(conf);
        } else if (edit != null && edit >= 0 && edit < hConfigs.size()) {
            HarvesterConfiguration hConfig = hConfigs.get(edit);
            if (hConfig.getClassName().equals(IBusHarvester.class.getName())) {
                return "redirect:" + EditIBusHarvesterController.TEMPLATE_EDIT_HARVESTER + "?id=" + edit;
            } else if (hConfig.getClassName().equals(TestSuiteHarvester.class.getName())) {
                return "redirect:" + EditTestSuiteHarvesterController.TEMPLATE_EDIT_HARVESTER + "?id=" + edit;
            } else {
                modelMap.addAttribute("errorKey", "harvester.type.notfound");
                modelMap.addAttribute("harvesterConfigs", hConfigs);
                modelMap.addAttribute("harvesterTypes", HARVESTER_TYPES);
                return "/list_harvester";
            }
        }

        return "redirect:" + TEMPLATE_LIST_HARVESTER;
    }

}