/*
 * **************************************************-
 * ingrid-interface-csw
 * ==================================================
 * Copyright (C) 2014 - 2025 wemove digital solutions GmbH
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

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    public LoginController() {
    }

    @ModelAttribute("securityEnabled")
    public Boolean injectAuthenticate() {
        return true;
    }

    @RequestMapping(value = "/login.html", method = RequestMethod.GET)
    public String login(final Model model, final HttpSession session) {
        return "/login";
    }

    @RequestMapping(value = "/loginFailure.html", method = RequestMethod.GET)
    public String loginFailure() {
        return "/loginFailure";
    }

    @RequestMapping(value = "/roleFailure.html", method = RequestMethod.GET)
    public String roleFailure() {
        return "/roleFailure";
    }

    @RequestMapping(value = "/logout.html", method = RequestMethod.GET)
    public String logout(final HttpSession session) {
        session.invalidate();
        return "redirect:/welcome.html";
    }

}
