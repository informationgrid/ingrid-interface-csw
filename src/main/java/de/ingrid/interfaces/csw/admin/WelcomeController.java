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