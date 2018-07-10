package org.orcid.frontend.web.controllers;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller("unsubscribeController")
@RequestMapping(value = { "/unsubscribe" })
public class UnsubscribeController {

    @RequestMapping(value = "/preferences.json", method = RequestMethod.GET)
    public Map<String, String> getPreferences() {
        
    }
}
