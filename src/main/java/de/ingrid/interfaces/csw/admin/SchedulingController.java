package de.ingrid.interfaces.csw.admin;

import it.sauronsoftware.cron4j.InvalidPatternException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SchedulingController {

    final IndexScheduler _scheduler;

    public static final String TEMPLATE_SCHEDULING_URI = "/scheduling.html";
    public static final String TEMPLATE_SCHEDULING_VIEW = "/scheduling";

    public static final String TEMPLATE_DELETE_SCHEDULING_URI = "/deletePattern.html";

    @Autowired
    public SchedulingController(final IndexScheduler scheduler) {
        _scheduler = scheduler;
    }

    @RequestMapping(value = TEMPLATE_SCHEDULING_URI, method = RequestMethod.GET)
    public String getScheduling(final ModelMap modelMap) {
        modelMap.addAttribute("pattern", _scheduler.getPattern());
        return TEMPLATE_SCHEDULING_VIEW;
    }

    @RequestMapping(value = TEMPLATE_SCHEDULING_URI, method = RequestMethod.POST)
    public String postScheduling(final ModelMap modelMap,
            @RequestParam(value = "hour", required = false) final String hour,
            @RequestParam(value = "minute", required = false) final String minute,
            @RequestParam(value = "dayOfWeek", required = false) final String dayOfWeek,
            @RequestParam(value = "dayOfMonth", required = false) final String dayOfMonth,
            @RequestParam(value = "pattern", required = false) String pattern) {
        // daily: m h * * *
        // weekly: m h * * w
        // monthly: m h d * *
        // pattern: m h d M w
        if (pattern == null) {
            pattern = (minute != null ? minute : "*") + " " + (hour != null ? hour : "*") + " "
                    + (dayOfMonth != null ? dayOfMonth : "*") + " * " + (dayOfWeek != null ? dayOfWeek : "*");
        }

        try {
            _scheduler.setPattern(pattern);
        } catch (InvalidPatternException e) {
            modelMap.addAttribute("error", "Invalid pattern!");
            _scheduler.deletePattern();
            return TEMPLATE_SCHEDULING_VIEW;
        } catch (Exception e) {
            modelMap.addAttribute("error", "An error occured!");
            _scheduler.deletePattern();
            return TEMPLATE_SCHEDULING_VIEW;
        }

        return "redirect:" + TEMPLATE_SCHEDULING_URI;
    }

    @RequestMapping(value = TEMPLATE_DELETE_SCHEDULING_URI, method = RequestMethod.POST)
    public String delete(final ModelMap modelMap) {
        _scheduler.deletePattern();
        return "redirect:" + TEMPLATE_SCHEDULING_URI;
    }

}
