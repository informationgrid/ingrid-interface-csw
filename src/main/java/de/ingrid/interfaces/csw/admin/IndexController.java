package de.ingrid.interfaces.csw.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import de.ingrid.interfaces.csw.index.StatusProvider;

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

    @Autowired
    private StatusProvider statusProvider;

    @ModelAttribute("isScheduled")
    public Boolean injectState() {
        return _scheduler.isRunning();
    }

    @RequestMapping(value = TEMPLATE_INDEXING_URI, method = RequestMethod.GET)
    public String getIndexing(ModelMap model) {
        if (_scheduler.isRunning()) {
            model.addAttribute("triggerResult", "success");
        }
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
    public @ResponseBody
    StatusResponse getIndexState(ModelMap model) {
        return new StatusResponse(_scheduler.isRunning(), statusProvider.toString());
    }

    private class StatusResponse {

        private Boolean isRunning;
        private String status;

        public StatusResponse(Boolean isRunning, String status) {
            this.setIsRunning(isRunning);
            this.setStatus(status);
        }

        public void setStatus(String status) {
            this.status = status;
        }

        @SuppressWarnings("unused")
        public String getStatus() {
            return status;
        }

        public void setIsRunning(Boolean isRunning) {
            this.isRunning = isRunning;
        }

        @SuppressWarnings("unused")
        public Boolean getIsRunning() {
            return isRunning;
        }

    }

    /*
     * @RequestMapping(value = TEMPLATE_INDEX_STATE_URI, method =
     * RequestMethod.GET) public String getIndexState(ModelMap model) {
     * model.addAttribute("isRunning", _scheduler.isRunning());
     * model.addAttribute("status", statusProvider.toString()); return
     * TEMPLATE_INDEX_STATE_VIEW; }
     */
}
