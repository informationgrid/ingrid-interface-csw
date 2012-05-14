package de.ingrid.interfaces.csw.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class IndexController {

    public static final String TEMPLATE_INDEXING_URI = "/indexing.html";
    public static final String TEMPLATE_INDEXING_VIEW = "/indexing";

    public static final String TEMPLATE_INDEX_STATE_URI = "/indexState.html";
    public static final String TEMPLATE_INDEX_STATE_VIEW = "/indexState";

    final IndexScheduler _scheduler;

    @Autowired
    public IndexController(final IndexScheduler scheduler) {
        _scheduler = scheduler;
    }

    @ModelAttribute("isScheduled")
    public Boolean injectState() {
        return _scheduler.isRunning();
    }

    @RequestMapping(value = TEMPLATE_INDEXING_URI, method = RequestMethod.GET)
    public String getIndexing(ModelMap model) {
        return TEMPLATE_INDEXING_VIEW;
    }

    @RequestMapping(value = TEMPLATE_INDEXING_URI, method = RequestMethod.POST)
    public String postIndexing(final ModelMap model) throws Exception {
        if (_scheduler.triggerManually()) {
            model.addAttribute("triggerResult", "success");
        } else {
            model.addAttribute("triggerResult", "error");
        }
        return TEMPLATE_INDEXING_VIEW;

    }

    @RequestMapping(value = TEMPLATE_INDEX_STATE_URI, method = RequestMethod.GET)
    public String getIndexState(ModelMap model) {
        model.addAttribute("isRunning", _scheduler.isRunning());
        return TEMPLATE_INDEX_STATE_VIEW;
    }
}
