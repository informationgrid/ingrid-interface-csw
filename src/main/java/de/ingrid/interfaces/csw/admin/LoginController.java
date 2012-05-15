package de.ingrid.interfaces.csw.admin;

import javax.servlet.http.HttpSession;

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
