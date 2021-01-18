/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2021 wemove digital solutions GmbH
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
import java.nio.file.Paths;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import de.ingrid.interfaces.csw.admin.command.TestSuiteHarvesterCommandObject;
import de.ingrid.interfaces.csw.admin.validation.TestSuiteHarvesterValidator;
import de.ingrid.interfaces.csw.config.ConfigurationProvider;
import de.ingrid.interfaces.csw.config.model.Configuration;
import de.ingrid.interfaces.csw.config.model.HarvesterConfiguration;
import de.ingrid.interfaces.csw.config.model.impl.RecordCacheConfiguration;
import de.ingrid.interfaces.csw.harvest.impl.TestSuiteHarvester;
import de.ingrid.interfaces.csw.tools.FileUtils;

@Controller
@SessionAttributes("harvester")
public class EditTestSuiteHarvesterController {

    public static final String TEMPLATE_EDIT_HARVESTER = "/edit_testsuite_harvester.html";

    @Autowired
    ConfigurationProvider cProvider = null;

    @Autowired
    private final TestSuiteHarvesterValidator.TestSuiteHarvesterValidatorStep1 _validatorStep1 = null;

    final private static Log log = LogFactory.getLog(EditTestSuiteHarvesterController.class);

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.GET)
    public String step1Get(final HttpSession session, final ModelMap modelMap,
            @RequestParam(value = "id", required = false) final Integer id) throws Exception {

        if (id != null && id >= 0) {
            List<HarvesterConfiguration> hConfigs = cProvider.getConfiguration().getHarvesterConfigurations();
            HarvesterConfiguration hConfig = hConfigs.get(id);
            modelMap.addAttribute("id", id);
            if (hConfig.getClassName().equals(TestSuiteHarvester.class.getName())) {
                if (hConfig.getWorkingDirectory() == null) {
                    hConfig.setWorkingDirectory(Paths.get( cProvider.getInstancesPath().getAbsolutePath(), FileUtils.encodeFileName(hConfig.getName()) ).toAbsolutePath().toString() );
                }
                TestSuiteHarvesterCommandObject commandObject = new TestSuiteHarvesterCommandObject(hConfig);
                commandObject.setId(id);
                // put into session
                session.setAttribute("harvester", commandObject);
                modelMap.addAttribute("harvester", commandObject);
            }
        } else if (session.getAttribute("harvester") == null) {
            modelMap.addAttribute("errorKey", "harvester.type.notfound");
            modelMap.addAttribute("harvester", new TestSuiteHarvesterCommandObject());
        } else {
            modelMap.addAttribute("harvester", session.getAttribute("harvester"));
        }
        return "/edit_testsuite_harvester";
    }

    @RequestMapping(value = TEMPLATE_EDIT_HARVESTER, method = RequestMethod.POST)
    public String step1Post(final HttpServletRequest request, final HttpSession session, final ModelMap modelMap,
            @ModelAttribute("harvester") final TestSuiteHarvesterCommandObject harvester, final Errors errors)
            throws Exception {

        if (WebUtils.hasSubmitParameter(request, "back")) {
            return "redirect:" + ManageHarvesterController.TEMPLATE_LIST_HARVESTER;
        }

        if (_validatorStep1.validate(errors).hasErrors()) {
            return "/edit_testsuite_harvester";
        }

        // transform to absolute path
        harvester.setWorkingDirectory((new File(harvester.getWorkingDirectory())).getAbsolutePath());
        RecordCacheConfiguration rcc = new RecordCacheConfiguration();
        rcc.setCachePath(new File(harvester.getWorkingDirectory(), "records").getAbsoluteFile());
        harvester.setCacheConfiguration(rcc);
        
        Configuration configuration = cProvider.getConfiguration();
        List<HarvesterConfiguration> hConfigs = configuration.getHarvesterConfigurations();
        hConfigs.set(harvester.getId(), (HarvesterConfiguration)harvester);
        if (log.isDebugEnabled()) {
            log.debug("Save configuration to: " + cProvider.getConfigurationFile());
        }
        cProvider.write(configuration);
        

        return "/edit_testsuite_harvester";

    }

}
