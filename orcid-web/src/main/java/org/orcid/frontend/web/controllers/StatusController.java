package org.orcid.frontend.web.controllers;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.InternalSSOManager;
import org.orcid.core.manager.StatusManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
public class StatusController {
    
    
    private static final Logger LOGGER = LoggerFactory.getLogger(StatusController.class);

    @Resource
    private LocaleManager localeManager;
    
    @Resource
    private InternalSSOManager internalSSOManager;
    
    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private StatusManager statusManager;
    
    @RequestMapping(value = "/tomcatUp.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public @ResponseBody
    String tomcatUp(HttpServletRequest request) {
        request.setAttribute("isMonitoring", true);
        return "{tomcatUp:true}";
    }
    
    @RequestMapping(value = "/webStatus.json")
    @Produces(value = { MediaType.APPLICATION_JSON })
    public ResponseEntity<Map<String, Boolean>> webStatus(HttpServletRequest request) {
        request.setAttribute("isMonitoring", true);
        Map<String, Boolean> statusMap = statusManager.createStatusMap();
        HttpStatus responseStatus = statusMap.get(StatusManager.OVERALL_OK) ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(responseStatus).body(statusMap);
    }

}
