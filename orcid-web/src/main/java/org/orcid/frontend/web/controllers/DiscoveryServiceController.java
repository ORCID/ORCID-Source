package org.orcid.frontend.web.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping("/disco")
public class DiscoveryServiceController extends BaseController {

    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean enabled;

    @RequestMapping
    public String discoveryServiceHome() {
        return "disco";
    }

}
