package de.ingrid.interfaces.csw.admin;

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
import de.ingrid.interfaces.csw.config.model.HarvesterTypes;
import de.ingrid.interfaces.csw.config.model.impl.IBusHarvesterConfiguration;

@Controller
public class ManageHarvesterController {

    public static final String TEMPLATE_LIST_HARVESTER = "/list_harvester.html";
    public static final String TEMPLATE_EDIT_HARVESTER = "/edit_harvester.html";

    ConfigurationProvider cProvider = new ConfigurationProvider();

    private final HarvesterValidator _validator;

    @Autowired
    public ManageHarvesterController(final HarvesterValidator validator) {
        _validator = validator;
    }

    @RequestMapping(value = TEMPLATE_LIST_HARVESTER, method = RequestMethod.GET)
    public String welcome(final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final HarvesterCommandObject harvester) throws Exception {

        modelMap.addAttribute("harvesterConfigs", cProvider.getConfiguration().getHarvesterConfigurations());
        modelMap.addAttribute("harvesterTypes", new String[] { HarvesterTypes.IBUS.name() });

        return "/list_harvester";
    }

    @RequestMapping(value = TEMPLATE_LIST_HARVESTER, method = RequestMethod.POST)
    public String post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final HarvesterCommandObject harvester, final Errors errors,
            @RequestParam(value = "delete", required = false) final Integer delete,
            @RequestParam(value = "edit", required = false) final Integer edit) throws Exception {
        if (WebUtils.hasSubmitParameter(request, "new")) {
            if (!_validator.validate(errors).hasErrors()) {
                if (harvester.getType().equals(HarvesterTypes.IBUS.name())) {
                    IBusHarvesterConfiguration newHarvesterConfig = new IBusHarvesterConfiguration();
                    newHarvesterConfig.setName(harvester.getName());
                    Configuration conf = cProvider.getConfiguration();
                    conf.getHarvesterConfigurations().add(newHarvesterConfig);
                    cProvider.write(conf);
                    harvester.setName("");
                    harvester.setType("");
                }
            } else {
                modelMap.addAttribute("harvesterConfigs", cProvider.getConfiguration().getHarvesterConfigurations());
                modelMap.addAttribute("harvesterTypes", new String[] { HarvesterTypes.IBUS.name() });
                return "/list_harvester";
            }
        } else if (delete != null && delete >= 0
                && delete < cProvider.getConfiguration().getHarvesterConfigurations().size()) {
            Configuration conf = cProvider.getConfiguration();
            conf.getHarvesterConfigurations().remove(delete.intValue());
            cProvider.write(conf);
        } else if (edit != null && edit >= 0 && edit < cProvider.getConfiguration().getHarvesterConfigurations().size()) {
            return "redirect:" + TEMPLATE_EDIT_HARVESTER + "?id=" + edit;
        }

        return "redirect:" + TEMPLATE_LIST_HARVESTER;
    }

}