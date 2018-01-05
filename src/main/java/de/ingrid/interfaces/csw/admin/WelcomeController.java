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
package de.ingrid.interfaces.csw.admin;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import de.ingrid.interfaces.csw.admin.command.Command;

@Controller
@SessionAttributes( { "postCommandObject" })
public class WelcomeController {

    public WelcomeController() {
    }

    @RequestMapping(value = "/welcome.html", method = RequestMethod.GET)
    public String welcome(final HttpSession session) throws Exception {
        if (session.getAttribute("postCommandObject") == null) {
            session.setAttribute("postCommandObject", new Command());
        }
        return "redirect:" + ManageHarvesterController.TEMPLATE_LIST_HARVESTER;
    }
}
