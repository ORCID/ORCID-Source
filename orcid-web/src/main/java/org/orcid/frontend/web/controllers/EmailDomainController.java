package org.orcid.frontend.web.controllers;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.core.common.manager.EmailDomainManager;
import org.orcid.persistence.jpa.entities.EmailDomainEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("emailDomainController")
@RequestMapping(value = { "/email-domain" })
public class EmailDomainController {
    
    @Resource
    private EmailDomainManager emailDomainManager;
    
    @RequestMapping(value = "/find-category", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public @ResponseBody String findCategory(@RequestParam("domain") String domain) {
        
        if(domain == null || domain.length() > 64) {
            return "{'error':'domain lenght too long or invalid'}";
        }        
                
        EmailDomainEntity ede = emailDomainManager.findByEmailDoman(domain);
        if(ede == null) {
            return "{'category':'" + EmailDomainEntity.DomainCategory.UNDEFINED.name() + "'}";
        } else {
            return "{'category':'" + ede.getCategory().name() + "'}";
        }
    }
}
